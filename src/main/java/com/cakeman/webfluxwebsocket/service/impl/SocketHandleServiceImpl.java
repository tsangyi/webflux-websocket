package com.cakeman.webfluxwebsocket.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cakeman.webfluxwebsocket.client.UnifiedClient;
import com.cakeman.webfluxwebsocket.enums.ClientTagEnum;
import com.cakeman.webfluxwebsocket.handler.WebSocketSessionHandler;
import com.cakeman.webfluxwebsocket.service.SocketHandleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author: tsangyi
 * @description:
 * @date: 2021/12/28
 */
@Slf4j
@Service
public class SocketHandleServiceImpl implements SocketHandleService {


    @Autowired
    private UnifiedClient unifiedClient;

    @Override
    public void handle(String msg) {


        JSONObject msgObj = JSON.parseObject(msg);
        String clientTag = msgObj.getString("clientTag");
        String uniqueId = msgObj.getString("uniqueId");

        WebSocketSessionHandler handler = unifiedClient.get(ClientTagEnum.findTag(clientTag), uniqueId);

        if (handler == null) {
            throw new RuntimeException("无法获取到对应" + clientTag + uniqueId + "的Handler,请检查socket是否断开连接");
        }

        handler.send(msg);
    }
}
