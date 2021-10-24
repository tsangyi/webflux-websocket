package com.cakeman.webfluxwebsocket.client;


import com.cakeman.webfluxwebsocket.handler.WebSocketSessionHandler;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;

/**
 * @BelongsPackage: com.yycx.viop.socket.client
 * @Author: zsx
 * @CreateTime: 2020-03-23 11:56
 * @Description:
 */
@Component
public class UnifiedClient implements SocketClient {

    private static BiMap<String, WebSocketSessionHandler> clientCache = HashBiMap.create();
    private static BiMap<String, WebSocketSession> clientSessionCache = HashBiMap.create();

    @Autowired
    private DeliveryServiceBeanFactory deliveryServiceBeanFactory;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private IssueRPCServerConfig rpcServerConfig;


    private String getCacheKey(ClientTagEnum clientTag, String uniqueId) {
        return clientTag.getClientTag() + "_" + uniqueId;
    }

    private String getRedisKey(ClientTagEnum clientTag, String uniqueId) {
        return RedisTagConstant.UNIQUE_TAG.replace("{clientTag}", clientTag.getClientTag()) + uniqueId;
    }

    private String cacheKey2RedisKey(String cacheKey) {
        String[] cacheKeyArr = cacheKey.split("_");
        return RedisTagConstant.UNIQUE_TAG.replace("{clientTag}", cacheKeyArr[0]) + cacheKeyArr[1];
    }

    @Override
    public void push(ClientTagEnum clientTag, String uniqueId, WebSocketSessionHandler sessionHandler) {
        if (clientCache.get(getCacheKey(clientTag, uniqueId)) != null) {
            clientCache.remove(getCacheKey(clientTag, uniqueId));
            clientSessionCache.remove(getCacheKey(clientTag, uniqueId));
        }
        clientCache.put(getCacheKey(clientTag, uniqueId), sessionHandler);
        clientSessionCache.put(getCacheKey(clientTag, uniqueId), sessionHandler.getSession());
        redisUtil.setValue(getRedisKey(clientTag, uniqueId), IPUtil.getLocalIPAddress() + ":" + rpcServerConfig.getPort());
    }

    @Override
    public WebSocketSessionHandler get(ClientTagEnum clientTag, String uniqueId) {
        return clientCache.getOrDefault(getCacheKey(clientTag, uniqueId), null);
    }

    @Override
    public List<WebSocketSessionHandler> getAll(ClientTagEnum clientTag) {
        List<WebSocketSessionHandler> handlerList = new ArrayList<WebSocketSessionHandler>();
        clientCache.forEach((key, handler) -> {
            if (key.startsWith(clientTag.getClientTag())) {
                handlerList.add(handler);
            }
        });
        return handlerList;
    }

    @Override
    public void remove(WebSocketSession session) {
        String cacheKey = clientSessionCache.inverse().get(session);
        if (null == cacheKey){
            return;
        }
        clientCache.remove(cacheKey);
        clientSessionCache.remove(cacheKey);
        redisUtil.delValue(cacheKey2RedisKey(cacheKey));
        String[] info = cacheKey.split("_");
        if (info[0].equals(ClientTagEnum.DRIVER.getClientTag())) {
            DeliveryService deliveryService = deliveryServiceBeanFactory.getService(SocketMsgTypeEnum.EXPRESS_LISTEN_SINGLE);
            JsonObject content = new JsonObject();
            content.addProperty("isOpen", "false");
            deliveryService.handle(info[1], content);
        }
    }

    @Override
    public String getHost(ClientTagEnum clientTag, String uniqueId) {
        return redisUtil.getValue(getRedisKey(clientTag, uniqueId));
    }
}
