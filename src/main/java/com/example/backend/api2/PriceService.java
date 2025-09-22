package com.example.backend.api2;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.room_price_override.RoomPriceOverride;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PriceService {

    private final DetailRepa detailRepa; // 객실(Detail) Repository
    private final RoomPriceOverrideRepository overrideRepository;

    @Transactional(readOnly = true)
    public int calculateTotalPrice(PriceCalculationRequestDto request) {
        Detail room = detailRepa.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("객실을 찾을 수 없습니다."));

        // 1. 요청된 기간과 겹치는 모든 특별가 설정을 미리 가져옵니다.
        List<RoomPriceOverride> overrides = overrideRepository.findAllOverlaps(
            request.getRoomId(), request.getCheckInDate(), request.getCheckOutDate()
        );

        int totalPrice = 0;
        // 2. 체크인부터 체크아웃 전날까지 하루씩 순회합니다.
        for (LocalDate date = request.getCheckInDate(); date.isBefore(request.getCheckOutDate()); date = date.plusDays(1)) {
            
            // 👇 현재 루프의 date를 final 변수에 복사합니다.
            final LocalDate currentDate = date;

            // 람다식에서는 이제 final인 currentDate를 사용합니다.
            Optional<RoomPriceOverride> applicableOverride = overrides.stream()
                .filter(o -> !currentDate.isBefore(o.getStartDate()) && !currentDate.isAfter(o.getEndDate()))
                .findFirst();

            if (applicableOverride.isPresent()) {
                totalPrice += applicableOverride.get().getPrice();
            } else {
                totalPrice += room.getRoomoffseasonminfee1();
            }
        }
        return totalPrice;
    }
}
