package com.example.gameshop.fragment;

import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.gameshop.Constants;
import com.example.gameshop.R;
import com.example.gameshop.adapter.GameListAdapter;
import com.example.gameshop.pojo.Game;
import com.example.gameshop.utils.RequestUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameListFragment extends Fragment {
    private static final String TAG = "GameListFragment";
    private RecyclerView lvGameList;
    private List<Game> gameList;
    private String[] platforms = new String[]{"NS", "PS"};
    private int page = 1;


    public GameListFragment() {
        // Required empty public constructor
    }

    public void initList() {
        new RequestUtil()
                .get()
                .url(Constants.GAME_LIST_URL)
                .addQueryParameter("page", page)
                .addQueryParameter("platform", platforms[0])
                .then(this::getListSuccess)
                .error((call, e) -> {

                })
                .request();
    }

    private void getListSuccess(Call call, Response response) throws IOException {
        ResponseBody body = response.body();
        assert body != null;
        JSONObject data = JSON.parseObject(body.string());
        switch (data.getString("code")) {
            case "200": {
                Type type = new TypeToken<List<Game>>() {
                }.getType();

                gameList = new Gson().fromJson(data.getString("data"), type);
                setAdapter();
                break;
            }
            case "400": {
                // TODO:获取失败，做提示
                Log.w(TAG, data.getString("msg"));
                break;
            }
            case "500": {
                // TODO:后台出错,做提示
                Log.e(TAG, data.getString("msg"));
                break;
            }
            default: {
                //不知道。。
            }
        }
    }

    public void setAdapter() {
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            GameListAdapter adapter = new GameListAdapter(getActivity(), gameList);
            lvGameList.setAdapter(adapter);
        });
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
        initList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_list, container, false);
        lvGameList = view.findViewById(R.id.game_list);
        lvGameList.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }
}