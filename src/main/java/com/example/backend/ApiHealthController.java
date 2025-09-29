package com.example.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiHealthController {

    @GetMapping("/api/health")
    public String healthCheck() {
        return "OK";
    }
}