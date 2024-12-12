package com.redistemplate.domain.hashes.controller;

import com.redistemplate.domain.hashes.model.HashModel;
import com.redistemplate.domain.hashes.model.reqeust.HashRequest;
import com.redistemplate.service.RedisHash;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "hash", description = "hash api")
@RestController
@RequestMapping("/api/v1/hash")
@RequiredArgsConstructor
public class HashesController {
    
    private final RedisHash redis;

    @PostMapping("/put-hashes")
    public void putHashes(@RequestBody @Valid HashRequest req) {
        redis.putInHash(req.baseRequest().key(), req.Field(), req.Name());
    }

    @GetMapping("/get-hash-value")
    public HashModel getHashes (@RequestParam @Valid String key, @RequestParam @Valid String field) {
        return redis.getFromHash(key, field);
    }
    
}
