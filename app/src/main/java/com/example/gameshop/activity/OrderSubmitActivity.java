package com.example.gameshop.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.example.gameshop.R;
import com.example.gameshop.adapter.OrderGameAdapter;
import com.example.gameshop.config.URL;
import com.example.gameshop.toast.ImageTextToast;
import com.example.gameshop.utils.RequestUtil;
import com.example.gameshop.utils.CallUtil;
import com.example.gameshop.utils.SharedDataUtil;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.*;

public class OrderSubmitActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    public static final int CODE = 6;
    private List<Long> idList;
    private String type;
    private List<Integer> numList;
    private TextView numTextView;
    private TextView priceTextView;
    private RecyclerView gameListView;
    private ImageTextToast toast;
    private View confirmButton;
    private SharedDataUtil util;
    private EditText nameET;
    private EditText phoneET;
    private EditText locationET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_submit);
        initParams();
        getGames();
        initView();
    }

    private void initAdapter(List<Map<String, Object>> info) {
        OrderGameAdapter adapter = new OrderGameAdapter(info, this, numList, type);
        runOnUiThread(() -> {
            gameListView.setAdapter(adapter);
        });
    }

    @SuppressLint("SetTextI18n")
    private void initParamView(List<Map<String, Object>> info) {
        int num = 0;
        double count = 0;
        for (int i = 0; i < numList.size(); i++) {
            num += numList.get(i);
            if ("回收".equals(type)) {
                count += numList.get(i) * (Double.parseDouble(Objects.requireNonNull(info.get(i).get("price")).toString()) - 15);

            } else if ("购买".equals(type)) {
                count += numList.get(i) * Double.parseDouble(Objects.requireNonNull(info.get(i).get("price")).toString());
            }
        }

        int finalNum = num;
        double finalCount = count;
        runOnUiThread(() -> {
            numTextView.setText("共" + finalNum + "张 合计");
            priceTextView.setText("" + finalCount);
        });
    }

    private void getGames() {
        CallUtil getGameCallback = new CallUtil()
                .success((msg, data) -> {
                    List<Map<String, Object>> info = JSONObject.parseObject(data, new TypeReference<List<Map<String, Object>>>() {
                    });

                    initAdapter(info);
                    initParamView(info);
                })
                .fail((msg, data) -> {
                    runOnUiThread(() -> {
                        toast.fail(msg);
                    });
                })
                .error((msg, data) -> {
                    runOnUiThread(() -> {
                        toast.error(msg);
                    });
                });

        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(URL.GAME_LIST))
                .newBuilder()
                .addQueryParameter("id", JSONObject.toJSONString(idList))
                .build();

        new RequestUtil(this)
                .setRequest(new Request.Builder().url(url).get().build())
                .setCallback(getGameCallback)
                .error((call, e) -> {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        toast.error("请求出错");
                    });
                })
                .async();
    }

    private void initParams() {
        idList = JSONObject.parseArray(getIntent().getStringExtra("id"), Long.class);
        numList = JSONObject.parseArray(getIntent().getStringExtra("num"), Integer.class);
        type = getIntent().getStringExtra("type");

        numTextView = findViewById(R.id.num_text);
        priceTextView = findViewById(R.id.price_count_text);

        gameListView = findViewById(R.id.games);
        nameET = findViewById(R.id.name_et);
        phoneET = findViewById(R.id.phoneNumber_et);
        locationET = findViewById(R.id.location_et);

        nameET.addTextChangedListener(this);
        phoneET.addTextChangedListener(this);
        locationET.addTextChangedListener(this);

        gameListView.setLayoutManager(new LinearLayoutManager(this));

        confirmButton = findViewById(R.id.confirm_bt);
        confirmButton.setOnClickListener(this);
        findViewById(R.id.back_bt).setOnClickListener(this);
        toast = new ImageTextToast(this);
        util = new SharedDataUtil(this);
    }

    @SuppressLint("SetTextI18n")
    void initView() {
        TextView confirmBtTextView = findViewById(R.id.confirm_bt_text);
        TextView titleTextView = findViewById(R.id.title_text);

        titleTextView.setText("确认" + type + "订单");
        confirmBtTextView.setText("确认" + type);

        if ("回收".equals(type)) {
            findViewById(R.id.buy_form).setVisibility(View.GONE);
            setConfirmBt(true);
        } else if ("购买".equals(type)) {
            findViewById(R.id.recycle_form).setVisibility(View.GONE);
            setConfirmBt(false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        setConfirmBt(
                !"".equals(nameET.getText().toString().trim()) &&
                        phoneET.getText().toString().length() == 11 &&
                        !"".equals(locationET.getText().toString().trim())
        );
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    void setConfirmBt(boolean enabled) {
        confirmButton.setBackground(getDrawable(enabled ? R.drawable.shape_corner11 : R.drawable.shape_corner17));
        confirmButton.setEnabled(enabled);
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.back_bt) {
            finish();

        } else if (vid == R.id.confirm_bt) {
            if ("回收".equals(type)) {
                submitRecycleForm();
            } else if ("购买".equals(type)) {
                submitBuyForm();
            }
        }
    }

    private final CallUtil submitCallback = new CallUtil()
            .success((msg, data) -> {
                Intent intent = new Intent()
                        .putExtra("msg", msg)
                        .putExtra("data", data);
                setResult(RESULT_OK, intent);
                finish();
            })
            .fail((msg, dataJSON) -> {
                runOnUiThread(() -> {
                    toast.fail(msg);
                });
            })
            .error((msg, dataJSON) -> {
                runOnUiThread(() -> {
                    toast.error(msg);
                });
            });

    private JSONObject getOrderForm() {
        JSONObject orderForm = new JSONObject();
        orderForm.put("account", util.getAccount());
        orderForm.put("type", type);

        List<Map<String, String>> gamesList = new ArrayList<>();
        for (int i = 0; i < idList.size(); i++) {
            Map<String, String> game = new HashMap<>();
            game.put("id", String.valueOf(idList.get(i)));
            game.put("num", String.valueOf(numList.get(i)));
            gamesList.add(game);
        }
        orderForm.put("games", gamesList);
        return orderForm;
    }

    private void submitBuyForm() {
        JSONObject orderForm = getOrderForm();
        orderForm.put("name", nameET.getText().toString());
        orderForm.put("phoneNumber", phoneET.getText().toString());
        orderForm.put("location", locationET.getText().toString());

        submitForm(orderForm);
    }

    private void submitRecycleForm() {
        submitForm(getOrderForm());
    }

    private void submitForm(JSONObject orderForm) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), orderForm.toString());
        Request request = new Request
                .Builder()
                .url(Objects.requireNonNull(HttpUrl.parse(URL.ORDER_ADD)))
                .post(requestBody)
                .addHeader("token", util.getToken())
                .build();

        new RequestUtil(this)
                .setRequest(request)
                .setCallback(submitCallback)
                .error((error, e) -> {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        toast.error("获取数据失败");
                    });
                })
                .async();
    }
}