package com.example.gameshop.toast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.gameshop.R;

/**
 * @author sheng
 * @date 2021/12/7 14:04
 */
public class ImageTextToast {
    private static Toast toast;
    private final View layout;

    @SuppressLint("InflateParams")
    public ImageTextToast(Context context) {
        if (toast != null) {
            toast.cancel();
        }
        toast = new Toast(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        layout = inflater.inflate(R.layout.toast_layout, null, false);
    }

    public void init(CharSequence text, int resourceId, int duration) {
        // toast上的图片
        ((ImageView) layout.findViewById(R.id.toast_img)).setImageResource(resourceId);

        // toast上的文字
        ((TextView) layout.findViewById(R.id.toast_text)).setText(text);

        toast.setDuration(duration);

        show();
    }


    public void info(CharSequence text) {
        info(text, Toast.LENGTH_SHORT);
    }

    public void success(CharSequence text) {
        success(text, Toast.LENGTH_SHORT);
    }

    public void fail(CharSequence text) {
        fail(text, Toast.LENGTH_SHORT);
    }

    public void error(CharSequence text) {
        error(text, Toast.LENGTH_SHORT);
    }


    public void info(CharSequence text, int duration) {
        init(text, R.mipmap.info_white, duration);
    }

    public void success(CharSequence text, int duration) {
        init(text, R.mipmap.success_white, duration);
    }

    public void fail(CharSequence text, int duration) {
        init(text, R.mipmap.warning_white, duration);
    }

    public void error(CharSequence text, int duration) {
        init(text, R.mipmap.error_white, duration);
    }

    public void show() {
        toast.setView(layout);
        toast.setGravity(Gravity.CENTER, 0, 70);
        toast.show();
    }
}

