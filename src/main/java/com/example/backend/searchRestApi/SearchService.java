package com.example.backend.searchRestApi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService {
    
    @Autowired
    private SearchRepository searchRepository;

    public List<SearchResponseDto> findBySearchElements(SearchRequestDto searchRequest) {
        List<SearchResponseDto> res = searchRepository.findBySearchElements(searchRequest);
        System.out.println(res);
        return res;
    }
}
