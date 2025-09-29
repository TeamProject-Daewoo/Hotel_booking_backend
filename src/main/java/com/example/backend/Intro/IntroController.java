package com.example.backend.Intro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tour")
public class IntroController {

    @Autowired
    private IntroService introService;

    @GetMapping("/intro/db/{contentId}")
    public ResponseEntity<Intro> getIntroFromDb(@PathVariable String contentId) {
        return introService.getFromDb(contentId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}