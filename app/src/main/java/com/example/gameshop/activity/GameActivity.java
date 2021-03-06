package com.example.gameshop.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.example.gameshop.config.URL;
import com.example.gameshop.R;
import com.example.gameshop.charts.HistoryPriceChart;
import com.example.gameshop.pojo.Game;
import com.example.gameshop.popupWindow.RecyclePopupWindow;
import com.example.gameshop.toast.ImageTextToast;
import com.example.gameshop.utils.RequestUtil;
import com.example.gameshop.utils.CallUtil;
import com.example.gameshop.utils.SharedDataUtil;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoader;
import lecho.lib.hellocharts.view.LineChartView;
import okhttp3.MultipartBody;
import okhttp3.Request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int CODE = 2;
    private static final String TAG = "GameActivity";
    private ImageTextToast toast;
    private Game game;
    private Banner picturesBanner;
    private TextView titleView;
    private TextView nameView;
    private TextView priceView;
    private LineChartView chart;
    private SharedDataUtil util;
    private long id;


    private interface AfterGetDataSuccess {
        void after();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initParams();
        setGameInfo(null);
        setOnClickListener();
        initScrollView();
    }


    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.back_bt) {
            // ????????????????????????
            finish();
        } else if (vid == R.id.add_bag_bt) {
            // ????????????????????????????????????
            onAddBagBtClick();

        } else if (vid == R.id.transaction_bt) {
            // ????????????????????????
            onTransactionBtClick();

        } else if (vid == R.id.service_bt) {
            // ?????????????????????
            new ImageTextToast(this).error("???????????????");

        } else if (vid == R.id.star_bt) {
            // ?????????????????????
            new ImageTextToast(this).error("???????????????");

        } else if (vid == R.id.more_bt) {
            // ?????????????????????
            new ImageTextToast(this).error("???????????????");

        } else if (vid == R.id.close_bt) {
            // ????????????????????????,????????????????
            new ImageTextToast(this).error("???????????????");

        }
    }

    // ??????????????????
    void setOnClickListener() {
        findViewById(R.id.back_bt).setOnClickListener(this);
        findViewById(R.id.add_bag_bt).setOnClickListener(this);
        findViewById(R.id.star_bt).setOnClickListener(this);
        findViewById(R.id.service_bt).setOnClickListener(this);
        findViewById(R.id.more_bt).setOnClickListener(this);
        findViewById(R.id.close_bt).setOnClickListener(this);
        findViewById(R.id.service_bt).setOnClickListener(this);
        findViewById(R.id.transaction_bt).setOnClickListener(this);
    }

    // ???????????????
    private void initParams() {
        // ????????????????????????????????????id
        id = getIntent().getLongExtra("id", -1);
        toast = new ImageTextToast(this);
        util = new SharedDataUtil(this);

        picturesBanner = findViewById(R.id.pictures_banner);
        picturesBanner
                .setBannerStyle(BannerConfig.CIRCLE_INDICATOR)
                .setImageLoader(new ImageLoader() {
                    @Override
                    public void displayImage(Context context, Object path, ImageView imageView) {
                        Glide.with(context).load(path).into(imageView);
                    }
                }).setDelayTime(3000);
        // ???view
        titleView = findViewById(R.id.title);
        nameView = findViewById(R.id.game_name);
        priceView = findViewById(R.id.game_price);
        chart = findViewById(R.id.history_price);

        // ???????????????????????????
        Glide.with(this).load(URL.IMAGE + "process.png").into((ImageView) findViewById(R.id.process));
    }

    // ????????????
    private void setGameInfo(AfterGetDataSuccess after) {
        CallUtil getGameCallback = new CallUtil()
                .success((msg, data) -> {
                    // ??????????????????
                    game = JSONObject.parseObject(data, Game.class);
                    // ???????????????????????????ui
                    initView();
                    // ??????????????????
                    if (after != null) {
                        after.after();
                    }
                })
                .fail((msg, data) -> {
                    Log.w(TAG, msg + ": " + data);
                    runOnUiThread(() -> {
                        toast.fail(msg);
                    });
                })
                .error((msg, data) -> {
                    Log.e(TAG, msg + ": " + data);
                    runOnUiThread(() -> {
                        toast.error(msg);
                    });
                });

        Request request = new Request.Builder().url(URL.GAME_INFO + id).build();
        new RequestUtil(this)
                .setRequest(request)
                .setCallback(getGameCallback)
                .error((error, e) -> {
                    e.printStackTrace();
                    GameActivity.this.runOnUiThread(() -> {
                        toast.error("??????????????????");
                    });
                })
                .async();
    }

    // ????????????????????????????????????????????????
    @SuppressLint("SetTextI18n")
    private void initView() {
        List<String> imageUrls = new ArrayList<>();
        // ????????????????????????????????????url
        for (String pic : game.getImages()) {
            imageUrls.add(URL.IMAGE + pic);
        }
        // ??????????????????????????????
        runOnUiThread(() -> {
            picturesBanner.setImages(imageUrls).start();
            titleView.setText(game.getName());
            nameView.setText(game.getName());
            priceView.setText(game.getPrice() + "???");

            new HistoryPriceChart(chart, game.getHistory_price(), game.getPrice().floatValue())
                    .line("#409EFF")
                    .init();
        });
    }

    private void initScrollView() {
        NestedScrollView scrollView = findViewById(R.id.scrollView);
        SwipeRefreshLayout swipe = findViewById(R.id.swipe);
        swipe.setColorSchemeResources(R.color.blue);
        // ?????????????????????????????????????????????
        scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            swipe.setEnabled(scrollY == 0);
        });
        // ?????????????????????
        swipe.setOnRefreshListener(() -> {
            setGameInfo(() -> {
                swipe.setRefreshing(false);
                toast.success("????????????");
            });
        });
    }

    // ??????????????????????????????
    public void onAddBagBtClick() {
        if (new SharedDataUtil(this).notLogin()) {
            // ?????????????????????????????????
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LoginActivity.CODE);

        } else {
            // ??????????????????
            RecyclePopupWindow window = new RecyclePopupWindow(this, game)
                    .setBuyTipText("??????????????????")
                    .setRecycleTipText("??????????????????")
                    .setSwitchDescText("???????????????")
                    .confirmBtText("??????????????????");
            // ??????????????????????????????????????????
            window.setOnSubmit(((type, num) -> {
                addPack(type, num, window);
            }));
            // ??????????????????
            window.showAtLocation(
                    getWindow().getDecorView(),
                    Gravity.BOTTOM,
                    0, 0
            );
            // ????????????
            window.setBackground();
        }
    }

    private void addPack(String type, int num, RecyclePopupWindow window) {
        MultipartBody.Builder formBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        formBuilder
                .addFormDataPart("num", JSONObject.toJSONString(num))
                .addFormDataPart("type", type)
                .addFormDataPart("id", JSONObject.toJSONString(game.getId()))
                .addFormDataPart("account", util.getAccount());

        Request request = new Request
                .Builder()
                .url(URL.ADD_PACK)
                .post(formBuilder.build())
                .addHeader("token", util.getToken())
                .build();

        CallUtil addPackCalledBack = new CallUtil()
                .success((msg, data) -> {
                    runOnUiThread(() -> {
                        toast.success(msg);
                        runOnUiThread(window::dismiss);
                    });
                })
                .fail((msg, data) -> {
                    runOnUiThread(() -> {
                        toast.fail(msg);
                    });
                    Log.w(TAG, msg + ": " + data);
                })
                .error((msg, data) -> {
                    runOnUiThread(() -> {
                        toast.error(msg);
                    });
                    Log.e(TAG, msg + ": " + data);
                });

        new RequestUtil(this)
                .setRequest(request)
                .setCallback(addPackCalledBack)
                .error((error, e) -> {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        toast.error("??????????????????");
                    });
                })
                .async();
    }

    // ??????????????????
    public void onTransactionBtClick() {
        if (new SharedDataUtil(this).notLogin()) {
            // ?????????????????????????????????
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LoginActivity.CODE);

        } else {
            // ??????????????????
            RecyclePopupWindow window = new RecyclePopupWindow(this, game)
                    .setBuyTipText("??????:" + game.getName())
                    .setRecycleTipText("??????:" + game.getName())
                    .setSwitchDescText("???????????????")
                    .transaction(true);
            // ???????????????????????????????????????????????????
            window.setOnSubmit((type, num) -> {
                Intent intent = new Intent(this, OrderSubmitActivity.class)
                        .putExtra("type", type)
                        .putExtra("id", JSONObject.toJSONString(Collections.singleton(game.getId())))
                        .putExtra("num", JSONObject.toJSONString(Collections.singleton(num)));
                startActivityForResult(intent, OrderSubmitActivity.CODE);
            });
            // ??????????????????
            window.showAtLocation(
                    getWindow().getDecorView(),
                    Gravity.BOTTOM,
                    0, 0
            );
            // ????????????
            window.setBackground();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case LoginActivity.CODE:
                    // ????????????
                    assert data != null;
                    new ImageTextToast(this).success(data.getStringExtra("msg"));
                    break;
                case 4:

                    break;
                default:

            }
        }
    }
}