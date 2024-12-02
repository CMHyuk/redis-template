package com.redistemplate.service;

import com.redistemplate.common.redis.RedisCommon;
import com.redistemplate.domain.string.model.StringModel;
import com.redistemplate.domain.string.model.request.MultiStringRequest;
import com.redistemplate.domain.string.model.request.StringRequest;
import com.redistemplate.domain.string.model.response.StringResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RedisString {

    private final RedisCommon redis;

    public void Set(StringRequest req) {
        String key = req.baseRequest().Key();
        StringModel newModel = new StringModel(key, req.Name());

        redis.setData(key, newModel);
    }

    public StringResponse Get(String key) {
        StringModel result = redis.getData(key, StringModel.class);

        List<StringModel> res = new ArrayList<StringModel>();

        if (result != null ) {
            res.add(result);
        }

        return new StringResponse(res);
    }

    public void MultiSet(MultiStringRequest req) {
        Map<String, Object> dataMap = new HashMap<>();

        for (int i=0; i< req.Names().length; i++) {
            String name = req.Names()[i];
            String key = "key:" + (i+1);

            StringModel newModel = new StringModel(key, name);

            dataMap.put(key, newModel);
        }

        redis.multiSetdata(dataMap);
    }
}
