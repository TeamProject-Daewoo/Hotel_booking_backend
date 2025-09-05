package com.example.backend.api;

import com.example.backend.api2.DetailRequestDTO;
import com.example.backend.api2.DetailTourService;
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
    private AccommodationRepa AccDAO;
    @Autowired
    private DetailTourService detailTourService; 

    public List<AccommodationDto> getAccommodations(String uri) throws Exception {
        String response = restTemplate.getForObject(uri, String.class);
        JsonNode root = mapper.readTree(response);

        // API 구조에 맞게 path 설정
        JsonNode items = root.path("response").path("body").path("items").path("item");
        List<AccommodationDto> result = new ArrayList<>();

        if (items.isArray()) {
            for (JsonNode item : items) {
                // lclsSystm1이 "AC"인 경우만 추가
                if ("AC".equals(item.path("lclsSystm1").asText(""))) {
                    AccommodationDto dto = new AccommodationDto();
                    dto.setTitle(item.path("title").asText(""));
                    dto.setAddr1(item.path("addr1").asText(""));
                    dto.setTel(item.path("tel").asText(""));
                    dto.setFirstimage(item.path("firstimage").asText(""));
                    dto.setFirstimage2(item.path("firstimage2").asText(""));
                    dto.setModifiedtime(item.path("modifiedtime").asText(""));
                    dto.setLclsSystm1(item.path("lclsSystm1").asText(""));
                    dto.setLclsSystm2(item.path("lclsSystm2").asText(""));
                    dto.setLclsSystm3(item.path("lclsSystm3").asText(""));
                    dto.setAreaCode(item.path("areacode").asText(""));
                    dto.setSigunguCode(item.path("sigungucode").asText(""));
                    dto.setCat1(item.path("cat1").asText(""));
                    dto.setCat2(item.path("cat2").asText(""));
                    dto.setCat3(item.path("cat3").asText(""));
                    dto.setlDongRegnCd(item.path("lDongRegnCd").asText(""));
                    dto.setlDongSignguCd(item.path("lDongSignguCd").asText(""));
                    dto.setContentid(item.path("contentid").asText(""));
                    dto.setContenttypeid(item.path("contenttypeid").asText(""));
                    dto.setMapx(item.path("mapx").asText(""));
                    dto.setMapy(item.path("mapy").asText(""));
                    AccDAO.save(dto);
                    DetailRequestDTO requestDTO = new DetailRequestDTO();
                    // 모두 고정값으로 세팅
                    requestDTO.setMobileOS("WEB");
                    requestDTO.setMobileApp("AppTest");
                    requestDTO.set_type("json");
                    requestDTO.setContentId(dto.getContentid());
                    requestDTO.setContentTypeId("32");
                    requestDTO.setNumOfRows(10);
                    requestDTO.setPageNo(1);
                    requestDTO.setServiceKey("d3edf95d6c9d0b621067fbce1f7fd2521372055015a6d19f6dd61b5c9879b661");
                    detailTourService.getDetailInfo(requestDTO);
                    result.add(dto);
                }
            }
        }

        return result;
    }
}

