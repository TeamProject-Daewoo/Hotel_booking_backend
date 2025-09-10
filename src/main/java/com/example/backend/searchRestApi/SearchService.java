package com.example.backend.searchRestApi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService {
    
    @Autowired
    private SearchRepository searchRepository;

    public List<SearchResponseDto> findBySearchElements(SearchRequestDto searchRequest) {
        return searchRepository.findBySearchElements(searchRequest);
    }
}
