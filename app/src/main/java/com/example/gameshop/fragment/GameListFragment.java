package com.example.gameshop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gameshop.config.URL;
import com.example.gameshop.R;
import com.example.gameshop.activity.GameActivity;
import com.example.gameshop.adapter.GameListAdapter;
import com.example.gameshop.pojo.Game;
import com.example.gameshop.toast.ImageTextToast;
import com.example.gameshop.utils.RequestUtil;
import com.example.gameshop.utils.ResponseUtil;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.Call;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameListFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "GameListFragment";
    private RecyclerView lvGameList;
    private TabLayout platformTab;
    private List<Game> gameList;
    private final String[] platforms = new String[]{"NS", "PS"};
    private ImageTextToast toast;
    private int page = 1;


    public GameListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.search) {
            // 点击搜索按钮,进入搜索界面
            Log.d(TAG, "点击了搜索按钮");
        }
    }

    public static GameListFragment newInstance() {
        Bundle args = new Bundle();

        GameListFragment fragment = new GameListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toast = new ImageTextToast(getActivity());
        initList(0);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_list, container, false);

        lvGameList = view.findViewById(R.id.game_list);
        lvGameList.setLayoutManager(new LinearLayoutManager(getActivity()));

        view.findViewById(R.id.search).setOnClickListener(this);
        platformTab = view.findViewById(R.id.top_tab);

        initTab();

        return view;
    }

    private void initList(int id) {
        new RequestUtil(getActivity())
                .get()
                .url(URL.GAME_LIST)
                .addQueryParameter("page", page)
                .addQueryParameter("platform", platforms[id])
                .then(this::getListSuccess)
                .error(this::getListError)
                .request();
    }

    private void getListSuccess(Call call, Response response) throws IOException {
        new ResponseUtil(response)
                .success((msg, dataJSON) -> {
                    Type type = new TypeToken<List<Game>>() {
                    }.getType();
                    // 设置list
                    gameList = new Gson().fromJson(dataJSON, type);
                    // 设置适配器
                    setAdapter();
                })
                .fail((msg, dataJSON) -> {
                    Log.w(TAG, msg);
                    toast.success(msg);
                })
                .error((msg, dataJSON) -> {
                    Log.e(TAG, msg);
                    toast.error(msg);
                })
                .handle();
    }

    private void setAdapter() {
        GameListAdapter adapter = new GameListAdapter(getActivity(), gameList);
        adapter.setOnGameClick(game -> {
            // 当item被点击，跳转到游戏详细信息界面
            Intent intent = new Intent(getActivity(), GameActivity.class);
            // 传被点击的id
            intent.putExtra("id", game.getId());
            Objects.requireNonNull(getActivity()).startActivity(intent);
        });
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> lvGameList.setAdapter(adapter));
    }

    private void getListError(Call call, IOException e) {
        e.printStackTrace();
        new ImageTextToast(getActivity()).error("获取数据失败");
    }

    private void initTab() {
        for (String platform : platforms) {
            platformTab.addTab(platformTab.newTab().setText(platform));
        }
        platformTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                gameList = new ArrayList<>();
                setAdapter();
                initList(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}