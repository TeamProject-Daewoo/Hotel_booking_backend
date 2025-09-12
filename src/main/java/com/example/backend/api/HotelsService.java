
package com.example.backend.api;

import com.example.backend.Intro.IntroService;
import com.example.backend.api2.DetailTourService;
import com.example.backend.common.TourApi;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HotelsService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private HotelsRepa hotels;
    @Autowired
    private DetailTourService detailTourService;
    @Autowired
    private IntroService introService; 

    public List<Hotels> getAccommodations(String uri) throws Exception {
        String response = restTemplate.getForObject(uri, String.class);
        JsonNode root = mapper.readTree(response);

        // API 구조에 맞게 path 설정
        JsonNode items = root.path("response").path("body").path("items").path("item");
        List<Hotels> result = new ArrayList<>();

        if (items.isArray()) {
            for (JsonNode item : items) {
                // lclsSystm1이 "AC"인 경우만 추가
                if ("AC".equals(item.path("lclsSystm1").asText(""))) {
                    Hotels dto = new Hotels();
                    dto.setTitle(item.path("title").asText(""));
                    dto.setAddr1(item.path("addr1").asText(""));
                    dto.setTel(item.path("tel").asText(""));
                    dto.setFirstimage(item.path("firstimage").asText(""));
                    dto.setAreaCode(item.path("areacode").asText(""));
                    dto.setContentid(item.path("contentid").asText(""));
                    dto.setMapx(item.path("mapx").asText(""));
                    dto.setMapy(item.path("mapy").asText(""));
                    
                    TourApi api = new TourApi();
                    String detailUri = api.getDetailUri( "1", "10", dto.getContentid());
                    String introUri = api.getIntroUri("1", "10", dto.getContentid());
                    //상세 정보와 소개가 존재할 때만 추가
                    if(detailTourService.getDetailInfo(detailUri) != null && introService.getIntroDTO(introUri) != null) {
                        hotels.save(dto);
                        result.add(dto);
                    }
                }
            }
        }

        return result;
    }

    public Optional<Hotels> getAccommodation(String contentid) {
        return hotels.findByContentid(contentid);
    }
}
