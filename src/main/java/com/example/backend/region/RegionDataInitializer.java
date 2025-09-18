package com.example.backend.region;

import com.example.backend.common.TourApi;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegionDataInitializer implements CommandLineRunner {

    // --- 의존성 주입 ---
    private final RegionRepository regionRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // DB에 데이터가 이미 있으면 실행하지 않고 종료
        if (regionRepository.count() > 0) {
            log.info("Regions data already exists. Skipping initialization.");
            return;
        }

        log.info("Start initializing regions data from Tour API...");

        // --- 1단계: 최상위 지역(시/도) 목록 가져오기 ---
        List<AreaItem> topLevelAreas = fetchRegionsFromApi(null);
        List<Region> topLevelRegionsToSave = topLevelAreas.stream()
            .map(item -> new Region(item.getName(), item.getCode(), null))
            .collect(Collectors.toList());
        
        // 가져온 최상위 지역 먼저 저장 (이후 반복문에서 사용하기 위함)
        List<Region> savedTopLevelRegions = regionRepository.saveAll(topLevelRegionsToSave);
        log.info("Saved {} top-level regions.", savedTopLevelRegions.size());

        // --- 2단계: 각 최상위 지역에 속한 하위 지역(시/군/구) 목록 가져오기 ---
        List<Region> subRegionsToSave = new ArrayList<>();
        for (Region topRegion : savedTopLevelRegions) {
            log.info("Fetching sub-regions for {} (code: {})...", topRegion.getName(), topRegion.getCode());
            List<AreaItem> subLevelAreas = fetchRegionsFromApi(topRegion.getCode());
            
            for (AreaItem subItem : subLevelAreas) {
                // Region Entity 생성 시 nameChosung은 자동으로 생성됨
                subRegionsToSave.add(new Region(subItem.getName(), subItem.getCode(), topRegion.getCode()));
            }
        }

        // 모아둔 모든 하위 지역을 DB에 한 번에 저장
        regionRepository.saveAll(subRegionsToSave);
        log.info("Saved {} sub-level regions.", subRegionsToSave.size());
        log.info("Regions data initialization complete.");
    }

    private List<AreaItem> fetchRegionsFromApi(Integer areaCode) throws Exception {
        TourApi api = new TourApi();
        String uri = api.getRegionUri("1", "100");
        // areaCode가 null이 아닐 경우에만 파라미터로 추가 (하위 지역 조회 시)
        if (areaCode != null) {
            uri += ("&areaCode="+areaCode);
        }

        // System.out.println(uri+"===========================");
        try {
            String responseBody = restTemplate.getForObject(uri, String.class);
            ApiResponseDto apiResponse = objectMapper.readValue(responseBody, ApiResponseDto.class);

            if (apiResponse != null && apiResponse.getResponse() != null &&
                apiResponse.getResponse().getBody() != null &&
                apiResponse.getResponse().getBody().getItems() != null &&
                apiResponse.getResponse().getBody().getItems().getItem() != null) {
                return apiResponse.getResponse().getBody().getItems().getItem();
            }
        } catch (Exception e) {
            log.error("Failed to fetch regions data from API for areaCode [{}]: {}", areaCode, e.getMessage());
        }
        return Collections.emptyList(); // 오류 발생 또는 데이터가 없을 경우 빈 리스트 반환
    }
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class ApiResponseDto {
    private ResponseDto response;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class ResponseDto {
    private BodyDto body;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class BodyDto {
    private ItemsDto items;
    private int totalCount;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class ItemsDto {
    private List<AreaItem> item;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class AreaItem {
    private int code;
    private String name;
    @JsonProperty("rnum")
    private int rnum;
}