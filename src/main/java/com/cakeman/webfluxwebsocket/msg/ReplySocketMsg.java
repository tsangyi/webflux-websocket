package com.cakeman.webfluxwebsocket.msg;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author xzy
 * @description 返回回复socket消息
 * @date 2021/10/26
 */
@Getter
@Setter
public class ReplySocketMsg {

    private String msgType;

    private Msg content;

    @Getter
    @Setter
    @AllArgsConstructor
    private static class Msg {

        private int code;
        private String msg;

    }

    /**
     * 成功
     *
     * @return
     */
    public static ReplySocketMsg success() {
        ReplySocketMsg replyMsg = new ReplySocketMsg();
        replyMsg.setMsgType("ACK");
        replyMsg.setContent(new Msg(1,"成功"));
        return replyMsg;
    }

    /**
     * 失败
     *
     * @return
     */
    public static ReplySocketMsg failure() {
        ReplySocketMsg replyMsg = new ReplySocketMsg();
        replyMsg.setMsgType("ACK");
        replyMsg.setContent(new Msg(0,"失败"));
        return replyMsg;
    }

    /**
     * 失败
     *
     * @param msg 失败的内容
     * @return
     */
    public static ReplySocketMsg failure(String msg) {
        ReplySocketMsg replyMsg = new ReplySocketMsg();
        replyMsg.setMsgType("ACK");
        replyMsg.setContent(new Msg(0,msg));
        return replyMsg;
    }

}
