package com.example.gameshop.utils;


import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import com.alibaba.fastjson.JSONObject;
import com.example.gameshop.config.URL;
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
    private MultipartBody.Builder formBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
    private final SharedDataUtil util;
    private String url;
    private boolean isPost = false;
    private boolean token = false;
    private Success success;
    private Error error;

    public interface Success {
        void response(Call call, Response response) throws IOException;
    }

    public interface Error {
        void catchError(Call call, IOException e);
    }

    public RequestUtil(Context context) {
        util = new SharedDataUtil(context);
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
        this.url = url;
        this.urlBuilder = Objects.requireNonNull(HttpUrl.parse(URL.BASE + url)).newBuilder();
        return this;
    }

    /**
     * get请求添加参数,需要先设置url
     */
    public RequestUtil addQueryParameter(@NonNull String name, Object value) {
        // 直接转String会出问题...
        urlBuilder.addQueryParameter(name, value instanceof String ? (String) value : JSONObject.toJSONString(value));
        return this;
    }

    /**
     * post请求添加表单类型参数
     */
    public RequestUtil addFormParameter(@NonNull String name, Object value) {
        if (value instanceof String) {
            formBuilder.addFormDataPart(name, (String) value);

        } else {
            formBuilder.addFormDataPart(name, JSONObject.toJSONString(value));
        }
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
     * 设置请求携带token,需要在设置url和post或get之后使用
     */
    public RequestUtil setToken() {
        // 设置请求头
        token = true;
        if (util.getToken() != null) {
            reqBuild.addHeader("token", util.getToken());
            // 携带token,必定需要带上账号
            if (isPost) {
                formBuilder.addFormDataPart("account", util.getAccount());
            } else {
                urlBuilder.addQueryParameter("account", util.getAccount());
            }
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

                // 如果返回的头中有token，应当更新token
                String token = response.header("token");
                if (token != null) {
                    Log.d("save token", token);
                    util.setToken(token);
                }
            }
        });

        // 清空表单内容...
        this.urlBuilder = Objects.requireNonNull(HttpUrl.parse(URL.BASE + this.url)).newBuilder();
        formBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (token) {
            setToken();
        }
    }
}
