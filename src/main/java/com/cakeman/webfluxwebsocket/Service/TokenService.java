package com.cakeman.webfluxwebsocket.Service;


import org.springframework.stereotype.Service;

@Service
public class TokenService {

    // demo演示，在引只对长度做校验
    public boolean validate(String token) {
        if (token.length() > 5) {
            return true;
        }
        return false;
    }
}
