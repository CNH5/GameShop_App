package com.example.gameshop.activity;

import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.example.gameshop.config.URL;
import com.example.gameshop.R;
import com.example.gameshop.fragment.GameListFragment;
import com.example.gameshop.fragment.PackFragment;
import com.example.gameshop.fragment.ServiceFragment;
import com.example.gameshop.fragment.UserFragment;
import com.example.gameshop.toast.ImageTextToast;
import com.example.gameshop.utils.RequestUtil;
import com.example.gameshop.utils.CallUtil;
import com.example.gameshop.utils.SharedDataUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import okhttp3.HttpUrl;
import okhttp3.Request;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private final List<Fragment> fragments = Arrays.asList(
            GameListFragment.newInstance(),
            new ServiceFragment(),
            PackFragment.newInstance(),
            UserFragment.newInstance()
    );
    private ViewPager2 mainView;
    private ImageTextToast toast;
    private SharedDataUtil util;

    private final CallUtil checkLoginCallback = new CallUtil()
            .fail((msg, dataJSON) -> {
                Log.d(TAG, msg);
                toast.fail("登录已过期!");
                new SharedDataUtil(this).cleanToken();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initParams();
        checkLogin();
        initBottomNav();
    }

    private void checkLogin() {
        if (util.getToken() != null) {
            HttpUrl url = Objects.requireNonNull(HttpUrl.parse(URL.CHECK))
                    .newBuilder()
                    .addQueryParameter("account", util.getAccount())
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("token", util.getToken())
                    .build();

            new RequestUtil(this)
                    .setRequest(request)
                    .setCallback(checkLoginCallback)
                    .error((error, e) -> {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            toast.error("获取数据失败");
                        });
                    })
                    .async();
        }

    }

    private void initParams() {
        mainView = findViewById(R.id.main_view);
        mainView.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return fragments.get(position);
            }

            @Override
            public int getItemCount() {
                return fragments.size();
            }
        });
        toast = new ImageTextToast(this);
        util = new SharedDataUtil(this);
    }

    private void initBottomNav() {
        BottomNavigationView mainBottomNav = findViewById(R.id.main_bottom_nav);
        mainBottomNav.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_game) {
                mainView.setCurrentItem(0);

            } else if (id == R.id.menu_service) {
                mainView.setCurrentItem(1);

            } else if (id == R.id.menu_pack) {
                mainView.setCurrentItem(2);

            } else if (id == R.id.menu_user) {
                if (new SharedDataUtil(this).notLogin()) {
                    Intent intent = new Intent(this, LoginActivity.class).putExtra("route", 3);
                    startActivityForResult(intent, LoginActivity.CODE);
                } else {
                    mainView.setCurrentItem(3);
                }
            }
            return true;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ConfigActivity.CODE:
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivityForResult(intent, LoginActivity.CODE);
                    mainView.setCurrentItem(0);
                    break;
                case LoginActivity.CODE:
                    // 要根据用户的点击来判断跳转到哪个
                    assert data != null;
                    mainView.setCurrentItem(data.getIntExtra("route", 0));
                    new ImageTextToast(this).success(data.getStringExtra("msg"));
                    break;
                case GameActivity.CODE:
                    // 这里就应该更新购物车的商品数量
                    break;
                default:

            }
        }
    }
}