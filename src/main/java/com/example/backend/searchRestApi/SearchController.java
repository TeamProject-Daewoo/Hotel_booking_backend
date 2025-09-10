package com.example.backend.searchRestApi;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("api")
public class SearchController {

    @Autowired
    SearchService searchService;

    @PostMapping("search")
    public ResponseEntity<List<SearchResponseDto>> search(@RequestBody SearchRequestDto request) {
        return ResponseEntity.ok(searchService.findBySearchElements(request));
    }
  
}
