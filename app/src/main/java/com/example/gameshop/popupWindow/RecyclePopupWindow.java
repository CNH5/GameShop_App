package com.example.gameshop.popupWindow;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.gameshop.R;
import com.example.gameshop.config.URL;
import com.example.gameshop.pojo.Game;
import com.example.gameshop.toast.ImageTextToast;
import com.example.gameshop.utils.RequestUtil;
import com.example.gameshop.utils.ResponseUtil;
import okhttp3.Response;

import java.io.IOException;

/**
 * @author sheng
 * @date 2021/12/8 6:20
 */
public class RecyclePopupWindow extends PopupWindow implements View.OnClickListener {
    private static final String TAG = "recycle_pack_game_add";
    private final Context mContext;
    private final Game game;
    private final View v;
    private int num = 1;
    private boolean isRecycle = true;
    private View recycle_switch_bt;
    private View buy_switch_bt;
    private TextView num_view;


    @SuppressLint("InflateParams")
    public RecyclePopupWindow(Context mContext, Game game) {
        this.mContext = mContext;
        this.game = game;
        v = LayoutInflater.from(mContext).inflate(R.layout.recycle_pack_add_view, null);

        initParams();
        initData();
        initWindow();
    }

    private void initParams() {
        buy_switch_bt = v.findViewById(R.id.buy_switch);
        recycle_switch_bt = v.findViewById(R.id.recycle_switch);
        num_view = v.findViewById(R.id.num);

        buy_switch_bt.setOnClickListener(this);
        recycle_switch_bt.setOnClickListener(this);
        v.findViewById(R.id.num_plus_bt).setOnClickListener(this);
        v.findViewById(R.id.num_reduce_bt).setOnClickListener(this);
        v.findViewById(R.id.confirm_bt).setOnClickListener(this);
        v.findViewById(R.id.close_bt).setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        ((TextView) v.findViewById(R.id.price)).setText("￥" + game.getPrice());
        Glide.with(mContext).load(URL.IMAGE + game.getCover_image()).into((ImageView) v.findViewById(R.id.cover_image));
    }

    private void setSwitch() {
        TextView tips_text_view = v.findViewById(R.id.tips_text);
        TextView recycle_switch_text_view = v.findViewById(R.id.recycle_switch_text);
        TextView buy_switch_text_view = v.findViewById(R.id.buy_switch_text);

        if (isRecycle) {
            buy_switch_text_view.setTextColor(Color.parseColor("#909399"));
            buy_switch_bt.setBackgroundColor(Color.WHITE);

            recycle_switch_text_view.setTextColor(Color.WHITE);
            recycle_switch_bt.setBackgroundResource(R.drawable.shape_corner6);
            tips_text_view.setText("添加到：回收");
        } else {
            recycle_switch_text_view.setTextColor(Color.parseColor("#909399"));
            recycle_switch_bt.setBackgroundColor(Color.WHITE);

            buy_switch_text_view.setTextColor(Color.WHITE);
            buy_switch_bt.setBackgroundResource(R.drawable.shape_corner6);
            tips_text_view.setText("添加到：购买");
        }
    }

    // 设置背景
    public void setBackground() {
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
        lp.alpha = 0.6f;
        ((Activity) mContext).getWindow().setAttributes(lp);
    }

    private void initWindow() {
        // 设置View
        setContentView(v);
        //设置宽与高
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置弹出窗体可点击
        setTouchable(true);
        // 设置窗口可以获取焦点
        setFocusable(true);
        // 设置进出动画
        setAnimationStyle(R.style.bottom_up_window);
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.recycle_switch) {
            if (!isRecycle) {
                isRecycle = true;
                setSwitch();
            }
        } else if (vid == R.id.buy_switch) {
            if (isRecycle) {
                isRecycle = false;
                setSwitch();
            }
        } else if (vid == R.id.num_plus_bt) {
            num_view.setText(String.valueOf(++num));

        } else if (vid == R.id.num_reduce_bt) {
            if (num == 1) {
                new ImageTextToast(mContext).fail("不能更少了哦~");
            } else {
                num_view.setText(String.valueOf(--num));
            }
        } else if (vid == R.id.confirm_bt) {
            confirm();
        } else if (vid == R.id.close_bt) {
            dismiss();
        }
    }

    private void confirm() {
        new RequestUtil(mContext)
                .url(URL.ADD_PACK)
                .post()
                .setToken()
                .addFormParameter("id", game.getId())
                .addFormParameter("num", num)
                .addFormParameter("type", isRecycle ? "回收" : "出售")
                .then((call, response) -> {
                    onResponseSuccess(response);
                })
                .error((call, e) -> {
                    e.printStackTrace();
                    new ImageTextToast(mContext).error("网络异常");
                })
                .request();
    }

    private void onResponseSuccess(Response response) throws IOException {
        new ResponseUtil(response)
                .success((msg, dataJSON) -> {
                    Log.d(TAG, msg);
                })
                .fail((msg, dataJSON) -> {
                    Log.w(TAG, msg);
                })
                .error((msg, dataJSON) -> {
                    Log.e(TAG, msg);
                })
                .handle();
    }


    @Override
    public void dismiss() {
        super.dismiss();

        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
        lp.alpha = 1.0f;
        ((Activity) mContext).getWindow().setAttributes(lp);
    }
}
