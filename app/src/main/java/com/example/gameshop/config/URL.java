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
    public static final String GAME_LIST = "/game/list";
    // 获取游戏详细信息
    public static final String GAME_INFO = "/game/info/";
    // 检验token
    public static final String CHECK = "/user/check";
    // 登录URL
    public static final String LOGIN = "/user/login";
    // 注册URL
    public static final String REGISTER = "/user/register";
    // 添加到回收袋
    public static final String ADD_PACK = "/recycle_pack/add";
    // 订单添加的URL
    public static final String ORDER_ADD = "/user/order/add";
    // 获取用户信息
    public static final String USER_INFO = "/user/info";
    // 获取回收袋的游戏
    public static final String RECYCLE_PACK_GAMES_LIST = "/recycle_pack/list";
    // 选中游戏
    public static final String RECYCLE_PACK_SELECTED_GAME = "/recycle_pack/change";
    // 修改数量
    public static final String RECYCLE_PACK_UPDATE_NUM = "/recycle_pack/update";
    // 修改用户信息
    public static final String USER_INFO_CHANGE = "/user/info/update";
}
