package com.example.gameshop.activity;

import android.content.Intent;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.gameshop.config.URL;
import com.example.gameshop.R;
import com.example.gameshop.toast.ImageTextToast;
import com.example.gameshop.utils.RequestUtil;
import com.example.gameshop.utils.ResponseUtil;
import com.example.gameshop.utils.SharedDataUtil;
import okhttp3.Response;

import java.io.IOException;

import static com.example.gameshop.config.ConstParam.*;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    public static final int CODE = 3;
    private EditText accountET;
    private EditText passwordET;
    private View loginBT;
    private boolean passwordVisible = false;
    private ImageView iSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initParams();
        setListener();
    }

    private void initParams() {
        accountET = findViewById(R.id.account_et);
        passwordET = findViewById(R.id.password_et);
        loginBT = findViewById(R.id.login_bt);
        iSwitch = findViewById(R.id.password_visible_switch);

        accountET.setText(new SharedDataUtil(this).getAccount());

        accountET.setOnFocusChangeListener(mOnFocusHindHintListener);
        passwordET.setOnFocusChangeListener(mOnFocusHindHintListener);
    }

    private void setListener() {
        findViewById(R.id.back_bt).setOnClickListener(this);
        findViewById(R.id.register_intent_bt).setOnClickListener(this);
        findViewById(R.id.forget_password_bt).setOnClickListener(this);
        iSwitch.setOnClickListener(this);
        loginBT.setOnClickListener(this);
        accountET.addTextChangedListener(this);
        passwordET.addTextChangedListener(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 如果焦点从输入框切换到其它，则隐藏键盘，同时EditText取消焦点
        cleanEditTextFocus(ev, this);
        // 这个是activity的事件分发，一定要有，不然就不会有任何的点击事件了
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // TODO:在输入有误的时候做提示
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        submitButtonEnabled(this, accountET, passwordET, loginBT);
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
        } else if (vid == R.id.login_bt) {
            submit();
        } else if (vid == R.id.register_intent_bt) {
            intentToRegister();
        } else if (vid == R.id.forget_password_bt) {
            new ImageTextToast(this).error("功能暂未实现");
        }
    }

    private void intentToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent, RegisterActivity.CODE);
    }

    // 提交注册表
    private void submit() {
        new RequestUtil(this)
                .post()
                .url(URL.LOGIN)
                .addFormParameter("account", accountET.getText().toString())
                .addFormParameter("password", passwordET.getText().toString())
                .then((call, response) -> {
                    onResponseSuccess(response);
                })
                .error((call, e) -> {
                    e.printStackTrace();
                    new ImageTextToast(this).error("请求失败");
                })
                .request();
    }

    // 后台有应答时的操作
    private void onResponseSuccess(Response response) throws IOException {
        new ResponseUtil(response)
                .success((msg, dataJSON) -> {
                    new SharedDataUtil(this).setAccount(accountET.getText().toString());
                    // 给上一级页面返回登录成功
                    setResult(RESULT_OK, new Intent().putExtra("msg", msg));
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
                })
                .handle();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RegisterActivity.CODE) {
            assert data != null;
            // 刚注册完，需要清空已经填写的账号密码
            accountET.setText("");
            passwordET.setText("");
            new ImageTextToast(this).success(data.getStringExtra("msg"));
        }
    }
}