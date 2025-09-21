package com.example.backend.main;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("api")
public class TopRankController {

    @Autowired
    TopRankService topRankService;

    @GetMapping("topRank")
    public ResponseEntity<List<TopRankResponseDto>> topRank() {
        return ResponseEntity.ok(topRankService.getToprank());
    }
    
}
