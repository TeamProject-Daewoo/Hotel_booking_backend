package com.example.backend.searchRestApi;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class SearchController {

    @Autowired
    SearchService searchService;

    @GetMapping("/api/search")
    public SearchResponseDTO search(@RequestBody SearchRequestDTO request) {
        return searchService.getListByKeyword(request);
    }
  
}
