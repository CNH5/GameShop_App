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
import com.example.gameshop.utils.CallBackUtil;
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
            // 左上角的返回按钮
            finish();
        } else if (vid == R.id.add_bag_bt) {
            // 右下角的添加到回收袋按钮
            onAddBagBtClick();

        } else if (vid == R.id.transaction_bt) {
            // 右下角的交易按钮
            onTransactionBtClick();

        } else if (vid == R.id.service_bt) {
            // 左下角客服按钮
            new ImageTextToast(this).error("功能未实现");

        } else if (vid == R.id.star_bt) {
            // 左下角收藏按钮
            new ImageTextToast(this).error("功能未实现");

        } else if (vid == R.id.more_bt) {
            // 右上角更多按钮
            new ImageTextToast(this).error("功能未实现");

        } else if (vid == R.id.close_bt) {
            // 右上角的关闭按钮,好像没必要?
            new ImageTextToast(this).error("功能未实现");

        }
    }

    // 设置按键监听
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

    // 初始化参数
    private void initParams() {
        // 获取之前的界面传递过来的id
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
        // 找view
        titleView = findViewById(R.id.title);
        nameView = findViewById(R.id.game_name);
        priceView = findViewById(R.id.game_price);
        chart = findViewById(R.id.history_price);

        // 设置回收流程的图片
        Glide.with(this).load(URL.IMAGE + "process.png").into((ImageView) findViewById(R.id.process));
    }

    // 获取数据
    private void setGameInfo(AfterGetDataSuccess after) {
        CallBackUtil getGameCallback = new CallBackUtil()
                .success((msg, data) -> {
                    // 设置游戏信息
                    game = JSONObject.parseObject(data, Game.class);
                    // 设置需要游戏信息的ui
                    initView();
                    // 执行额外操作
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
        new RequestUtil()
                .setRequest(request)
                .setContext(this)
                .setCallback(getGameCallback)
                .error((error, e) -> {
                    e.printStackTrace();
                    GameActivity.this.runOnUiThread(() -> {
                        toast.error("获取数据失败");
                    });
                })
                .async();
    }

    // 获取数据之后，将数据显示到界面中
    @SuppressLint("SetTextI18n")
    private void initView() {
        List<String> imageUrls = new ArrayList<>();
        // 把获取的图片转换成真正的url
        for (String pic : game.getImages()) {
            imageUrls.add(URL.IMAGE + pic);
        }
        // 设置数据要渲染的地方
        runOnUiThread(() -> {
            picturesBanner.setImages(imageUrls).start();
            titleView.setText(game.getName());
            nameView.setText(game.getName());
            priceView.setText(game.getPrice() + "元");

            new HistoryPriceChart(chart, game.getHistory_price(), game.getPrice().floatValue())
                    .line("#409EFF")
                    .init();
        });
    }

    private void initScrollView() {
        NestedScrollView scrollView = findViewById(R.id.scrollView);
        SwipeRefreshLayout swipe = findViewById(R.id.swipe);
        swipe.setColorSchemeResources(R.color.blue);
        // 防止下拉刷新组件和滑动组件冲突
        scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            swipe.setEnabled(scrollY == 0);
        });
        // 下拉时获取数据
        swipe.setOnRefreshListener(() -> {
            setGameInfo(() -> {
                swipe.setRefreshing(false);
                toast.success("刷新成功");
            });
        });
    }

    // 点击添加到购物车按钮
    public void onAddBagBtClick() {
        if (new SharedDataUtil(this).notLogin()) {
            // 没登陆，跳转到登陆界面
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LoginActivity.CODE);

        } else {
            // 设置调出窗口
            RecyclePopupWindow window = new RecyclePopupWindow(this, game)
                    .setBuyTipText("添加到：购买")
                    .setRecycleTipText("添加到：回收")
                    .setSwitchDescText("添加类型：")
                    .confirmBtText("添加到回收袋");
            // 点击确认按钮之后提交添加请求
            window.setOnSubmit(((type, num) -> {
                addPack(type, num, window);
            }));
            // 设置出现位置
            window.showAtLocation(
                    getWindow().getDecorView(),
                    Gravity.BOTTOM,
                    0, 0
            );
            // 设置背景
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

        CallBackUtil addPackCalledBack = new CallBackUtil()
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

        new RequestUtil()
                .setContext(this)
                .setRequest(request)
                .setCallback(addPackCalledBack)
                .error((error, e) -> {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        toast.error("获取数据失败");
                    });
                })
                .async();
    }

    // 点击交易按钮
    public void onTransactionBtClick() {
        if (new SharedDataUtil(this).notLogin()) {
            // 没登陆，跳转到登陆界面
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LoginActivity.CODE);

        } else {
            // 设置调出窗口
            RecyclePopupWindow window = new RecyclePopupWindow(this, game)
                    .setBuyTipText("购买:" + game.getName())
                    .setRecycleTipText("回收:" + game.getName())
                    .setSwitchDescText("交易类型：")
                    .transaction(true);
            // 点击确认按钮之后跳转到订单生成界面
            window.setOnSubmit((type, num) -> {
                Intent intent = new Intent(this, OrderSubmitActivity.class)
                        .putExtra("type", type)
                        .putExtra("id", JSONObject.toJSONString(Collections.singleton(game.getId())))
                        .putExtra("num", JSONObject.toJSONString(Collections.singleton(num)));
                startActivityForResult(intent, OrderSubmitActivity.CODE);
            });
            // 设置出现位置
            window.showAtLocation(
                    getWindow().getDecorView(),
                    Gravity.BOTTOM,
                    0, 0
            );
            // 设置背景
            window.setBackground();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case LoginActivity.CODE:
                    // 登陆成功
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