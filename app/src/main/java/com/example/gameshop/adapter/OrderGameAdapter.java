package com.example.gameshop.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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

import java.util.List;
import java.util.Map;

/**
 * @author sheng
 * @date 2021/12/22 2:59
 */
public class OrderGameAdapter extends RecyclerView.Adapter<OrderGameAdapter.ViewHolder> {
    private final List<Map<String, Object>> info;
    private final Context mContext;
    private final List<Integer> numList;

    public OrderGameAdapter(List<Map<String, Object>> info, Context mContext, List<Integer> numList) {
        this.info = info;
        this.mContext = mContext;
        this.numList = numList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_order_game, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> data = info.get(position);
        // 加载游戏封面图片
        Glide.with(mContext).load(URL.IMAGE + (data.get("cover_image") == null ? "notfound.jpg" : data.get("cover_image"))).into(holder.coverImageView);
        holder.gameNameTextView.setText(String.valueOf(data.get("name")));
        holder.gamePriceTextView.setText(String.valueOf(data.get("price")));
        holder.gameNumTextView.setText("" + numList.get(position));
    }

    @Override
    public int getItemCount() {
        return info.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImageView;
        TextView gameNameTextView;
        TextView gamePriceTextView;
        TextView gameNumTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            coverImageView = itemView.findViewById(R.id.cover_image);
            gameNameTextView = itemView.findViewById(R.id.game_name);
            gamePriceTextView = itemView.findViewById(R.id.price);
            gameNumTextView = itemView.findViewById(R.id.num);
        }
    }
}
