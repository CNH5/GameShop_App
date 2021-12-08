package com.example.gameshop.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import com.example.gameshop.R;

/**
 * 本地分享数据获取设置？反正不知道该叫什么，大致包括登陆状态获取和保存token等功能
 *
 * @author sheng
 * @date 2021/12/7 15:59
 */
public class SharedDataUtil {
    private final String accountKey;
    private final String tokenKey;
    private final SharedPreferences spFile;
    private final SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public SharedDataUtil(Context context) {
        String spFileName = context.getResources().getString(R.string.shared_preferences_file_name);
        accountKey = context.getResources().getString(R.string.account);
        tokenKey = context.getResources().getString(R.string.token);
        spFile = context.getSharedPreferences(spFileName, Activity.MODE_PRIVATE);
        editor = spFile.edit();
    }

    public boolean notLogin() {
        return getToken() == null;
    }

    public String getAccount() {
        return spFile.getString(accountKey, null);
    }

    public String getToken() {
        return spFile.getString(tokenKey, null);
    }

    public void cleanToken() {
        setToken(null);
    }

    public void setAccount(String account) {
        editor.putString(accountKey, account);
        editor.apply();
    }

    public void setToken(String token) {
        editor.putString(tokenKey, token);
        editor.apply();
    }
}
