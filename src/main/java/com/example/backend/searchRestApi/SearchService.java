package com.example.backend.searchRestApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    @Autowired
    SearchViewRepository searchViewRepository;

    public SearchResponseDTO getListByKeyword(SearchRequestDTO request) {
        System.out.println(request);
        return searchViewRepository.getSearchView(request);
    }
  
}
