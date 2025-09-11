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

@Service
public class AccommodationService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private AccommodationRepa accommodationRepository; // Repa -> Repository로 변경
    @Autowired
    private DetailTourService detailTourService;
    @Autowired
    private IntroService introService; 

    public List<AccommodationDto> getAccommodations(String uri) throws Exception {
        String response = restTemplate.getForObject(uri, String.class);
        JsonNode root = mapper.readTree(response);
        JsonNode items = root.path("response").path("body").path("items").path("item");
        List<AccommodationDto> result = new ArrayList<>();

        if (items.isArray()) {
            for (JsonNode item : items) {
                if ("AC".equals(item.path("lclsSystm1").asText(""))) {
                    AccommodationDto dto = mapper.treeToValue(item, AccommodationDto.class);
                    
                    TourApi api = new TourApi();
                    String detailUri = api.getDetailUri( "1", "10", dto.getContentid());
                    String introUri = api.getIntroUri("1", "10", dto.getContentid());

                    if(detailTourService.getDetailInfo(detailUri) != null && introService.getIntroDTO(introUri) != null) {
                        // DTO를 Entity로 변환
                        Accommodation accommodationEntity = new Accommodation(dto);
                        // Entity를 Repository를 통해 저장
                        accommodationRepository.save(accommodationEntity);

                        result.add(dto);
                    }
                }
            }
        }

        return result;
    }
}