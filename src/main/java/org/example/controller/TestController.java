package org.example.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/test")
    public Map<String, Object> test() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "后端连接成功！");
        result.put("data", "Hello from Spring Boot!");
        return result;
    }

    @PostMapping("/test")
    public Map<String, Object> testPost(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "POST 请求成功！");
        result.put("data", request);
        return result;
    }
}

