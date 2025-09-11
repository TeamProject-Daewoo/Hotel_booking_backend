package com.example.backend.api2;

import com.example.backend.api2.DetailResponseDTO1.Body.Items.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class DetailTourService {

    private final DetailRepa detailRepa;

    public DetailResponseDTO getDetailInfo(String uri) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            DetailResponseDTO dto = restTemplate.getForObject(uri, DetailResponseDTO.class);

            if (dto != null && dto.getResponse() != null && dto.getResponse().getBody().getItems().getItem() != null) {
                for (Item item : dto.getResponse().getBody().getItems().getItem()) {
                    // DTO를 Entity로 변환하여 저장
                    DetailEntity detailEntity = new DetailEntity(item);
                    detailRepa.save(detailEntity);
                }
            }
            return dto;
        } catch (HttpClientErrorException e) {
            log.error("Tour API - 상세정보 호출 중 클라이언트 에러 발생 (URI: {}): {} - {}", uri, e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            log.error("상세 정보 처리 중 알 수 없는 에러 발생 (URI: {})", uri, e);
            return null;
        }
    }
}