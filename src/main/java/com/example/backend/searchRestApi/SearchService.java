package com.example.backend.searchRestApi;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.reservation.AvailabilityRequestDto;
import com.example.backend.reservation.ReservationService;
import com.querydsl.jpa.impl.JPAQuery;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchService {
    
    @Autowired
    private final SearchRepository searchRepository;
    private final ReservationService reservationService;

    public SearchResponseDto findBySearchElements(SearchRequestDto searchRequest) {
        
        SearchResponseDto searchResponseDto = new SearchResponseDto();

        JPAQuery<?> listBaseQuery = searchRepository.createBaseQuery(searchRequest);
        List<SearchCardDto> cards = searchRepository.fetchSearchCards(listBaseQuery, searchRequest);

        JPAQuery<?> countBaseQuery = searchRepository.createCountBaseQuery(searchRequest);
        Map<String, Integer> counts = searchRepository.fetchCategoryCounts(countBaseQuery);

        cards.forEach(hotel -> {
                // 각 호텔의 contentId를 사용하여 가용성 정보를 조회하기 위한 DTO 생성
                AvailabilityRequestDto availabilityDto = new AvailabilityRequestDto();
                availabilityDto.setContentId(hotel.getContentId());
                availabilityDto.setStartDate(searchRequest.getCheckInDate());
                availabilityDto.setEndDate(searchRequest.getCheckOutDate());

                Map<LocalDate, Map<Long, Integer>> availabilityInfo = reservationService.getRoomAvailability(availabilityDto);
                
                //예약 가능, 예약 마감 등 상태 전달
                String status = determineStatus(availabilityInfo);
                hotel.setStatus(status);
            });

        searchResponseDto.setSearchCards(cards);
        searchResponseDto.setCounts(counts);
        // System.out.println(searchResponseDto);
        return searchResponseDto;
    }

    private String determineStatus(Map<LocalDate, Map<Long, Integer>> availabilityInfo) {
        if (availabilityInfo.isEmpty()) {
            return "정보 없음";
        }

        boolean hasAnyAvailability = false;
        int limitedCount = 0;
        int totalRoomDayCombinations = 0;

        for (Map<Long, Integer> dailyAvailability : availabilityInfo.values()) {
            totalRoomDayCombinations += dailyAvailability.size();
            for (Integer availableCount : dailyAvailability.values()) {
                if (availableCount > 0) {
                    hasAnyAvailability = true;
                }
                if (availableCount > 0 && availableCount <= 3) { // 3개 이하가 마감 임박 기준
                    limitedCount++;
                }
            }
        }
        
        // 전체 기간 중 마감 임박 상태가 50% 이상일 경우 마감 임박으로 판단
        if (totalRoomDayCombinations > 0 && (double) limitedCount / totalRoomDayCombinations > 0.5) {
            return "마감 임박";
        } else if (hasAnyAvailability) {
            return "예약 가능";
        } else {
            return "예약 마감";
        }
    }

    //이 호텔에 예약 가능한 객실이 있는지 확인
    private boolean hasAvailableRoom(Map<LocalDate, Map<Long, Integer>> availabilityInfo) {
        for (Map<Long, Integer> dailyAvailability : availabilityInfo.values()) {
            boolean availableThisDay = false;
            for (Integer availableCount : dailyAvailability.values()) {
                if (availableCount > 0) {
                    availableThisDay = true;
                    break; // 이 날은 예약 가능한 방이 있으니 다음 날짜로 넘어감
                }
            }
            if (!availableThisDay) {
                return false; // 이 날짜에 가능한 방이 없으므로 전체 기간 예약 불가
            }
        }
        return false;
    }

    public List<String> findByRecommendElements(String keyword) {
        return searchRepository.findByRecommendElements(keyword);
    }
}
