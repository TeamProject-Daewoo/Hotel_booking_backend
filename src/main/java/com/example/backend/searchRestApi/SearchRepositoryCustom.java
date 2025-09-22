package com.example.backend.searchRestApi;

import java.util.List;
import java.util.Map;

import com.querydsl.jpa.impl.JPAQuery;

public interface SearchRepositoryCustom {
    SearchResponseDto findBySearchElements(SearchRequestDto searchRequest);
    JPAQuery<?> createBaseQuery(SearchRequestDto searchRequest);
    JPAQuery<?> createCountBaseQuery(SearchRequestDto searchRequest);
    List<SearchCardDto> fetchSearchCards(JPAQuery<?> baseQuery, SearchRequestDto searchRequest);
    Map<String, Integer> fetchCategoryCounts(JPAQuery<?> baseQuery);
    List<String> findByRecommendElements(String keyword);
}