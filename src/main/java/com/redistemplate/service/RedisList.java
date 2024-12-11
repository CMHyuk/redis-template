package com.redistemplate.service;

import com.redistemplate.common.redis.RedisCommon;
import com.redistemplate.domain.list.model.ListModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisList {

    private final RedisCommon redis;

    public void addToListLeft(String key, String name) {
        ListModel model = new ListModel(name);
        redis.addToListLeft(key, model);
    }

    public void addToListRight(String key, String name) {
        ListModel model = new ListModel(name);
        redis.addToListRight(key, model);
    }

    public List<ListModel> getAllData(String key) {
        return redis.getAllList(key, ListModel.class);
    }
}
