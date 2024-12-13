package com.redistemplate.domain.strategy.controller;

import com.redistemplate.service.RedisStrategy;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "strategy set ", description = "strategy api")
@RestController
@RequestMapping("/api/v1/strategy")
@RequiredArgsConstructor
public class StrategyController {

    private final RedisStrategy service;

    @GetMapping("/lua-script")
    public void luaScript(@RequestParam @Valid String key1, @RequestParam @Valid String key2, @RequestParam @Valid String newKey) {
        service.luaScript(key1, key2, newKey);
    }

}
