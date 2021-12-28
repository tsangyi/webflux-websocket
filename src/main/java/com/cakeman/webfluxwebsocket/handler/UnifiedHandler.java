package com.cakeman.webfluxwebsocket.handler;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cakeman.webfluxwebsocket.client.UnifiedClient;
import com.cakeman.webfluxwebsocket.enums.ClientTagEnum;
import com.cakeman.webfluxwebsocket.msg.RedisTagConstant;
import com.cakeman.webfluxwebsocket.msg.ReplySocketMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @author xzy
 * @description socket通信处理接口
 * @date 2021/10/26
 */
@Slf4j
@Component
public class UnifiedHandler implements WebSocketHandler {

    //    @Resource
//    private RedisService redisService;
    @Resource
    private UnifiedClient unifiedClient;
    @Autowired
    private ObjectFactory<UnifiedSessionHandler> unifiedSessionHandlerFactory;
    private final DirectProcessor<WebSocketSessionHandler> connectedProcessor;

    public UnifiedHandler() {
        connectedProcessor = DirectProcessor.create();
    }


    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Flux<WebSocketMessage> output = session
                .receive() //访问入站消息流。
                .doOnNext(message -> {
                    //对每条消息执行一些操作
                    //对于嵌套的异步操作，您可能需要调用message.retain()使用池化数据缓冲区的基础服务器
                    WebSocketMessage retain = message.retain();
                })
                .concatMap(mapper -> {
                    //执行使用消息内容的嵌套异步操作
                    String msg = mapper.getPayloadAsText();
                    System.out.println("mapper: " + msg);
                    System.out.println("session-code: " + session.hashCode());
                    return Flux.just(msg);
                })
                .map(value -> {
                    // 创建出站消息，生成组合流
                    System.out.println("value: " + value);
                    return session.textMessage("Echo " + value);
                });


        log.info("【进入UnifiedHandler的handle】session:" + session.hashCode());

        UnifiedSessionHandler sessionHandler = unifiedSessionHandlerFactory.getObject();

        log.info("【进入UnifiedHandler的handle】sessionHandler:" + sessionHandler.hashCode());

        sessionHandler.connected().subscribe(value -> {
            log.info("【新连接进入】sessionId：" + value.getId());
        });
        sessionHandler.disconnected().subscribe(value -> {
            log.info("【连接断开】sessionId：" + value.getId());
            unifiedClient.remove(value);
        });
        connectedProcessor.sink().next(sessionHandler);
        return sessionHandler.handle(session);


    }

    @Component
    @Scope("prototype")
    private class UnifiedSessionHandler extends WebSocketSessionHandler {

        @Override
        public void customHandle(String message) {
            log.info("【UnifiedSessionHandler】接收到消息：{}", message);
            try {
                Integer msgType = null;
                String clientTag = "";
                String uniqueId = "";
                String contentObj = "";
                try {
                    JSONObject jsonObject = JSON.parseObject(message);
                    msgType = jsonObject.getInteger("msgType");
                    clientTag = jsonObject.getString("clientTag");
                    uniqueId = jsonObject.getString("uniqueId");
                    contentObj = jsonObject.getString("content");
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    this.send(JSON.toJSONString(ReplySocketMsg.failure(e.getMessage())));
                    return;
                }

                ClientTagEnum clientTagEnum = ClientTagEnum.findTag(clientTag);
                if (clientTagEnum == null) {
                    this.send(JSON.toJSONString(ReplySocketMsg.failure("找不到对应的clientTag,请确认clientTag是否正确")));
                    return;
                }

                if (StringUtils.isEmpty(uniqueId)) {
                    this.send(JSON.toJSONString(ReplySocketMsg.failure("uniqueId不能为空")));
                    return;
                }
                com.huayue.websocket.enums.socket.SocketMsgTypeEnum msgTypeEnum = com.huayue.websocket.enums.socket.SocketMsgTypeEnum.getByCode(msgType);
                if (null == msgTypeEnum) {
                    this.send(JSON.toJSONString(ReplySocketMsg.failure("非法的msgType")));
                    return;
                }
                if (com.huayue.websocket.enums.socket.SocketMsgTypeEnum.USER_REGISTER.equals(msgTypeEnum)) {
                    if (!isAccess(clientTagEnum, uniqueId)) {
                        this.send(JSON.toJSONString(ReplySocketMsg.failure("请先获取准入许可方可建立连接并通信")));
                        return;
                    }
                    log.info("【信息类型:{}】uniqueId：{}", msgTypeEnum.getTitle(), uniqueId);
                    unifiedClient.push(clientTagEnum, uniqueId, this);
                    this.send(JSON.toJSONString(ReplySocketMsg.success()));
                    return;
                }

                if (unifiedClient.get(clientTagEnum, uniqueId) == null) {
                    this.send(JSON.toJSONString(ReplySocketMsg.failure("请先注册之后再发送消息")));
                    return;
                }
//                //消息处理
//                DeliveryService deliveryService = deliveryServiceBeanFactory.getService(msgTypeEnum);
//                boolean result = deliveryService.handle(uniqueId, contentObj);
//                if (!result) {
//                    this.send(JSON.toJSONString(ReplySocketMsg.failure("处理失败")));
//                    return;
//                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                this.send(JSON.toJSONString(ReplySocketMsg.failure(e.getMessage())));
                return;
            }
            this.send(JSON.toJSONString(ReplySocketMsg.success()));
        }

        public boolean isAccess(ClientTagEnum clientTag, String uniqueId) {
            String key = RedisTagConstant.REGISTER_ACCESS_TAG.replace("{clientTag}", clientTag.getClientTag()) + uniqueId;
//            String resultMsg = (String) redisService.get(key);
//            if (Boolean.parseBoolean(resultMsg)) {
//                return true;
//            }
//            return false;

            return true;
        }


    }
}
