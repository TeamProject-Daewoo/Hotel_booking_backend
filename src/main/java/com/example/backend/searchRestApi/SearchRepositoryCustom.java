package com.example.backend.searchRestApi;

import java.util.List;

public interface SearchRepositoryCustom {
    List<SearchResponseDto> findBySearchElements(SearchRequestDto searchRequestDTO);
}