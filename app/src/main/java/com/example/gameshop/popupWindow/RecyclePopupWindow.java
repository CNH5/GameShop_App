package com.example.gameshop.popupWindow;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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

/**
 * @author sheng
 * @date 2021/12/8 6:20
 */
public class RecyclePopupWindow extends PopupWindow implements View.OnClickListener {
    private final Context mContext;
    private final Game game;
    private final View v;
    private int num = 1;
    private boolean isRecycle = true;
    private View recycleSwitchBt;
    private View buySwitchBt;
    private View numReduceBt;
    private TextView numView;
    private TextView switchDesc;
    private TextView confirmBtText;
    private TextView tipsTextView;
    private TextView recycleSwitchTextView;
    private TextView buySwitchTextView;
    private TextView expectPriceView;
    private View recycleTipsView;
    private OnSubmit onSubmit;
    private String buyTipText;
    private String recycleTipText;
    private boolean transaction = false;

    public RecyclePopupWindow setBuyTipText(String buyTipText) {
        this.buyTipText = buyTipText;
        if (!isRecycle) {
            tipsTextView.setText(buyTipText);
        }
        return this;
    }

    public RecyclePopupWindow setRecycleTipText(String recycleTipText) {
        this.recycleTipText = recycleTipText;
        if (isRecycle) {
            tipsTextView.setText(recycleTipText);
        }
        return this;
    }

    public RecyclePopupWindow confirmBtText(String text) {
        this.confirmBtText.setText(text);
        return this;
    }

    public RecyclePopupWindow transaction(boolean transaction) {
        this.transaction = transaction;
        expectPriceView.setText(String.valueOf((game.getPrice() - 15)));
        recycleTipsView.setVisibility(View.VISIBLE);
        return this;
    }

    // 设置类型的描述文本
    public RecyclePopupWindow setSwitchDescText(String text) {
        this.switchDesc.setText(text);
        return this;
    }

    public interface OnSubmit {
        void submit(String type, int num);
    }

    public void setOnSubmit(OnSubmit onSubmit) {
        this.onSubmit = onSubmit;
    }


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
        buySwitchBt = v.findViewById(R.id.buy_switch);
        recycleSwitchBt = v.findViewById(R.id.recycle_switch);
        numView = v.findViewById(R.id.num);
        switchDesc = v.findViewById(R.id.switch_desc);
        confirmBtText = v.findViewById(R.id.confirm_bt_text);
        tipsTextView = v.findViewById(R.id.tips_text);
        recycleSwitchTextView = v.findViewById(R.id.recycle_switch_text);
        buySwitchTextView = v.findViewById(R.id.buy_switch_text);
        recycleTipsView = v.findViewById(R.id.recycle_tips);
        expectPriceView = v.findViewById(R.id.expect_price);
        numReduceBt = v.findViewById(R.id.num_reduce_bt);

        buySwitchBt.setOnClickListener(this);
        recycleSwitchBt.setOnClickListener(this);
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
        if (isRecycle) {
            buySwitchTextView.setTextColor(Color.parseColor("#909399"));
            buySwitchBt.setBackgroundColor(Color.WHITE);

            recycleSwitchTextView.setTextColor(Color.WHITE);
            recycleSwitchBt.setBackgroundResource(R.drawable.shape_corner6);
            tipsTextView.setText(recycleTipText);

            if (transaction) {
                recycleTipsView.setVisibility(View.VISIBLE);
            }
        } else {
            recycleSwitchTextView.setTextColor(Color.parseColor("#909399"));
            recycleSwitchBt.setBackgroundColor(Color.WHITE);

            buySwitchTextView.setTextColor(Color.WHITE);
            buySwitchBt.setBackgroundResource(R.drawable.shape_corner6);
            tipsTextView.setText(buyTipText);

            if (transaction) {
                recycleTipsView.setVisibility(View.INVISIBLE);
            }
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
        // 设置开关的状态
        setSwitch();
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
            if (num == 1) {
                numReduceBt.setBackground(mContext.getDrawable(R.drawable.blue_bt_background_2));
            }
            numView.setText(String.valueOf(++num));

            if (transaction && isRecycle) {
                expectPriceView.setText(String.valueOf(num * (game.getPrice() - 15)));
            }
        } else if (vid == R.id.num_reduce_bt) {
            if (num == 1) {
                new ImageTextToast(mContext).fail("不能更少了哦~");

            } else {
                num--;
                if (num == 1) {
                    numReduceBt.setBackground(mContext.getDrawable(R.drawable.blue_bt_background_3));
                }
                numView.setText(String.valueOf(num));
                if (transaction && isRecycle) {
                    expectPriceView.setText(String.valueOf(num * (game.getPrice() - 15)));
                }
            }
        } else if (vid == R.id.confirm_bt) {
            if (onSubmit != null) {
                onSubmit.submit(isRecycle ? "回收" : "购买", num);
            }
        } else if (vid == R.id.close_bt) {
            dismiss();
        }
    }


    @Override
    public void dismiss() {
        super.dismiss();

        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
        lp.alpha = 1.0f;
        ((Activity) mContext).getWindow().setAttributes(lp);
    }
}
