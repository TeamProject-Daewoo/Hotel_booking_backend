package com.example.backend.searchRestApi;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.api.Hotels;

public interface SearchRepository extends JpaRepository<Hotels, String>, SearchRepositoryCustom {
    SearchResponseDto findBySearchElements(SearchRequestDto searchRequest);
    List<String> findByRecommendElements(String keyword);
}
