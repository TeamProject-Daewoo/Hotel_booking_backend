package com.example.backend.searchRestApi;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("api/search")
public class SearchController {

    @Autowired
    SearchService searchService;

    @GetMapping("/")
    public ResponseEntity<List<SearchResponseDto>> search(@RequestBody SearchRequestDto request) {
        return ResponseEntity.ok(searchService.findBySearchElements(request));
    }
  
}
