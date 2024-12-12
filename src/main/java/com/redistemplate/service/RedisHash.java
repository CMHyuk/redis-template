package com.redistemplate.service;

import com.redistemplate.common.redis.RedisCommon;
import com.redistemplate.domain.hashes.model.HashModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisHash {

    private final RedisCommon redis;

    public void putInHash(String key, String field, String name) {
        HashModel model = new HashModel(name);
        redis.putInHash(key, field, model);
    }

    public HashModel getFromHash(String key, String field) {
        return redis.getFromHash(key, field, HashModel.class);
    }
}
