package com.example.gameshop.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.example.gameshop.R;
import com.example.gameshop.config.URL;
import com.example.gameshop.pojo.RecyclePackGame;
import com.example.gameshop.toast.ImageTextToast;
import com.example.gameshop.utils.RequestUtil;
import com.example.gameshop.utils.CallUtil;
import com.example.gameshop.utils.SharedDataUtil;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;

import java.util.*;


/**
 * @author sheng
 * @date 2021/11/22 5:13
 */
public class PackGameAdapter extends RecyclerView.Adapter<PackGameAdapter.ViewHolder> implements View.OnClickListener {
    private static final String TAG = "PackGameAdapter";
    private final List<RecyclePackGame> games;
    private final Context mContext;
    private final String type;
    private OnGameClick onGameClick;
    private List<RecyclePackGame> selectedGames = new ArrayList<>();
    private final List<Integer> selectedPosition = new ArrayList<>();
    private final ImageTextToast toast;
    private static final String NUM_PLUS_SIGNAL = "numPlus";
    private static final String NUM_REDUCE_SIGNAL = "numReduce";
    private static final String GAME_SELECTED_SIGNAL = "gameSelected";
    private OnSelectedGamesChange onSelectedGamesChange;
    private final SharedDataUtil util;

    //  删除数据
    public void removeSelected() {
        //删除动画
        for (int position : selectedPosition) {
            games.remove(position);
            ((Activity) mContext).runOnUiThread(() -> {
                notifyItemRemoved(position);
            });
        }
        selectedGames = new ArrayList<>();
        ((Activity) mContext).runOnUiThread(this::notifyDataSetChanged);
    }

    public List<RecyclePackGame> getSelectedGames() {
        return selectedGames;
    }

    public void setOnSelectedGamesChange(OnSelectedGamesChange onSelectedGamesChange) {
        this.onSelectedGamesChange = onSelectedGamesChange;
    }

    public interface OnSelectedGamesChange {
        void onchange(List<RecyclePackGame> selected);
    }

    public void selectedAll(boolean selected) {
        for (int i = 0; i < getItemCount(); i++) {
            if (selected != games.get(i).isSelected()) {
                notifyItemChanged(i, GAME_SELECTED_SIGNAL);
            }
        }
        // 修改view的进程和这边不同步，为了方便就直接这样.. 反正效果一样嘛
        onSelectedGamesChange.onchange(selected ? games : new ArrayList<>());
    }

    public void setOnGameClick(OnGameClick onGameClick) {
        this.onGameClick = onGameClick;
    }

    public PackGameAdapter(Context mContext, List<RecyclePackGame> games, String type, ImageTextToast toast) {
        this.games = games;
        this.mContext = mContext;
        this.toast = toast;
        this.type = type;
        this.util = new SharedDataUtil(mContext);
    }

    public interface OnGameClick {
        void onClick(RecyclePackGame game);
    }

    void gameItemSelected(int position) {
        changeItem(position, GAME_SELECTED_SIGNAL);
        // 点击选中按钮
        CallUtil selectedGameCallback = new CallUtil()
                .success((msg, data) -> {
                    if (onSelectedGamesChange != null) {
                        onSelectedGamesChange.onchange(selectedGames);
                    }
                })
                .fail((msg, dataJSON) -> {
                    ((Activity) mContext).runOnUiThread(() -> {
                        toast.fail(msg);
                    });
                    Log.w(TAG, "PackGameAdapter: " + msg);
                })
                .error((msg, dataJSON) -> {
                    ((Activity) mContext).runOnUiThread(() -> {
                        toast.error(msg);
                    });
                    Log.e(TAG, "PackGameAdapter: " + msg);
                });

        FormBody form = new FormBody
                .Builder()
                .add("account", util.getAccount())
                .add("type", type)
                .add("idList", JSONObject.toJSONString(Collections.singletonList(games.get(position).getId())))
                .build();

        Request request = new Request.Builder()
                .url(Objects.requireNonNull(HttpUrl.parse(URL.PACK_SELECTED)))
                .addHeader("token", util.getToken())
                .post(form)
                .build();

        new RequestUtil(mContext)
                .setRequest(request)
                .setCallback(selectedGameCallback)
                .error((call, e) -> {
                    e.printStackTrace();
                    ((Activity) mContext).runOnUiThread(() -> {
                        toast.error("请求出错");
                    });
                }).async();
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        int position = (int) v.getTag();
        if (vid == R.id.selected) {
            gameItemSelected(position);

        } else if (vid == R.id.num_plus_bt) {
            // 数量增加按钮
            numUpdate(position, games.get(position).getNum() + 1, NUM_PLUS_SIGNAL, NUM_REDUCE_SIGNAL);

        } else if (vid == R.id.num_reduce_bt) {
            // 减少按钮
            if (games.get(position).getNum() == 1) {
                new ImageTextToast(mContext).fail("不能更少了哦~");

            } else {
                numUpdate(position, games.get(position).getNum() - 1, NUM_REDUCE_SIGNAL, NUM_PLUS_SIGNAL);
            }

        } else {
            // 点击整个按钮
            if (onGameClick != null) {
                onGameClick.onClick(games.get(position));
            }
        }
    }

