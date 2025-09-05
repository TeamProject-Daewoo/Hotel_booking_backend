package com.example.backend.api2;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class DetailTourService {

    private static final String API_URL = "https://apis.data.go.kr/B551011/KorService2/detailInfo2";

    public DetailResponseDTO getDetailInfo(DetailRequestDTO requestDTO) {
        try{
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
           
            return restTemplate.getForObject(url, DetailResponseDTO.class);
        }catch(Exception e){
            return null;
        }
        
    }
}
