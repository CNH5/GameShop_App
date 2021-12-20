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
public class ResponseUtil {
    private Response response;
    private Success success;
    private Fail fail;
    private Error error;
    private AfterSuccess afterSuccess;
    private AfterNotSuccess afterNotSuccess;

    public interface AfterSuccess {
        void after();
    }

    public interface AfterNotSuccess {
        void after();
    }

    public interface Success {
        void onSuccess(String msg, String dataJSON);
    }

    public interface Fail {
        void onFail(String msg, String dataJSON);
    }

    public interface Error {
        void onError(String msg, String dataJSON);
    }

    public ResponseUtil() {

    }

    public ResponseUtil(Response response) {
        this.response = response;
    }

    public ResponseUtil setResponse(Response response) {
        this.response = response;
        return this;
    }

    /**
     * 设置当后端返回code为success时执行的操作
     */
    public ResponseUtil success(Success success) {
        this.success = success;
        return this;
    }

    public ResponseUtil afterSuccess(AfterSuccess afterSuccess) {
        this.afterSuccess = afterSuccess;
        return this;
    }

    public ResponseUtil afterNotSuccess(AfterNotSuccess afterNotSuccess) {
        this.afterNotSuccess = afterNotSuccess;
        return this;
    }

    /**
     * 设置当后端返回code为fail时执行的操作
     */
    public ResponseUtil fail(Fail fail) {
        this.fail = fail;
        return this;
    }

    /**
     * 设置当后端返回code为error时执行的操作
     */
    public ResponseUtil error(Error error) {
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
        switch (data.getString("code")) {
            case "200":
                // 请求成功
                if (success != null) {
                    success.onSuccess(data.getString("msg"), data.getString("data"));
                }
                // 请求成功之后执行的操作
                if (afterSuccess != null) {
                    afterSuccess.after();
                }
                break;
            case "400":
                // 获取失败
                if (fail != null) {
                    fail.onFail(data.getString("msg"), data.getString("data"));
                }
                // 出错之后的操作
                if (afterNotSuccess != null) {
                    afterNotSuccess.after();
                }
                break;
            case "500":
                // 后台出错
                if (error != null) {
                    error.onError(data.getString("msg"), data.getString("data"));
                }
                // 出错之后的操作
                if (afterNotSuccess != null) {
                    afterNotSuccess.after();
                }
                break;
            default: {
                // TODO:还应该添加自定义信号的支持
            }
        }
    }
}
