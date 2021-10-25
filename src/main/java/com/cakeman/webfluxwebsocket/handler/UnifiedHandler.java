package com.cakeman.webfluxwebsocket.handler;


import com.cakeman.webfluxwebsocket.Service.TokenService;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.reactive.socket.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class UnifiedHandler implements WebSocketHandler, CorsConfigurationSource {

    @Autowired
    private TokenService tokenService;

    /**
     * 实现自定义的请求处理类WebSocketHandler
     */
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        // 在生产环境中，需对url中的参数进行检验，如token，不符合要求的连接的直接关闭
        HandshakeInfo handshakeInfo = session.getHandshakeInfo();
        if (handshakeInfo.getUri().getQuery() == null) {
            return session.close(CloseStatus.REQUIRED_EXTENSION);
        } else {
            // 对参数进行解析，在些使用的是jetty-util包
            MultiMap<String> values = new MultiMap<String>();
            UrlEncoded.decodeTo(handshakeInfo.getUri().getQuery(), values, "UTF-8");
            String token = values.getString("token");
            boolean isValidate = tokenService.validate(token);
            if (!isValidate) {
                return session.close();
            }
        }
        Flux<WebSocketMessage> output = session
                .receive() //访问入站消息流。
                .doOnNext(message ->{
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
        return session.send(output);
    }

    /**
     * 直接使自定义的WebSocketHandler实现CorsConfigurationSource接口，并返回一个CorsConfiguration
     */
    @Override
    public CorsConfiguration getCorsConfiguration(ServerWebExchange exchange) {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        return corsConfiguration;
    }
}
