package com.example.gameshop.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.gameshop.R;
import com.example.gameshop.config.URL;
import com.example.gameshop.pojo.RecyclePackGame;
import com.example.gameshop.toast.ImageTextToast;
import com.example.gameshop.utils.RequestUtil;
import com.example.gameshop.utils.ResponseUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sheng
 * @date 2021/11/22 5:13
 */
public class PackGameAdapter extends RecyclerView.Adapter<PackGameAdapter.ViewHolder> implements View.OnClickListener {
    private static final String TAG = "PackGameAdapter";
    private final List<RecyclePackGame> games;
    private final Context mContext;
    private OnGameClick onGameClick;
    private final ImageTextToast toast;
    // 选中游戏的请求
    private final RequestUtil selectedGameRequest;
    // 处理选中游戏的响应头
    private final ResponseUtil selectedGameResponse;
    // 数量修改的请求
    private final RequestUtil numUpdateRequest;
    private static final String NUM_PLUS_SIGNAL = "numPlus";
    private static final String NUM_REDUCE_SIGNAL = "numReduce";

    public void setOnGameClick(OnGameClick onGameClick) {
        this.onGameClick = onGameClick;
    }

    public PackGameAdapter(Context mContext, List<RecyclePackGame> games, String type, ImageTextToast toast) {
        this.games = games;
        this.mContext = mContext;
        this.toast = toast;

        selectedGameRequest = new RequestUtil(mContext)
                .url(URL.RECYCLE_PACK_SELECTED_GAME).post().setToken().addFormParameter("type", type);
        // TODO:完善选中游戏之后的操作
        selectedGameResponse = new ResponseUtil()
                .success((msg, dataJSON) -> {

                })
                .fail((msg, dataJSON) -> {

                })
                .error((msg, dataJSON) -> {

                });

        numUpdateRequest = new RequestUtil(mContext)
                .url(URL.RECYCLE_PACK_UPDATE_NUM).post().setToken().addFormParameter("type", type);
    }

    public interface OnGameClick {
        void onClick(long gameId);
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        int position = (int) v.getTag();
        if (vid == R.id.selected) {
            // 点击选中按钮
            selectedGameRequest
                    .addFormParameter("idList", Collections.singletonList(games.get(position).getId()))
                    .then((call, response) -> {
                        selectedGameResponse.setResponse(response).handle();
                    })
                    .error((call, e) -> {
                        e.printStackTrace();

                        toast.fail("网络异常");
                    })
                    .request();

        } else if (vid == R.id.num_plus_bt) {
            // 数量增加按钮
            numUpdate(position, NUM_PLUS_SIGNAL, NUM_REDUCE_SIGNAL);

        } else if (vid == R.id.num_reduce_bt) {
            // 减少按钮
            if (games.get(position).getNum() == 1) {
                new ImageTextToast(mContext).fail("不能更少了哦~");

            } else {
                numUpdate(position, NUM_REDUCE_SIGNAL, NUM_PLUS_SIGNAL);
            }

        } else {
            // 点击整个按钮
            if (onGameClick != null) {
                onGameClick.onClick(games.get(position).getId());
            }
        }
    }

    // 修改游戏数量
    private void numUpdate(int position, String signal, String failSignal) {
        // 修改数量的View
        notifyItemChanged(position, signal);
        Map<String, Object> num = new HashMap<>();
        num.put("id", games.get(position).getId());
        num.put("num", games.get(position).getNum());

        numUpdateRequest
                .addFormParameter("numList", num)
                .then((call, response) -> {
                    new ResponseUtil()
                            .setResponse(response)
                            .success((msg, dataJSON) -> {
                                toast.success(msg);
                            })
                            .fail((msg, dataJSON) -> {
                                toast.fail(msg);
                                Log.w(TAG, msg);
                            })
                            .error((msg, dataJSON) -> {
                                toast.error(msg);
                                Log.e(TAG, msg);
                            })
                            .afterNotSuccess(() -> {
                                changeNum(position, failSignal);
                            })
                            .handle();
                })
                .error((call, e) -> {
                    changeNum(position, failSignal);

                    Looper.prepare();
                    new ImageTextToast(mContext).error("网络异常");
                    Looper.loop();
                })
                .request();
    }

