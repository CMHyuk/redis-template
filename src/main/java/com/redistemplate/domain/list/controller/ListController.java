package com.redistemplate.domain.list.controller;

import com.redistemplate.domain.list.model.ListModel;
import com.redistemplate.domain.list.model.request.ListRequest;
import com.redistemplate.service.RedisList;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "list", description = "list api")
@RestController
@RequestMapping("/api/v1/list")
@RequiredArgsConstructor
public class ListController {

    private final RedisList redis;

    @PostMapping("/list-add-left")
    public void setNewValueToList(@RequestBody @Valid ListRequest req) {
        redis.addToListLeft(req.baseRequest().key(), req.Name());
    }

    @PostMapping("/list-add-right")
    public void setNewValueToRight(@RequestBody @Valid ListRequest req) {
        redis.addToListRight(req.baseRequest().key(), req.Name());
    }

    @GetMapping("/all")
    public List<ListModel> getAll(@RequestParam String key) {
        return redis.getAllData(key);
    }
}
