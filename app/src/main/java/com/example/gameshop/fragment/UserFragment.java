package com.example.gameshop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.gameshop.R;
import com.example.gameshop.activity.ConfigActivity;
import com.example.gameshop.activity.LoginActivity;
import com.example.gameshop.utils.SharedDataUtil;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment implements View.OnClickListener {
    private ImageView avatarView;
    private TextView nameView;

    public UserFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void init() {
        if (new SharedDataUtil(Objects.requireNonNull(getActivity())).notLogin()) {
            // 没登陆，设置成对应的样子
        } else {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        view.findViewById(R.id.config_bt).setOnClickListener(this);
        view.findViewById(R.id.user_info).setOnClickListener(this);
        view.findViewById(R.id.all_order_bt).setOnClickListener(this);
        view.findViewById(R.id.wait_send_bt).setOnClickListener(this);
        view.findViewById(R.id.wait_receive_bt).setOnClickListener(this);
        view.findViewById(R.id.advice_bt).setOnClickListener(this);
        view.findViewById(R.id.kd_bt).setOnClickListener(this);
        view.findViewById(R.id.cn_bt).setOnClickListener(this);

        nameView = view.findViewById(R.id.name);
        avatarView = view.findViewById(R.id.avatar);

        return view;
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.config_bt) {
            Intent intent = new Intent(getActivity(), ConfigActivity.class);
            Objects.requireNonNull(getActivity()).startActivityForResult(intent, ConfigActivity.CODE);

        } else if (vid == R.id.user_info) {
            if (new SharedDataUtil(Objects.requireNonNull(getActivity())).notLogin()) {
                // 没登陆
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                Objects.requireNonNull(getActivity()).startActivityForResult(intent, LoginActivity.CODE);

            } else {
                // 跳转到个人信息界面
            }

        }
    }
}