package com.example.gameshop.activity;

import android.util.Log;
import androidx.annotation.NonNull;
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
import com.example.gameshop.utils.ResponseUtil;
import com.example.gameshop.utils.SharedDataUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private final List<Fragment> fragments = Arrays.asList(
            GameListFragment.newInstance(),
            new ServiceFragment(),
            new PackFragment(),
            new UserFragment()
    );
    private ViewPager2 mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkLogin();
        initParams();
        initBottomNav();
    }

    private void checkLogin() {
        new RequestUtil(this)
                .get()
                .url(URL.CHECK_URL)
                .setToken()
                .then((call, response) -> {
                    new ResponseUtil(response)
                            .fail((msg, dataJSON) -> {
                                Log.d(TAG, msg);
                                new SharedDataUtil(this).cleanToken();
                            })
                            .handle();
                })
                .error((call, e) -> {
                    e.printStackTrace();
                    new ImageTextToast(this).error("网络异常");
                })
                .request();
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
                mainView.setCurrentItem(3);
            }
            return true;
        });
    }

}