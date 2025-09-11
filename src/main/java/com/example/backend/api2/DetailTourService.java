package com.example.backend.api2;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.backend.api2.DetailResponseDTO1.Body.Items.Item;

@Service
public class DetailTourService {

    @Autowired
    private DetailRepa detailRepa;

    public DetailResponseDto getDetailInfo(String uri) {
        try{
            RestTemplate restTemplate = new RestTemplate();
    
            // JSON 응답 받기 위해 DetailResponseDTO로 변환
            DetailResponseDto dto = restTemplate.getForObject(uri, DetailResponseDto.class);
            for (Item item : dto.getResponse().getBody().getItems().getItem()) {
                DetailDto res = new DetailDto();
                BeanUtils.copyProperties(item, res);
                detailRepa.save(res);
            }
            return dto;
        }catch(Exception e){
            return null;
        }
        
    }
}
