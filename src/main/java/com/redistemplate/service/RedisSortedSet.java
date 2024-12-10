package com.redistemplate.service;

import com.redistemplate.common.redis.RedisCommon;
import com.redistemplate.domain.sortedSet.model.SortedSet;
import com.redistemplate.domain.sortedSet.model.request.SortedSetRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class RedisSortedSet {

    private final RedisCommon redis;

    public void setSortedSet(SortedSetRequest req) {
        SortedSet model = new SortedSet(req.Name(), req.Score());
        redis.addToSortedSet(req.baseRequest().key(), model, req.Score());
    }

    public Set<SortedSet> getSetDataByRange(String key, Float min, Float max) {
        return redis.rangeByScore(key, min, max, SortedSet.class);
    }

    public List<SortedSet> getTopN(String key, Integer n) {
        return redis.getTopNFromSortedSet(key, n, SortedSet.class);
    }
}
