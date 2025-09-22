package com.example.backend.api2;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.example.backend.api2.DetailResponseDTO1.Body.Items.Item;
import com.example.backend.room_price_override.RoomPriceOverride;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DetailTourService {

    private final DetailRepa detailRepa;
    
    private final RoomPriceOverrideRepository overrideRepository;
    
    private final PriceService priceService;

    public DetailResponseDto getDetailInfo(String uri) {
        try{
            RestTemplate restTemplate = new RestTemplate();
    
            // JSON 응답 받기 위해 DetailResponseDTO로 변환
            DetailResponseDto dto = restTemplate.getForObject(uri, DetailResponseDto.class);
            for (Item item : dto.getResponse().getBody().getItems().getItem()) {
                Detail res = new Detail();
                BeanUtils.copyProperties(item, res);
                detailRepa.save(res);
            }
            return dto;
        }catch(Exception e){
            return null;
        }
        
    }
    
    public List<Detail> getDistinctByContentid(String contentid) {
        return detailRepa.findDistinctRoomsByContentid(contentid);
    }

    @Transactional(readOnly = true)
    public List<RoomDetailDTO> getDistinctByContentidWithDynamicPricing(String contentid, LocalDate checkInDate, LocalDate checkOutDate) {
        
    	List<Detail> rooms = detailRepa.findDistinctRoomsByContentid(contentid);

        return rooms.stream().map(room -> {
            // 👇 각 객실의 '총합 가격'을 계산합니다.
            int totalPrice = priceService.calculateTotalPrice(
                new PriceCalculationRequestDto(room.getId(), checkInDate, checkOutDate)
            );

            // 👇 DTO의 finalPrice에 '총합 가격'을 담아 반환합니다.
            return RoomDetailDTO.fromEntity(room, totalPrice);

        }).toList();
    }
}