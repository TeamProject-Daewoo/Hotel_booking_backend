package com.example.backend.searchRestApi;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.api.Accommodation;

public interface SearchRepository extends JpaRepository<Accommodation, String>, SearchRepositoryCustom {
    List<SearchResponseDto> findBySearchElements(SearchRequestDto searchRequest);
}
