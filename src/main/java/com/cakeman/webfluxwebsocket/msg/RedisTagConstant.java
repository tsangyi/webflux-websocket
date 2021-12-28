package com.cakeman.webfluxwebsocket.msg;


/**
 * @author xzy
 * @description Redis的Key常量
 * @date 2021/10/26
 */
public class RedisTagConstant {

    /**
     * 分隔符
     */
    public static final String SPLIT = ":";

    /**
     * 可以注册的客户端的tag
     */
    public static final String REGISTER_ACCESS_TAG = "hyyc" + SPLIT + "register" + SPLIT + "{clientTag}" + SPLIT;

    /**
     * 数据的tag
     */
    public static final String UNIQUE_TAG = "hyyc" + SPLIT + "socket" + SPLIT + "{clientTag}" + SPLIT;

}
