package com.example.gameshop.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.bumptech.glide.Glide;
import com.example.gameshop.Constants;
import com.example.gameshop.R;
import com.example.gameshop.pojo.Game;
import com.example.gameshop.utils.RequestUtil;
import com.example.gameshop.utils.ResponseUtil;
import com.google.gson.Gson;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoader;
import okhttp3.Call;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int CODE = 2;
    private static final String TAG = "GameActivity";
    private long id;
    private Game game;
    private Banner picturesBanner;
    private TextView titleView;
    private TextView name;
    private TextView price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initParams();
        setGameInfo();
        setOnClickListener();
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.back_bt) {
            finish();
        }
    }

    void setOnClickListener() {
        findViewById(R.id.back_bt).setOnClickListener(this);
    }

    private void initParams() {
        id = getIntent().getLongExtra("id", -1);
        picturesBanner = findViewById(R.id.pictures_banner);
        picturesBanner
                .setBannerStyle(BannerConfig.CIRCLE_INDICATOR)
                .setImageLoader(new ImageLoader() {
                    @Override
                    public void displayImage(Context context, Object path, ImageView imageView) {
                        Glide.with(context).load(path).into(imageView);
                    }
                }).setDelayTime(3000);
        titleView = findViewById(R.id.title);
        name = findViewById(R.id.game_name);
        price = findViewById(R.id.game_price);
    }

    private void setGameInfo() {
        new RequestUtil()
                .get()
                .url(Constants.GAME_INFO_URL + "/" + id)
                .then(this::getGameSuccess)
                .error(this::getGameError)
                .request();
    }

    private void getGameSuccess(Call call, Response response) throws IOException {
        new ResponseUtil(response)
                .success((msg, dataJSON) -> {
                    // 设置游戏信息
                    game = new Gson().fromJson(dataJSON, Game.class);
                    // 设置需要游戏信息的ui
                    initView();
                })
                .fail((msg, dataJSON) -> {
                    // TODO:获取失败，做提示
                    Log.w(TAG, msg);
                })
                .error((msg, dataJSON) -> {
                    // TODO:后台出错,做提示
                    Log.e(TAG, msg);
                })
                .handle();
    }

    private void getGameError(Call call, IOException e) {
        // TODO:提示请求出错
        e.printStackTrace();
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        List<String> imageUrls = new ArrayList<>();
        // 把获取的图片转换成真正的url
        for (String pic : game.getImages()) {
            imageUrls.add(Constants.IMAGE_URL + pic);
        }
        // 设置数据对应的位置
        runOnUiThread(() -> {
            picturesBanner.setImages(imageUrls).start();
            titleView.setText(game.getName());
            name.setText(game.getName());
            price.setText(game.getPrice() + "元");
        });
    }
}