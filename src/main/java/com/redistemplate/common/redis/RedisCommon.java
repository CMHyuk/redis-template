package com.redistemplate.common.redis;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisCommon {
    private final RedisTemplate<String, String> template;
    private final Gson gson;

    @Value("${spring.redis.default-time}")
    private Duration defaultExpireTime;

    public <T> T getData(String key, Class<T> clazz) {
        String jsonValue = template.opsForValue().get(key);
        if (jsonValue == null) { 
            return null;
        } 

        return gson.fromJson(jsonValue, clazz);
    }

    public <T> void setData(String key, T value) {
        String jsonValue = gson.toJson(value);
        template.opsForValue().set(key, jsonValue);
        template.expire(key, defaultExpireTime);
    }

    public <T> void multiSetData(Map<String, T> datas) {
        Map<String, String> jsonMap = new HashMap<>();

        for (Map.Entry<String, T> entry : datas.entrySet()) {
            jsonMap.put(entry.getKey(), gson.toJson(entry.getValue()));
        }

        template.opsForValue().multiSet(jsonMap);
    }


    public <T> void addToSortedSet(String key, T value, Float score) {
        String jsonValue = gson.toJson(value);
        template.opsForZSet().add(key, jsonValue, score);
    }

    public <T> Set<T> rangeByScore(String key, Float minScore, Float maxScore, Class<T> clazz) {
        Set<String> jsonValues = template.opsForZSet().rangeByScore(key, minScore, maxScore);
        Set<T> resultSet = new HashSet<T>();

        if (jsonValues != null) {
            for (String jsonValue : jsonValues) {
                T v = gson.fromJson(jsonValue, clazz);
                resultSet.add(v);
            }
        }

        return resultSet;
    }

    public <T> List<T> getTopNFromSortedSet(String key, int n, Class<T> clazz) {
        Set<String> jsonValues = template.opsForZSet().reverseRange(key, 0, n-1);
        List<T> resultSet = new ArrayList<T>();

        if (jsonValues != null) {
            for (String jsonValue : jsonValues) {
                T v = gson.fromJson(jsonValue, clazz);
                resultSet.add(v);
            }
        }

        return resultSet;
    }

    
    public <T> void addToListLeft(String key, T value) {
        String jsonValue = gson.toJson(value);
        template.opsForList().leftPush(key, jsonValue);   
    }

    public <T> void addToListRight(String key, T value) {
        String jsonValue = gson.toJson(value);
        template.opsForList().rightPush(key, jsonValue);
    }

    public <T> List<T> getAllList(String key, Class<T> clazz) {
        List<String> jsonValues = template.opsForList().range(key, 0, -1);
        List<T> resultSet = new ArrayList<>();


        if (jsonValues != null ) {
            for (String jsonValue : jsonValues) {
                T value = gson.fromJson(jsonValue, clazz);
                resultSet.add(value);
            }
        }

        return resultSet;
    }

    public <T> void removeFromList(String key, T value) {
        String jsonValue = gson.toJson(value);
        template.opsForList().remove(key, 1, jsonValue);
    }

    public <T> void putInHash(String key, String field, T value) {
        String jsonValue = gson.toJson(value);
        template.opsForHash().put(key, field, jsonValue);
    }   

    public <T> T getFromHash(String key, String field, Class<T> clazz) {
        Object result  = template.opsForHash().get(key, field);

        if (result != null) {
            return gson.fromJson(result.toString(), clazz);
        }

        return null;
    }

    public void removeFromHash(String key, String field) {
        template.opsForHash().delete(key, field);
    }


    public void setBit(String key, long offset, boolean value) {
        template.opsForValue().setBit(key, offset, value);
    }

    public boolean getBit(String key, long offset) {
        return template.opsForValue().getBit(key, offset);
    }

    public Long sumTwoKeyAndRenew(String key1, String key2, String resultKey) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();

        redisScript.setLocation(new ClassPathResource("/lua/newKey.lua"));
        redisScript.setResultType(Long.class);

        List<String> keys = Arrays.asList(key1, key2, resultKey);

        return template.execute(redisScript, keys);
    }


}

