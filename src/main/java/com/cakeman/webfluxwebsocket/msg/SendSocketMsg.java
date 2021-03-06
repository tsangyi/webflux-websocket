package com.cakeman.webfluxwebsocket.msg;

import lombok.Getter;
import lombok.Setter;

/**
 * @author tsangyi
 * @description 发送socket消息体
 * @date 2021/10/26
 */
@Setter
@Getter
public class SendSocketMsg {
    private Long sendTime;
    private Msg msg;


    @Setter
    @Getter
    public static class Msg {
        private String clientTag;
        private int msgType;
        private Long uniqueId;
        private String content;
    }
}
