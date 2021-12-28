package com.cakeman.webfluxwebsocket.enums;


/**
 * @author xzy
 * @description socket事件类型
 * @date 2021/10/26
 */
public enum SocketMsgTypeEnum {

    USER_REGISTER(1, "用户注册",true),
    APPROVAL_SPONSOR(2, "审批发起人",true),
    APPROVER(3, "审批人",true),

    ;

    private int code;
    private String title;
    private Boolean assureSuccess;

    private SocketMsgTypeEnum(int code, String title, Boolean assureSuccess) {
        this.code = code;
        this.title = title;
        this.assureSuccess = assureSuccess;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getAssureSuccess() {
        return assureSuccess;
    }

    public void setAssureSuccess(Boolean assureSuccess) {
        this.assureSuccess = assureSuccess;
    }

    public static SocketMsgTypeEnum getByCode(int code) {
        for (SocketMsgTypeEnum msgContent : SocketMsgTypeEnum.values()) {
            if (msgContent.getCode() == code) {
                return msgContent;
            }
        }
        return null;
    }
}