    // 修改游戏数量
    private void numUpdate(int position, int num, String signal, String failSignal) {
        // 修改数量的View
        notifyItemChanged(position, signal);
        Map<String, Object> numObject = new HashMap<>();
        numObject.put("id", games.get(position).getId());
        numObject.put("num", num);

        CallUtil numUpdateCallback = new CallUtil()
                .success((msg, data) -> {
                    // 修改了选中的游戏的数量
                    if (onSelectedGamesChange != null && games.get(position).isSelected()) {
                        onSelectedGamesChange.onchange(selectedGames);
                    }
                })
                .fail((msg, data) -> {
                    toast.fail(msg);
                    ((Activity) mContext).runOnUiThread(() -> {
                        toast.fail(msg);
                    });
                    changeItem(position, failSignal);
                })
                .error((msg, data) -> {
                    toast.error(msg);
                    ((Activity) mContext).runOnUiThread(() -> {
                        toast.error(msg);
                    });
                    changeItem(position, failSignal);
                });

        FormBody form = new FormBody
                .Builder()
                .add("account", util.getAccount())
                .add("type", type)
                .add("numList", JSONObject.toJSONString(Collections.singletonList(numObject)))
                .build();

        Request request = new Request.Builder()
                .url(Objects.requireNonNull(HttpUrl.parse(URL.PACK_UPDATE_NUM)))
                .addHeader("token", util.getToken())
                .post(form)
                .build();

        new RequestUtil(mContext)
                .setRequest(request)
                .setCallback(numUpdateCallback)
                .error((call, e) -> {
                    e.printStackTrace();
                    ((Activity) mContext).runOnUiThread(() -> {
                        toast.error("请求出错");
                    });
                }).async();
    }

    private void changeItem(int position, String signal) {
        ((Activity) mContext).runOnUiThread(() -> {
            notifyItemChanged(position, signal);
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_pack_game, parent, false));
    }

    @Override
    @SuppressLint("SetTextI18n")
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecyclePackGame game = games.get(position);
        // 加载游戏封面图片
        Glide.with(mContext).load(URL.IMAGE + (game.getCover_image() == null ? "notfound.jpg" : game.getCover_image())).into(holder.coverImage);
        // 设置选中状态
        holder.selectedBt.setImageResource(game.isSelected() ? R.mipmap.selected : R.mipmap.unselected);
        if (game.isSelected()) {
            selectedGames.add(game);
            selectedPosition.add(position);
        }
        // 设置游戏名
        holder.gameNameView.setText(game.getName());
        // 显示游戏平台
        holder.platformNameView.setText(game.getPlatform());
        // 设置减少按钮的图标
        holder.numReduceBt.setBackground(mContext.getDrawable(game.getNum() == 1 ? R.drawable.blue_bt_background_3 : R.drawable.blue_bt_background_2));
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
        holder.numTextView.setText("" + game.getNum());

        holder.selectedBt.setTag(position);
        holder.numReduceBt.setTag(position);
        holder.NumPlusBt.setTag(position);
        holder.setItemTag(position);
    }

    @Override
    @SuppressLint("SetTextI18n")
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
                    holder.numTextView.setText("" + game.getNum());

                } else if (NUM_REDUCE_SIGNAL.equals(payloads.get(0))) {
                    // 点击减少按钮
                    game.setNum(game.getNum() - 1);
                    holder.numTextView.setText("" + game.getNum());

                    if (game.getNum() == 1) {
                        holder.numReduceBt.setBackground(mContext.getDrawable(R.drawable.blue_bt_background_3));
                    }

                } else if (GAME_SELECTED_SIGNAL.equals(payloads.get(0))) {
                    game.setSelected(!game.isSelected());
                    // 修改图标
                    holder.selectedBt.setImageResource(game.isSelected() ? R.mipmap.selected : R.mipmap.unselected);
                    // 修改已选中的id列表
                    if (game.isSelected()) {
                        selectedGames.add(game);
                        selectedPosition.add(position);
                    } else {
                        selectedGames.remove(game);
                        selectedPosition.remove(Integer.valueOf(position));
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
            numTextView = itemView.findViewById(R.id.game_num);
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
