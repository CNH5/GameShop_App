package com.example.gameshop.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.gameshop.R;

public class OrderSubmitActivity extends AppCompatActivity {
    public static final int CODE = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_submit);
    }
}