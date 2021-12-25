package com.example.gameshop.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;

/**
 * 后端的格式: {code:"", msg:"", data:""}
 *
 * @author sheng
 * @date 2021/12/5 15:04
 */
public class CallUtil {
    private Response response;
    private Success success;
    private Fail fail;
    private Error error;

    public interface Success {
        void onSuccess(String msg, String data);
    }

    public interface Fail {
        void onFail(String msg, String data);
    }

    public interface Error {
        void onError(String msg, String data);
    }

    public CallUtil() {

    }

    public CallUtil(Response response) {
        this.response = response;
    }

    public CallUtil setResponse(Response response) {
        this.response = response;
        return this;
    }

    /**
     * 设置当后端返回code为success时执行的操作
     */
    public CallUtil success(Success success) {
        this.success = success;
        return this;
    }

    /**
     * 设置当后端返回code为fail时执行的操作
     */
    public CallUtil fail(Fail fail) {
        this.fail = fail;
        return this;
    }

    /**
     * 设置当后端返回code为error时执行的操作
     */
    public CallUtil error(Error error) {
        this.error = error;
        return this;
    }

    /**
     * 处理response
     */
    public void handle() throws IOException {
        ResponseBody body = response.body();
        assert body != null;
        // 因为后端用了个工具类，不这样弄就很麻烦
        JSONObject data = JSON.parseObject(body.string());
        if (data.getString("error") != null) {
            if (error != null) {
                error.onError("请求失败", null);
            }
            return;
        }

        switch (data.getString("code")) {
            case "200":
                // 请求成功
                if (success != null) {
                    success.onSuccess(data.getString("msg"), data.getString("data"));
                }
                break;
            case "400":
                // 获取失败
                if (fail != null) {
                    fail.onFail(data.getString("msg"), data.getString("data"));
                }
                break;
            case "500":
                // 后台出错
                if (error != null) {
                    error.onError(
                            data.getString("msg"),
                            data.getString("data")
                    );
                }
                break;
            default: {
                // TODO:还应该添加自定义信号的支持
            }
        }
    }
}
