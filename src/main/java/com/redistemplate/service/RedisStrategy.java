package com.redistemplate.service;

import com.redistemplate.common.redis.RedisCommon;
import com.redistemplate.domain.strategy.model.ValueWithTTL;
import com.redistemplate.domain.string.model.StringModel;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class RedisStrategy {

    private final RedisCommon redis;

    public StringModel simpleStrategy(String key) {
        StringModel model = redis.getData(key, StringModel.class);

        if (model == null ){
            // DB를 조회한 값이라고 가정
            StringModel fromDBData = new StringModel(key,"new db");

            redis.setData(key, fromDBData);

            return fromDBData;
        }

        return model;
    }

    public StringModel perStrategy(String key) {
        ValueWithTTL<StringModel> valueWithTTL = redis.getValueWithTTl(key, StringModel.class);

        if (valueWithTTL != null) {
            asyncPERStrategy(key, valueWithTTL.getTtl());

            return valueWithTTL.getValue();
        }

        StringModel fromDBData = new StringModel(key,"new db");

        redis.setData(key, fromDBData);

        return fromDBData;
    }


    @Async
    public void asyncPERStrategy(String key, Long reaminTTL ) {
        double probability = calculateProbability(reaminTTL);

        Random random = new Random();

        if (random.nextDouble() < probability) {
            StringModel fromDB = new StringModel(key, "db from");
            redis.setData(key, fromDB);
        }

    }

    private double calculateProbability(Long remainTTl) {
        double base = 0.5;
        double decayRate = 0.1;

        return base * Math.pow(Math.E, -decayRate * remainTTl);
    }

    public void luaScript(String key1, String key2, String newKey) {
        redis.sumTwoKeyAndRenew(key1, key2, newKey);
    }

}
