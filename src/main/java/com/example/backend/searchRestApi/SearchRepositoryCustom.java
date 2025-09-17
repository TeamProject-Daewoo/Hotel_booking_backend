package com.example.backend.searchRestApi;

import java.util.List;

public interface SearchRepositoryCustom {
    SearchResponseDto findBySearchElements(SearchRequestDto searchRequest);
    List<String> findByRecommendElements(String keyword);
}