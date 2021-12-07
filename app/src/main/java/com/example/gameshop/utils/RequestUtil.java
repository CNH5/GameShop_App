package com.example.gameshop.utils;


import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import com.example.gameshop.Constants;
import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.util.Objects;

/**
 * 把okhttp改成和axios和ajax差不多的形式
 *
 * @author sheng
 * @date 2021/12/4 13:38
 */
public class RequestUtil {
    private final Request.Builder reqBuild = new Request.Builder();
    private HttpUrl.Builder urlBuilder;
    private final FormBody.Builder formBuilder = new FormBody.Builder();
    public static final Gson GSON = new Gson();
    private boolean isPost = false;
    private Success success;
    private Error error;

    public interface Success {
        void response(Call call, Response response) throws IOException;
    }

    public interface Error {
        void catchError(Call call, IOException e);
    }

    /**
     * 设置请求类型为get
     */
    public RequestUtil get() {
        this.isPost = false;
        this.reqBuild.get();
        return this;
    }

    /**
     * 设置请求类型为post
     */
    public RequestUtil post() {
        this.isPost = true;
        return this;
    }

    /**
     * 设置url
     */
    public RequestUtil url(String url) {
        this.urlBuilder = Objects.requireNonNull(HttpUrl.parse(Constants.BASE_URL + url)).newBuilder();
        return this;
    }

    /**
     * get请求添加参数
     */
    public RequestUtil addQueryParameter(@NonNull String name, Object value) {
        // 直接转String会出问题...
        urlBuilder.addQueryParameter(name, value instanceof String ? (String) value : GSON.toJson(value));
        return this;
    }

    /**
     * post请求添加参数
     */
    public RequestUtil addRequestParameter(@NonNull String name, Object value) {
        formBuilder.add(name, value instanceof String ? (String) value : GSON.toJson(value));
        return this;
    }

    /**
     * 获取数据成功时的操作
     */
    public RequestUtil then(Success success) {
        this.success = success;
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
     * 设置请求携带token,需要在设置post或get之后使用
     */
    public RequestUtil setToken(Context context) {
        // 获取分享文件?好像是这么叫的
        SharedDataUtil util = new SharedDataUtil(context);

        reqBuild.addHeader("token", util.getToken());
        // 携带token,必定需要带上账号
        if (isPost) {
            formBuilder.add("account", util.getAccount());
        } else {
            urlBuilder.addQueryParameter("account", util.getAccount());
        }
        return this;
    }

    /**
     * 发送请求
     */
    public void request() {
        HttpUrl url = urlBuilder.build();
        reqBuild.url(url);
        Log.d("request url", url.toString());
        if (isPost) {
            reqBuild.post(formBuilder.build());
        }
        Call call = new OkHttpClient().newCall(reqBuild.build());

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (error != null) {
                    error.catchError(call, e);
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (success != null) {
                    success.response(call, response);
                }
            }
        });
    }
}