    private void changeNum(int position, String signal) {
        ((Activity) mContext).runOnUiThread(() -> {
            notifyItemChanged(position, signal);
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recycle_pack_game, parent, false));
    }

    @Override
    @SuppressLint("SetTextI18n")
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecyclePackGame game = games.get(position);
        // 加载游戏封面图片
        Glide.with(mContext).load(URL.IMAGE + (game.getCover_image() == null ? "notfound.jpg" : game.getCover_image())).into(holder.coverImage);
        // 设置选中状态
        holder.selectedBt.setImageResource(game.isSelected() ? R.mipmap.selected : R.mipmap.unselected);
        // 设置游戏名
        holder.gameNameView.setText(game.getName());
        // 显示游戏平台
        holder.platformNameView.setText(game.getPlatform());
        // 设置图标样式
        if ("NS".equals(game.getPlatform())) {
            holder.platformNameView.setTextColor(Color.parseColor("#EF2020"));
            holder.platform.setBackground(mContext.getDrawable(R.drawable.shape_corner12));

        } else {
            holder.platformNameView.setTextColor(Color.parseColor("#409EFF"));
            holder.platform.setBackground(mContext.getDrawable(R.drawable.shape_corner10));
        }
        // 设置价格
        holder.gamePriceView.setText("￥" + game.getNow_price());
        // 设置回收袋中的游戏数量
        holder.numTextView.setText(game.getNum());
        holder.setItemTag(position);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (getItemViewType(position) == 0) {
            if (payloads.isEmpty()) {
                onBindViewHolder(holder, position);

            } else {
                RecyclePackGame game = games.get(position);

                if (NUM_PLUS_SIGNAL.equals(payloads.get(0))) {
                    // 点击增加按钮
                    if (game.getNum() == 1) {
                        holder.numReduceBt.setBackground(mContext.getDrawable(R.drawable.blue_bt_background_2));
                    }
                    game.setNum(game.getNum() + 1);
                    holder.numTextView.setText(game.getNum());

                } else if (NUM_REDUCE_SIGNAL.equals(payloads.get(0))) {
                    // 点击减少按钮
                    game.setNum(game.getNum() - 1);
                    holder.numTextView.setText(game.getNum());

                    if (game.getNum() == 1) {
                        holder.numReduceBt.setBackground(mContext.getDrawable(R.drawable.blue_bt_background_3));
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        ImageView selectedBt;
        ImageView coverImage;
        TextView gameNameView;
        View platform;
        TextView platformNameView;
        TextView gamePriceView;
        View numReduceBt;
        View NumPlusBt;
        TextView numTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            selectedBt = itemView.findViewById(R.id.selected);
            coverImage = itemView.findViewById(R.id.cover_image);
            gameNameView = itemView.findViewById(R.id.game_name);
            platform = itemView.findViewById(R.id.platform);
            platformNameView = itemView.findViewById(R.id.platform_text);
            gamePriceView = itemView.findViewById(R.id.game_price);
            numReduceBt = itemView.findViewById(R.id.num_reduce_bt);
            NumPlusBt = itemView.findViewById(R.id.num_plus_bt);
            numTextView = itemView.findViewById(R.id.num);
            // 设置监听
            selectedBt.setOnClickListener(PackGameAdapter.this);
            numReduceBt.setOnClickListener(PackGameAdapter.this);
            NumPlusBt.setOnClickListener(PackGameAdapter.this);
            itemView.setOnClickListener(PackGameAdapter.this);
        }

        public void setItemTag(int tag) {
            itemView.setTag(tag);
        }
    }
}
