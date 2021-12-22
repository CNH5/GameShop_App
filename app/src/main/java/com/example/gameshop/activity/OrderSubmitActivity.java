package com.example.gameshop.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.fastjson.JSONObject;
import com.example.gameshop.R;
import com.example.gameshop.config.URL;
import com.example.gameshop.utils.RequestUtil;
import com.example.gameshop.utils.CallBackUtil;

import java.util.List;
import java.util.Map;

public class OrderSubmitActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    public static final int CODE = 6;
    private List<Long> idList;
    private List<Integer> numList;
    private TextView numTextView;
    private TextView priceTextView;
    private TextView titleTextView;
    private RecyclerView gameListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_submit);
        initParams();
    }

    private void initParams() {
        idList = JSONObject.parseArray(getIntent().getStringExtra("id"), Long.class);
        numList = JSONObject.parseArray(getIntent().getStringExtra("num"), Integer.class);
        numTextView = findViewById(R.id.num_text);
        priceTextView = findViewById(R.id.price_count_text);
        titleTextView = findViewById(R.id.title_text);
        gameListView = findViewById(R.id.games);

        findViewById(R.id.confirm_bt).setOnClickListener(this);
        findViewById(R.id.back_bt).setOnClickListener(this);
        initGetInfoRequest();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.back_bt) {
            finish();
        }
    }

    private void initGetInfoRequest() {

    }
}