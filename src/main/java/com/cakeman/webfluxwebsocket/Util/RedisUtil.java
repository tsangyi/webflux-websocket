package com.cakeman.webfluxwebsocket.Util;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author tsangyi
 * @description Redis的Key常量
 * @date 2021/10/26
 */
@Component
public class RedisUtil {

    /**
     * 分隔符
     */
    public static final String SPLIT = ":";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 增加键/值到Redis数据库
     *
     * @param key   键
     * @param value 值
     */
    public void setValue(String key, String value) {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set(key, value);
    }

    /**
     * 增加键/值到Redis数据库，并设置过期时间
     *
     * @param key    键
     * @param value  值
     * @param second 过期时间，单位是：秒
     */
    public void setValue(String key, String value, int second) {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set(key, value, second, TimeUnit.SECONDS);
    }

    /**
     * 增加键/值到Redis数据库，设置在某一固定时间过期
     *
     * @param key
     * @param value
     * @param date
     */
    public void setValue(String key, String value, Date date) {
        long second = (date.getTime() - System.currentTimeMillis()) / 1000;
        //设置的时间小于当前时间，则表示该键值已经是过期的，从Redis中删除
        if (second < 0) {
            delValue(key);
            return;
        }
        setValue(key, value, (int) second);
    }

    /**
     * 从Redis数据库获取值
     *
     * @param key 键
     * @return 返回键值
     */
    public String getValue(String key) {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        return ops.get(key);
    }

    /**
     * 从Redis数据库获取值,并重置它的过期时间
     *
     * @param key          键
     * @param expireSecond 新的过期时间，单位：秒
     * @return 返回键值
     */
    public String getValue(String key, int expireSecond) {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        String value = ops.get(key);
        stringRedisTemplate.expire(key, expireSecond, TimeUnit.SECONDS);
        return value;
    }

    /**
     * 重置键值过期时间
     *
     * @param key    键
     * @param second 值
     * @return 是否成功设置过期时间
     */
    public Boolean expire(String key, int second) {
        return stringRedisTemplate.expire(key, second, TimeUnit.SECONDS);
    }

    /**
     * 删除键值
     *
     * @param key 键
     */
    public Boolean delValue(String key) {
        return stringRedisTemplate.delete(key);
    }

}
