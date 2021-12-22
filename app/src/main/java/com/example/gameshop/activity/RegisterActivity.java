package com.example.gameshop.activity;

import android.content.Intent;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.gameshop.R;
import com.example.gameshop.config.URL;
import com.example.gameshop.toast.ImageTextToast;
import com.example.gameshop.utils.RequestUtil;
import com.example.gameshop.utils.CallBackUtil;
import com.example.gameshop.utils.SharedDataUtil;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;

import java.util.Objects;

import static com.example.gameshop.config.ConstParam.*;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    public static final int CODE = 4;
    private EditText accountET;
    private EditText passwordET;
    private View registerBT;
    private boolean passwordVisible = false;
    private ImageView iSwitch;

    private final CallBackUtil registeredCallback = new CallBackUtil()
            .success((msg, dataJSON) -> {
                new SharedDataUtil(this).setAccount(accountET.getText().toString());
                // 给登录界面返回注册成功
                Intent intent = new Intent()
                        .putExtra("msg", msg)
                        .putExtra("account", accountET.getText().toString());
                setResult(RESULT_OK, intent);
                // 回到上一页，直接关掉就行了
                finish();
            })
            .fail((msg, dataJSON) -> {
                Looper.prepare();
                new ImageTextToast(this).fail(msg);
                Looper.loop();
            })
            .error((msg, dataJSON) -> {
                Looper.prepare();
                new ImageTextToast(this).error(msg);
                Looper.loop();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initParams();
        setListener();
    }

    private void initParams() {
        accountET = findViewById(R.id.account_et);
        passwordET = findViewById(R.id.password_et);
        registerBT = findViewById(R.id.register_bt);
        iSwitch = findViewById(R.id.password_visible_switch);

        accountET.setOnFocusChangeListener(mOnFocusHindHintListener);
        passwordET.setOnFocusChangeListener(mOnFocusHindHintListener);
    }

    private void setListener() {
        findViewById(R.id.back_bt).setOnClickListener(this);
        iSwitch.setOnClickListener(this);
        registerBT.setOnClickListener(this);
        accountET.addTextChangedListener(this);
        passwordET.addTextChangedListener(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        cleanEditTextFocus(ev, this);
        //这个是activity的事件分发，一定要有，不然就不会有任何的点击事件了
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        submitButtonEnabled(this, accountET, passwordET, registerBT);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.back_bt) {
            finish();
        } else if (vid == R.id.password_visible_switch) {
            switchPasswordVisible(passwordVisible = !passwordVisible, iSwitch, passwordET);
        } else if (vid == R.id.register_bt) {
            submit();
        } else {
            new ImageTextToast(this).error("未知按钮");
        }
    }

    private void submit() {
        FormBody form = new FormBody
                .Builder()
                .add("account", accountET.getText().toString())
                .add("password", passwordET.getText().toString())
                .build();

        Request request = new Request.Builder()
                .url(Objects.requireNonNull(HttpUrl.parse(URL.PACK_GAMES_LIST)))
                .post(form)
                .build();

        new RequestUtil().setContext(this).setRequest(request).setCallback(registeredCallback).async();
    }

}