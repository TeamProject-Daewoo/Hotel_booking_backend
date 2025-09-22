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
            // 1. 적용 가능한 특별가를 조회합니다.
            Optional<RoomPriceOverride> override = overrideRepository
                .findApplicableOverride(room.getId(), checkInDate, checkOutDate);

            // 2. DTO를 생성하고, 특별가가 있을 경우에만 finalPrice를 설정합니다.
            RoomDetailDTO dto = RoomDetailDTO.fromEntity(room, null); // finalPrice를 일단 null로 생성
            override.ifPresent(o -> dto.setFinalPrice(o.getPrice())); // 특별가가 존재하면(ifPresent) dto의 finalPrice 설정
            
            System.out.println("############" + dto);
            
            return dto;

        }).toList();
    }
}