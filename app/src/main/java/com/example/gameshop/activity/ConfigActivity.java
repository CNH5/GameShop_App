package com.example.gameshop.activity;

import android.content.Intent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.gameshop.R;
import com.example.gameshop.utils.SharedDataUtil;

public class ConfigActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int CODE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        if (null == new SharedDataUtil(this).getToken()) {
            findViewById(R.id.logout_bt).setVisibility(View.GONE);
        } else {
            findViewById(R.id.no_more).setVisibility(View.GONE);
        }

        findViewById(R.id.back_bt).setOnClickListener(this);
        findViewById(R.id.logout_bt).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.back_bt) {
            finish();
        } else if (vid == R.id.logout_bt) {
            logout();
        }
    }

    // 登出操作
    private void logout() {
        new SharedDataUtil(this).cleanToken();
        setResult(RESULT_OK, new Intent());

        finish();
    }
}