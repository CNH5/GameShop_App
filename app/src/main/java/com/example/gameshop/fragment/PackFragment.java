package com.example.gameshop.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gameshop.R;
import com.example.gameshop.activity.GameActivity;
import com.example.gameshop.activity.OrderSubmitActivity;
import com.example.gameshop.adapter.PackGameAdapter;
import com.example.gameshop.config.URL;
import com.example.gameshop.pojo.RecyclePackGame;
import com.example.gameshop.toast.ImageTextToast;
import com.example.gameshop.utils.RequestUtil;
import com.example.gameshop.utils.ResponseUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PackFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "PackFragment";
    private List<RecyclePackGame> games;
    private RecyclerView lvPackGameList;
    // 获取游戏的请求
    private RequestUtil getGameListRequest;
    // 全选的请求
    private RequestUtil selectedAllRequest;
    // 删除选中的请求
    private RequestUtil selectedDeleteRequest;
    private ImageTextToast toast;
    private ImageView allSelectedIcon;
    private TextView totalPriceTextView;
    private TextView countTextView;
    private TextView manageBtText;
    private View gameManagerBt;
    private View settlementView;
    private View manageView;
    private boolean isAllSelected;
    private boolean isManageMode;
    private boolean isRecycle = true;
    private PackGameAdapter adapter;


    public PackFragment() {
        // Required empty public constructor
    }

    public static PackFragment newInstance() {
        return new PackFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParams();
        getGameList();
        initCountText();
    }

    // 获取回收袋中的游戏列表
    void getGameList() {
        getGameListRequest.addQueryParameter("type", getType()).request();
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        isAllSelected = games.size() != 0;
        double count = 0;
        for (RecyclePackGame game : games) {
            if (!game.isSelected() && isAllSelected) {
                isAllSelected = false;
            }
            if (game.isSelected()) {
                count += game.getNum() * game.getNow_price();
            }
        }

        double finalCount = count;
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            setAllSelectedIcon();
            totalPriceTextView.setText("" + finalCount);
        });
    }

    void initCountText() {
        countTextView.setText(isRecycle ? "预计可获得" : "合计：");
    }

    // 设置参数
    private void initParams() {
        toast = new ImageTextToast(getActivity());

        initSelectedAllRequest();
        initGetGameListResponse();
        initSelectedDeleteRequest();
    }

    @SuppressLint("SetTextI18n")
    private void setAdapter() {
        adapter = new PackGameAdapter(getActivity(), games, getType(), toast);
        adapter.setOnGameClick(game -> {
            Intent intent = new Intent(getActivity(), GameActivity.class).putExtra("id", game.getId());
            Objects.requireNonNull(getActivity()).startActivityForResult(intent, GameActivity.CODE);
        });

        adapter.setOnSelectedGamesChange(selected -> {
            Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                isAllSelected = selected.size() == games.size();
                setAllSelectedIcon();
                double count = 0;
                for (RecyclePackGame game : selected) {
                    count += game.getNum() * game.getNow_price();
                }
                totalPriceTextView.setText("" + count);
            });
        });
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> lvPackGameList.setAdapter(adapter));
    }

    void setAllSelectedIcon() {
        allSelectedIcon.setImageResource(isAllSelected ? R.mipmap.selected : R.mipmap.unselected);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pack, container, false);
        lvPackGameList = view.findViewById(R.id.recycle_pack_games);
        lvPackGameList.setLayoutManager(new LinearLayoutManager(getActivity()));
        allSelectedIcon = view.findViewById(R.id.all_selected_icon);
        totalPriceTextView = view.findViewById(R.id.total_price);
        countTextView = view.findViewById(R.id.count_text);
        gameManagerBt = view.findViewById(R.id.recycle_manage_bt);
        manageBtText = view.findViewById(R.id.manage_bt_text);
        settlementView = view.findViewById(R.id.settlement_area);
        manageView = view.findViewById(R.id.manage_area);

        gameManagerBt.setOnClickListener(this);
        view.findViewById(R.id.all_selected_bt).setOnClickListener(this);
        view.findViewById(R.id.settlement_bt).setOnClickListener(this);
        view.findViewById(R.id.add_favorite_bt).setOnClickListener(this);
        view.findViewById(R.id.delete_game_bt).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.all_selected_bt) {
            // 全选按钮
            selectedAll();
        } else if (vid == R.id.recycle_manage_bt) {
            // 右上角的管理按钮
            initManageArea();
        } else if (vid == R.id.settlement_bt) {
            // 结算按钮
            settlement();
        } else if (vid == R.id.add_favorite_bt) {
            // 收藏按钮
            addFavorite();
        } else if (vid == R.id.delete_game_bt) {
            // 删除按钮
            deleteGame();
        }
    }

    private void addFavorite() {

    }

    // 执行全选操作
    private void selectedAll() {
        selectedAllRequest
                .addQueryParameter("type", getType())
                .addQueryParameter("selected", !isAllSelected)
                .request();
    }

    private void settlement() {
        if (adapter.getSelectedGames().size() == 0) {
            toast.fail("还没有选中游戏哦~");
        } else {
            Intent intent = new Intent(getActivity(), OrderSubmitActivity.class);
            Objects.requireNonNull(getActivity()).startActivityForResult(intent, OrderSubmitActivity.CODE);
        }
    }

    private void deleteGame() {
        if (adapter.getSelectedGames().size() == 0) {
            toast.fail("还没有选中游戏哦~");

        } else {
            List<Long> idList = new ArrayList<>();
            for (RecyclePackGame game : adapter.getSelectedGames()) {
                idList.add(game.getId());
            }
            selectedDeleteRequest
                    .addFormParameter("type", getType())
                    .addFormParameter("idList", idList)
                    .request();
        }
    }

    private void initManageArea() {
        if (isManageMode = !isManageMode) {
            gameManagerBt.setBackground(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.shape_corner13));

            manageBtText.setTextColor(Color.parseColor("#409EFF"));
            manageBtText.setTextSize(13);
            manageBtText.setText("管理 x");

            settlementView.setVisibility(View.GONE);
            manageView.setVisibility(View.VISIBLE);

        } else {
            gameManagerBt.setBackground(null);

            manageBtText.setTextSize(15);
            manageBtText.setTextColor(Color.parseColor("#606266"));
            manageBtText.setText("管理");

            settlementView.setVisibility(View.VISIBLE);
            manageView.setVisibility(View.GONE);
        }
    }

    private String getType() {
        return isRecycle ? "回收" : "购买";
    }

    private void initSelectedAllRequest() {
        ResponseUtil requestUtil = new ResponseUtil()
                .afterSuccess(() -> {
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                        adapter.selectedAll(isAllSelected = !isAllSelected);
                    });
                })
                .fail((msg, dataJSON) -> {
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                        toast.fail(msg);
                    });
                })
                .error((msg, dataJSON) -> {
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                        toast.error(msg);
                    });
                });

        selectedAllRequest = new RequestUtil(getActivity())
                .url(URL.PACK_ALL_SELECTED)
                .post()
                .setToken()
                .then((call, response) -> {
                    requestUtil.setResponse(response).handle();
                })
                .error((call, e) -> {
                    e.printStackTrace();
                    toast.error("请求失败");
                });
    }

    private void initGetGameListResponse() {
        ResponseUtil responseUtil = new ResponseUtil()
                .success((msg, dataJSON) -> {
                    Type type = new TypeToken<List<RecyclePackGame>>() {
                    }.getType();

                    games = new Gson().fromJson(dataJSON, type);
                    setAdapter();
                    initView();
                })
                .fail((msg, dataJSON) -> {
                    Log.w(TAG, "fail: " + msg);
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                        toast.fail(msg);
                    });
                })
                .error((msg, dataJSON) -> {
                    Log.e(TAG, "error: " + msg);
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                        toast.error(msg);
                    });
                });

        getGameListRequest = new RequestUtil(getActivity())
                .url(URL.PACK_GAMES_LIST)
                .get()
                .setToken()
                .then((call, response) -> {
                    responseUtil.setResponse(response).handle();
                })
                .error((call, e) -> {
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                        toast.fail("网络异常");
                    });
                    e.printStackTrace();
                });
    }

    void initSelectedDeleteRequest() {
        ResponseUtil responseUtil = new ResponseUtil()
                .success((msg, dataJSON) -> {
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                        toast.success(msg);
                    });
                })
                .afterSuccess(() -> {
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                        for (Integer position : adapter.getSelectedPosition()) {
                            adapter.remove(position);
                        }
                    });
                })
                .fail((msg, dataJSON) -> {
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                        toast.fail(msg);
                    });
                })
                .error((msg, dataJSON) -> {
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                        toast.error(msg);
                    });
                });

        selectedDeleteRequest = new RequestUtil(getActivity())
                .url(URL.PACK_DELETE)
                .post()
                .setToken()
                .then((call, response) -> {
                    responseUtil.setResponse(response).handle();
                })
                .error((call, e) -> {
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                        toast.fail("网络异常");
                    });
                    e.printStackTrace();
                });
    }
}