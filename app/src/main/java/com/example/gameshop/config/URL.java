package com.example.gameshop.config;

/**
 * @author sheng
 * @date 2021/11/22 16:20
 */
public class URL {
    // 后端接口地址
    public static final String BASE = "http://192.168.23.1:8100";
    // 后端图片地址
    public static final String IMAGE = BASE + "/image/";
    // 获取游戏列表
    public static final String GAME_QUERY = BASE + "/game/query";
    // 根据id获取游戏信息
    public static final String GAME_LIST = BASE + "/game/list";
    // 获取游戏详细信息
    public static final String GAME_INFO = BASE + "/game/info/";
    // 检验token
    public static final String CHECK = BASE + "/user/check";
    // 登录URL
    public static final String LOGIN = BASE + "/user/login";
    // 注册URL
    public static final String REGISTER = BASE + "/user/register";
    // 添加到回收袋
    public static final String ADD_PACK = BASE + "/pack/add";
    // 订单添加的URL
    public static final String ORDER_ADD = BASE + "/user/order/add";
    // 获取用户信息
    public static final String USER_INFO = BASE + "/user/info";
    // 获取回收袋的游戏
    public static final String PACK_GAMES_LIST = BASE + "/pack/list";
    // 选中游戏
    public static final String PACK_SELECTED = BASE + "/pack/selected";
    // 购物车全选
    public static final String PACK_ALL_SELECTED = BASE + "/pack/selected/all";
    // 修改数量
    public static final String PACK_UPDATE_NUM = BASE + "/pack/update";
    // 修改数量
    public static final String PACK_DELETE = BASE + "/pack/delete";
    // 修改用户信息
    public static final String USER_INFO_CHANGE = BASE + "/user/info/update";
}
