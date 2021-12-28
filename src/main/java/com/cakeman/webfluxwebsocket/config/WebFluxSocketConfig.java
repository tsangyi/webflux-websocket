package com.cakeman.webfluxwebsocket.config;

import com.cakeman.webfluxwebsocket.handler.UnifiedHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xzy
 * @description websocket配置
 * @date 2021/10/25
 */
@Configuration
public class WebFluxSocketConfig {

    @Resource
    private UnifiedHandler unifiedHandler;

    @Bean
    public HandlerMapping socketMapping() {
        Map<String, WebSocketHandler> map = new HashMap<String, WebSocketHandler>(4);
        map.put("/unified", unifiedHandler);
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
        mapping.setUrlMap(map);
        return mapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
