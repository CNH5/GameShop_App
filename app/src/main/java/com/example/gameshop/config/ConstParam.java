package com.example.gameshop.config;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import com.example.gameshop.R;

import java.util.regex.Pattern;

/**
 * @author sheng
 * @date 2021/12/8 20:46
 */
public class ConstParam {
    public static final Pattern ACCOUNT = Pattern.compile("([0-9]|[a-z]|[A-Z]|[_@#]){4,20}");
    public static final Pattern PASSWORD = Pattern.compile("([0-9]|[a-z]|[A-Z]|[_@#]){4,20}");

    // 设置输入框获取焦点时hint为空
    public static final View.OnFocusChangeListener mOnFocusHindHintListener = (v, hasFocus) -> {
        EditText textView = (EditText) v;
        String hint;
        if (hasFocus) {
            hint = textView.getHint().toString();
            textView.setTag(hint);
            textView.setHint("");
        } else {
            hint = textView.getTag().toString();
            textView.setHint(hint);
        }
    };

    //判断是否要收起键盘
    public static boolean shouldHideKeyboard(View v, MotionEvent event) {
        //如果目前得到焦点的这个view是editText的话进行判断点击的位置
        if (v instanceof EditText) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            // 点击EditText的事件，忽略它。
            return !(event.getX() > left) || !(event.getX() < right)
                    || !(event.getY() > top) || !(event.getY() < bottom);
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上
        return false;
    }

    //如果焦点从输入框切换到其它，则隐藏键盘，同时EditText取消焦点
    public static void cleanEditTextFocus(MotionEvent ev, Activity activity) {
        //如果是点击事件，获取点击的view，并判断是否要收起键盘
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            //获取目前得到焦点的view
            View v = activity.getCurrentFocus();
            //判断是否要收起并进行处理
            if (shouldHideKeyboard(v, ev)) {
                InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                v.clearFocus();
            }
        }
    }

    public static void switchPasswordVisible(boolean status, ImageView switcher, EditText passEdit) {
        if (status) {
            switcher.setImageResource(R.mipmap.eye);
            passEdit.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            switcher.setImageResource(R.mipmap.eye_close);
            passEdit.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
            passEdit.setTypeface(Typeface.DEFAULT);
        }
    }

    public static void submitButtonEnabled(Context context, EditText account, EditText password, View button) {
        if (ACCOUNT.matcher(account.getText().toString()).matches()
                && PASSWORD.matcher(password.getText().toString()).matches()) {
            button.setEnabled(true);
            button.setBackground(context.getDrawable(R.drawable.shape_corner3));
        } else {
            button.setEnabled(false);
            button.setBackground(context.getDrawable(R.drawable.shape_corner4));
        }
    }
}
