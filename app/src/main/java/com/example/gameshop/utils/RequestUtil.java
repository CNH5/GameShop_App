package com.example.gameshop.utils;


import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import okhttp3.*;

import java.io.IOException;

/**
 * 把okhttp改成和axios和ajax差不多的形式
 *
 * @author sheng
 * @date 2021/12/4 13:38
 */
public class RequestUtil {
    private Request request;
    private CallUtil callback;
    private final Context mContext;
    private Error error;

    public RequestUtil(Context mContext) {
        this.mContext = mContext;
    }

    public interface Error {
        void catchError(Call call, IOException e);
    }

    public RequestUtil setCallback(CallUtil callback) {
        this.callback = callback;
        return this;
    }

    public RequestUtil setRequest(Request request) {
        this.request = request;
        return this;
    }

    /**
     * 请求出错时的调用
     */
    public RequestUtil error(Error error) {
        this.error = error;
        return this;
    }

    /**
     * 发送请求
     */
    public void async() {
        Call call = new OkHttpClient().newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (error != null) {
                    error.catchError(call, e);
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                callback.setResponse(response).handle();

                // 如果返回的头中有token，应当更新token
                String token = response.header("token");
                if (token != null) {
                    Log.d("save token", token);
                    new SharedDataUtil(mContext).setToken(token);
                }
            }
        });
    }
}
