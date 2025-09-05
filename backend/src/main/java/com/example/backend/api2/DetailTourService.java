package com.example.backend.api2;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.backend.api2.DetailResponseDTO1.Body.Items;
import com.example.backend.api2.DetailResponseDTO1.Body.Items.Item;

import ch.qos.logback.core.joran.util.beans.BeanUtil;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class DetailTourService {

    private static final String API_URL = "https://apis.data.go.kr/B551011/KorService2/detailInfo2";
    @Autowired
    private DetailRepa detailRepa;

    public DetailResponseDTO getDetailInfo(DetailRequestDTO requestDTO) {
        try{
            System.out.println(requestDTO);
            RestTemplate restTemplate = new RestTemplate();

            String encodedServiceKey;
            try {
                encodedServiceKey = URLEncoder.encode(requestDTO.getServiceKey(), StandardCharsets.UTF_8.toString());
            } catch (Exception e) {
                throw new RuntimeException("ServiceKey encoding failed", e);
            }
    
            String url = API_URL + "?" +
                    "serviceKey=" + encodedServiceKey +
                    "&MobileOS=" + requestDTO.getMobileOS() +
                    "&MobileApp=" + requestDTO.getMobileApp() +
                    "&_type=" + requestDTO.get_type() +
                    "&contentId=" + requestDTO.getContentId() +
                    "&contentTypeId=" + requestDTO.getContentTypeId() +
                    "&numOfRows=" + requestDTO.getNumOfRows() +
                    "&pageNo=" + requestDTO.getPageNo();
            
            // JSON 응답 받기 위해 DetailResponseDTO로 변환\
            DetailResponseDTO dto = restTemplate.getForObject(url, DetailResponseDTO.class);
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
