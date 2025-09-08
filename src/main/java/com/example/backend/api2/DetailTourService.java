package com.example.backend.api2;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.backend.api2.DetailResponseDTO1.Body.Items.Item;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class DetailTourService {

    @Autowired
    private DetailRepa detailRepa;

    public DetailResponseDTO getDetailInfo(String uri) {
        try{
            RestTemplate restTemplate = new RestTemplate();
    
            // JSON 응답 받기 위해 DetailResponseDTO로 변환
            DetailResponseDTO dto = restTemplate.getForObject(uri, DetailResponseDTO.class);
            for (Item item : dto.getResponse().getBody().getItems().getItem()) {
                DetailDTO res = new DetailDTO();
                BeanUtils.copyProperties(item, res);
                detailRepa.save(res);
            }
            return dto;
        }catch(Exception e){
            return null;
        }
        
    }
}
