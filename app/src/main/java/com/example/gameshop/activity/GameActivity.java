package com.example.gameshop.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.gameshop.R;

public class GameActivity extends AppCompatActivity {
    public static final int CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }
}