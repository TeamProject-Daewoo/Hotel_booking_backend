package com.example.backend.api;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AccommodationController {

    private final AccommodationService accommodationService;

    public AccommodationController(AccommodationService accommodationService) {
        this.accommodationService = accommodationService;
    }

    @GetMapping("/accommodations")
    public List<AccommodationDto> getAccommodations(
            @RequestParam(defaultValue = "d3edf95d6c9d0b621067fbce1f7fd2521372055015a6d19f6dd61b5c9879b661") String serviceKey,
            @RequestParam(defaultValue = "1") String areaCode,
            @RequestParam(defaultValue = "1") String pageNo,
            @RequestParam(defaultValue = "10") String numOfRows,
            @RequestParam(defaultValue = "B02") String cat1,
            @RequestParam(defaultValue = "B0201") String cat2,
            @RequestParam(defaultValue = "B02010700") String cat3
    ) throws Exception {

        // String uri = "http://apis.data.go.kr/B551011/KorService2/areaBasedList2"
        //         + "?ServiceKey=" + serviceKey
        //         + "&areaCode=" + areaCode
        //         + "&pageNo=" + pageNo
        //         + "&numOfRows=" + numOfRows
        //         + "&cat1=" + cat1 
        //         + "&cat2=" + cat2
        //         + "&cat3=" + cat3
        //         + "&_type=json&MobileOS=ETC&MobileApp=AppTest&arrange=C";

        String uri = "http://apis.data.go.kr/B551011/KorService2/areaBasedList2"
    + "?ServiceKey=" + serviceKey
    + "&areaCode=" + areaCode           // 지역코드 (예: 4=대구, 32=강원)
    + "&pageNo=" + pageNo               // 페이지 번호
    + "&numOfRows=100"                  // 최대값 (기본 10 → 최대 100)
    + "&_type=json"                     // JSON 응답
    + "&MobileOS=ETC"                   // 고정 값
    + "&MobileApp=AppTest"              // 임의 앱 이름
    + "&arrange=O"                      // 정렬기준: O=제목순, A=조회순, C=수정일순 등
    + "&contentTypeId=32";              // 숙박업종 (숙박 = 32)


        return accommodationService.getAccommodations(uri);
    }
}

