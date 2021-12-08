package com.example.gameshop.config;

/**
 * @author sheng
 * @date 2021/11/22 16:20
 */
public class URL {
    // 后端接口地址
    public static final String BASE_URL = "http://192.168.23.1:8100";
    // 后端图片地址
    public static final String IMAGE_URL = BASE_URL + "/image/";
    // 获取游戏列表的URL
    public static final String GAME_LIST_URL = "/game/info/list";
    // 获取游戏详细信息的URL
    public static final String GAME_INFO_URL = "/game/info";
    // 用于检验token的URL
    public static final String CHECK_URL = "/user/check";
    // 登录用的URL
    public static final String LOGIN_URL = "/user/login";
    // 注册用的URL
    public static final String REGISTER_URL = "/user/register";
}
