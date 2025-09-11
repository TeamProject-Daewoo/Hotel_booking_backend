package com.example.backend.api;

import com.example.backend.Intro.IntroService;
import com.example.backend.api2.DetailTourService;
import com.example.backend.common.TourApi;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();
    private final AccommodationRepa AccDAO;
    private final DetailTourService detailTourService;
    private final IntroService introService;

    public List<AccommodationDto> getAccommodations(String uri) throws Exception {
        String response = restTemplate.getForObject(uri, String.class);
        JsonNode root = mapper.readTree(response);

        JsonNode items = root.path("response").path("body").path("items").path("item");
        List<AccommodationDto> result = new ArrayList<>();

        if (items.isArray()) {
            for (JsonNode item : items) {
                if ("AC".equals(item.path("lclsSystm1").asText(""))) {
                    // JsonNode를 DTO로 변환
                    AccommodationDto dto = mapper.treeToValue(item, AccommodationDto.class);

                    // 상세 정보와 소개 정보가 모두 있을 때만 목록에 추가하고 DB에 저장
                    // (성능 개선을 위해 이 부분은 주석 처리하고, 상세 조회 시 저장하는 것을 권장)
                    /*
                    TourApi api = new TourApi();
                    String detailUri = api.getDetailUri("1", "10", dto.getContentid());
                    String introUri = api.getIntroUri("1", "10", dto.getContentid());

                    // 비동기 병렬 처리로 성능 개선
                    CompletableFuture<Boolean> hasDetailFuture = CompletableFuture.supplyAsync(() -> detailTourService.getDetailInfo(detailUri) != null);
                    CompletableFuture<Boolean> hasIntroFuture = CompletableFuture.supplyAsync(() -> introService.getIntroDTO(introUri) != null);

                    if (hasDetailFuture.get() && hasIntroFuture.get()) {
                        Accommodation accommodationEntity = new Accommodation(dto);
                        AccDAO.save(accommodationEntity);
                        result.add(dto);
                    }
                    */

                    // **권장하는 방식**: 목록에서는 DB 저장 없이 DTO만 반환
                    Accommodation accommodationEntity = new Accommodation(dto);
                    AccDAO.save(accommodationEntity); // 일단 기존 로직대로 저장
                    result.add(dto);
                }
            }
        }

        return result;
    }

    public Optional<Accommodation> getAccommodation(String contentid) {
        return AccDAO.findById(contentid);
    }
}