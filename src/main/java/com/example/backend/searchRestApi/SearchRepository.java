package com.example.backend.searchRestApi;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.api.Hotels;

public interface SearchRepository extends JpaRepository<Hotels, Long>, SearchRepositoryCustom {
    SearchResponseDto findBySearchElements(SearchRequestDto searchRequest);
}
