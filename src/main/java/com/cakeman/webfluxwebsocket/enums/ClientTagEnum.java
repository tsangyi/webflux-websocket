package com.cakeman.webfluxwebsocket.enums;

/**
 * @author tsangyi
 * @description 客户端类型
 * @date 2021/10/26
 */
public enum ClientTagEnum {

    USER("USER"),
    ;

    private String clientTag;

    private ClientTagEnum(String tag) {
        this.clientTag = tag;
    }

    public String getClientTag() {
        return this.clientTag;
    }

    public static ClientTagEnum findTag(String tag) {
        for (ClientTagEnum tagEnum : ClientTagEnum.values()) {
            if (tagEnum.getClientTag().equals(tag)) {
                return tagEnum;
            }
        }
        return null;
    }

}
