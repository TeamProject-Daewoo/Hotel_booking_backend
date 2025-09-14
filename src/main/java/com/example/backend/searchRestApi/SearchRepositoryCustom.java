package com.example.backend.searchRestApi;

public interface SearchRepositoryCustom {
    SearchResponseDto findBySearchElements(SearchRequestDto searchRequestDTO);
}