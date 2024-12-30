package com.redistemplate.common.redis;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import com.redistemplate.domain.strategy.model.ValueWithTTL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
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

    @Value("${spring.data.redis.default-time}")
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

    public <T> ValueWithTTL<T> getValueWithTTl(String key, Class<T> clazz) {
        T value = null;
        Long ttl = null;

        try {

            List<Object> results = template.executePipelined(new RedisCallback<Object>() {
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    StringRedisConnection conn = (StringRedisConnection) connection;
                    conn.get(key);
                    conn.ttl(key);

                    return null;
                }
            });

            value = gson.fromJson((String) results.get(0), clazz);
            ttl = (Long) results.get(1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ValueWithTTL(value, ttl);
    }

    public Long sumTwoKeyAndRenew(String key1, String key2, String resultKey) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();

        redisScript.setLocation(new ClassPathResource("/lua/newKey.lua"));
        redisScript.setResultType(Long.class);

        List<String> keys = Arrays.asList(key1, key2, resultKey);

        return template.execute(redisScript, keys);
    }

    public boolean limitRequest(String userId) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();

        redisScript.setLocation(new ClassPathResource("/lua/rate_limiter.lua"));
        redisScript.setResultType(Long.class);

        String key = "rate_limiter:" + userId;
        long bucketCapacity = 10;       // 1분에 최대 10개의 요청 허용
        double refillRate = 10.0 / 60;  // 초당 0.1667개의 토큰 추가
        long intervalInSeconds = 1;     // 리필 간격
        long currentTime = Instant.now().getEpochSecond();

        // Redis Lua 스크립트 실행
        Long result = template.execute(
                redisScript,
                Collections.singletonList(key), // KEYS[1]
                String.valueOf(bucketCapacity),  // ARGV[1] - String으로 변환
                String.valueOf(refillRate),      // ARGV[2]
                String.valueOf(currentTime),     // ARGV[3]
                String.valueOf(intervalInSeconds) // ARGV[4]
        );

        log.info("result = {} ", result);

        return result != null && result == 1;
    }

}

