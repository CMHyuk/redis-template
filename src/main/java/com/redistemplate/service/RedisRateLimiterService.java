package com.redistemplate.service;

import com.redistemplate.common.redis.RedisCommon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisRateLimiterService {

    private final RedisCommon redisCommon;

    public boolean isRequestAllowed(String userId) {
       return redisCommon.limitRequest(userId);
    }

}
