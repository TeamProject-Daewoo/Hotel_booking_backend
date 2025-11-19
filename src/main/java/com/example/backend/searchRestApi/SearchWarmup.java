package com.example.backend.searchRestApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/* 초기 검색 속도 개선을 위한 더미 요청 */
@Component
public class SearchWarmup implements CommandLineRunner {

    @Autowired
    private SearchService searchService;

    @Override
    public void run(String... args) throws Exception {
        // 1. DTO 객체 생성 및 데이터 형식 맞추기
        SearchRequestDto dummyDto = new SearchRequestDto();
        
        dummyDto.setCheckInDate(LocalDate.now());
        dummyDto.setCheckOutDate(LocalDate.now().plusDays(2));
        dummyDto.setGuestCount(2);
        dummyDto.setMinPrice(0);
        dummyDto.setMaxPrice(500000);
        dummyDto.setRating(1.0);
        dummyDto.setKeyword("ㄱ");
        dummyDto.setOrder("rating_desc");
        dummyDto.setCategory("HOTEL");
        dummyDto.setAmenities(new HashMap<>());
        dummyDto.setFreebies(new HashMap<>()); 
        dummyDto.setPage(1);
        dummyDto.setSize(1);
        // 2. 서비스 로직 호출 (웜업 실행)
        try {
            searchService.findBySearchElements(dummyDto);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}