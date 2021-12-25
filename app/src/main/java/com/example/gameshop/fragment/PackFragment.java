package com.example.gameshop.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.alibaba.fastjson.JSONObject;
import com.example.gameshop.R;
import com.example.gameshop.activity.GameActivity;
import com.example.gameshop.activity.OrderSubmitActivity;
import com.example.gameshop.adapter.PackGameAdapter;
import com.example.gameshop.config.URL;
import com.example.gameshop.pojo.RecyclePackGame;
import com.example.gameshop.toast.ImageTextToast;
import com.example.gameshop.utils.RequestUtil;
import com.example.gameshop.utils.CallUtil;
import com.example.gameshop.utils.SharedDataUtil;
import okhttp3.*;

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
    private ImageTextToast toast;
    private ImageView allSelectedIcon;
    private TextView totalPriceTextView;
    private TextView countTextView;
    private TextView manageBtText;
    private SwipeRefreshLayout swipe;
    private View gameManagerBt;
    private View settlementView;
    private View manageView;
    private View recycleSwitchBt;
    private View buySwitchBt;
    private TextView recycleSwitchTextView;
    private TextView buySwitchTextView;
    private TextView settlementBtText;
    private boolean isAllSelected;
    private boolean isManageMode;
    private boolean isRecycle = true;
    private PackGameAdapter adapter;
    private SharedDataUtil util;

    private final CallUtil deleteGameCallback = new CallUtil()
            .success((msg, dataJSON) -> {
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                    toast.success(msg);
                });
                adapter.removeSelected();
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

    private final CallUtil getGameCallback = new CallUtil()
            .success((msg, data) -> {
                games = JSONObject.parseArray(data, RecyclePackGame.class);
                setAdapter();
                initView();

                if (swipe != null) {
                    swipe.setRefreshing(false);
                }
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

    private final CallUtil selectedAllCallback = new CallUtil()
            .success((msg, data) -> {
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                    adapter.selectedAll(isAllSelected = !isAllSelected);
                });
            })
            .fail((msg, data) -> {
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                    toast.fail(msg);
                });
            })
            .error((msg, data) -> {
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                    toast.error(msg);
                });
            });

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
    }

    // 获取回收袋中的游戏列表
    void getGameList() {
        if (util.notLogin()) {
            // 设置成中间有一个登录按钮的样式
        } else {
            HttpUrl url = Objects.requireNonNull(HttpUrl.parse(URL.PACK_GAMES_LIST))
                    .newBuilder()
                    .addQueryParameter("account", util.getAccount())
                    .addQueryParameter("type", getType())
                    .build();

            Request request = new Request.Builder().url(url).get().addHeader("token", util.getToken()).build();

            new RequestUtil(getActivity())
                    .setRequest(request)
                    .setCallback(getGameCallback)
                    .error((error, e) -> {
                        e.printStackTrace();
                        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                            toast.error("获取数据失败");
                        });
                    })
                    .async();
        }

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
                if (isRecycle) {
                    count += game.getNum() * (game.getNow_price() - 15);
                } else {
                    count += game.getNum() * game.getNow_price();
                }
            }
        }

        double finalCount = count;
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            setAllSelectedIcon();
            totalPriceTextView.setText("" + finalCount);
        });
    }

    @SuppressLint("SetTextI18n")
    void initTransactionText() {
        countTextView.setText(isRecycle ? "预计可获得" : "合计：");
        settlementBtText.setText("下单" + getType());
    }

    // 设置参数
    private void initParams() {
        toast = new ImageTextToast(getActivity());
        util = new SharedDataUtil(Objects.requireNonNull(getActivity()));
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
                    if (isRecycle) {
                        count += game.getNum() * (game.getNow_price() - 15);
                    } else {
                        count += game.getNum() * game.getNow_price();
                    }
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
        swipe = view.findViewById(R.id.swipe);
        buySwitchBt = view.findViewById(R.id.buy_switch);
        recycleSwitchBt = view.findViewById(R.id.recycle_switch);
        recycleSwitchTextView = view.findViewById(R.id.recycle_switch_text);
        buySwitchTextView = view.findViewById(R.id.buy_switch_text);
        settlementBtText = view.findViewById(R.id.settlement_bt_text);
        NestedScrollView scrollView = view.findViewById(R.id.scrollView);

        gameManagerBt.setOnClickListener(this);
        recycleSwitchBt.setOnClickListener(this);
        buySwitchBt.setOnClickListener(this);
        view.findViewById(R.id.all_selected_bt).setOnClickListener(this);
        view.findViewById(R.id.settlement_bt).setOnClickListener(this);
        view.findViewById(R.id.add_favorite_bt).setOnClickListener(this);
        view.findViewById(R.id.delete_game_bt).setOnClickListener(this);

        initTransactionText();
        swipe.setColorSchemeResources(R.color.blue);
        // 防止下拉刷新组件和滑动组件冲突
        scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            swipe.setEnabled(scrollY == 0);
        });
        swipe.setOnRefreshListener(this::getGameList);
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
        } else if (vid == R.id.recycle_switch) {
            if (!isRecycle) {
                isRecycle = true;
                setType();
                getGameList();
            }
        } else if (vid == R.id.buy_switch) {
            if (isRecycle) {
                isRecycle = false;
                setType();
                getGameList();
            }
        }
    }

    private void addFavorite() {
        toast.error("暂未完成~");
    }

    // 执行全选操作
    private void selectedAll() {
        FormBody form = new FormBody
                .Builder()
                .add("account", util.getAccount())
                .add("type", getType())
                .add("selected", JSONObject.toJSONString(!isAllSelected))
                .build();

        Request request = new Request.Builder()
                .url(Objects.requireNonNull(HttpUrl.parse(URL.PACK_ALL_SELECTED)))
                .addHeader("token", util.getToken())
                .post(form)
                .build();

        new RequestUtil(getActivity())
                .setRequest(request)
                .setCallback(selectedAllCallback)
                .error((call, e) -> {
                    e.printStackTrace();
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                        toast.error("请求出错");
                    });
                }).async();
    }

    private void settlement() {
        if (adapter.getSelectedGames().size() == 0) {
            toast.fail("还没有选中游戏哦~");
        } else {
            // 传递选中的游戏id给结算界面
            ArrayList<Long> idList = new ArrayList<>();
            ArrayList<Integer> numList = new ArrayList<>();
            for (RecyclePackGame game : adapter.getSelectedGames()) {
                idList.add(game.getId());
                numList.add(game.getNum());
            }
            Intent intent = new Intent(getActivity(), OrderSubmitActivity.class)
                    .putExtra("type", getType())
                    .putExtra("id", JSONObject.toJSONString(idList))
                    .putExtra("num", JSONObject.toJSONString(numList));
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
            FormBody form = new FormBody
                    .Builder()
                    .add("account", util.getAccount())
                    .add("type", getType())
                    .add("idList", JSONObject.toJSONString(idList))
                    .build();

            Request request = new Request.Builder()
                    .url(Objects.requireNonNull(HttpUrl.parse(URL.PACK_DELETE)))
                    .addHeader("token", util.getToken())
                    .post(form)
                    .build();

            new RequestUtil(getActivity())
                    .setRequest(request)
                    .setCallback(deleteGameCallback)
                    .error((call, e) -> {
                        e.printStackTrace();
                        toast.error("请求失败");
                    }).async();
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

    private void setType() {
        if (isRecycle) {
            buySwitchTextView.setTextColor(Color.parseColor("#909399"));
            buySwitchBt.setBackgroundColor(Color.WHITE);

            recycleSwitchTextView.setTextColor(Color.WHITE);
            recycleSwitchBt.setBackgroundResource(R.drawable.shape_corner6);

        } else {
            recycleSwitchTextView.setTextColor(Color.parseColor("#909399"));
            recycleSwitchBt.setBackgroundColor(Color.WHITE);

            buySwitchTextView.setTextColor(Color.WHITE);
            buySwitchBt.setBackgroundResource(R.drawable.shape_corner6);
        }
        initTransactionText();
    }

    private String getType() {
        return isRecycle ? "回收" : "购买";
    }
}