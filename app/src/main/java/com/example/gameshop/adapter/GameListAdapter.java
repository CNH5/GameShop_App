package com.example.gameshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.gameshop.Constants;
import com.example.gameshop.R;
import com.example.gameshop.pojo.Game;

import java.util.List;

/**
 * @author sheng
 * @date 2021/11/22 5:12
 */
public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.ViewHolder> implements View.OnClickListener {
    private final List<Game> gameList;
    private final Context mContext;

    public GameListAdapter(Context context, List<Game> data) {
        this.mContext = context;
        this.gameList = data;
    }

    private onGameClick onGameClick;

    public void setonGameClick(onGameClick onGameClick) {
        this.onGameClick = onGameClick;
    }

    public interface onGameClick {
        void use(Game game);
    }

    @Override
    public void onClick(View v) {
        onGameClick.use(gameList.get((int) v.getTag()));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_game, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Game game = gameList.get(position);
        // 加载游戏封面图片
        Glide.with(mContext).load(Constants.IMAGE_URL + game.getCover_image()).into(holder.cover_image);
        // 设置游戏名称
        holder.name.setText(game.getName());
        // 设置价格
        holder.price.setText(game.getPrice());
        // 设置每个item的tag
        holder.setItemTag(position);
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cover_image;
        TextView name;
        TextView price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cover_image = itemView.findViewById(R.id.cover_image);
            name = itemView.findViewById(R.id.game_name);
            price = itemView.findViewById(R.id.game_price);

            itemView.setOnClickListener(GameListAdapter.this);
        }

        public void setItemTag(int tag) {
            itemView.setTag(tag);
        }
    }
}
