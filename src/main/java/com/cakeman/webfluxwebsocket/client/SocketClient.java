package com.cakeman.webfluxwebsocket.client;


import com.cakeman.webfluxwebsocket.enums.ClientTagEnum;
import com.cakeman.webfluxwebsocket.handler.WebSocketSessionHandler;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.List;

/**
 * @author xzy
 * @description socket通道处理接口
 * @date 2021/10/29
 */
public interface SocketClient {

    /**
     * 保存通道处理
     *
     * @param clientTag
     * @param socketKey
     * @param sessionHandler
     */
    void push(ClientTagEnum clientTag, String socketKey, WebSocketSessionHandler sessionHandler);

    /**
     * 获取通道处理类
     *
     * @param socketKey
     * @return
     */
    WebSocketSessionHandler get(ClientTagEnum clientTag, String socketKey);

    /**
     * 获取所有通道处理类
     *
     * @return
     */
    List<WebSocketSessionHandler> getAll(ClientTagEnum clientTag);

    /**
     * 移除通道处理和通道
     *
     * @param session
     */
    void remove(WebSocketSession session);

    String getHost(ClientTagEnum clientTag, String uniqueId);
}
