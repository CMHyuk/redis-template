package com.redistemplate.domain.rateLimiter;

import com.redistemplate.service.RedisRateLimiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RateLimiterController {

    private final RedisRateLimiterService rateLimiterService;

    @GetMapping("/api/resource/{userId}")
    public ResponseEntity<String> getResource(@PathVariable String userId) {
        if (!rateLimiterService.isRequestAllowed(userId)) {
            return ResponseEntity.status(429).body("Too Many Requests");
        }
        return ResponseEntity.ok("Request Accepted");
    }

}
