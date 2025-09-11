package com.example.backend.Intro;

import com.example.backend.Intro.IntroResponseDTO.Response.Body.Items.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class IntroService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final IntroRepository introRepository;

    public IntroResponseDTO getIntroDTO(String uri) {
        try {
            IntroResponseDTO responseDto = restTemplate.getForObject(uri, IntroResponseDTO.class);

            if (responseDto != null && responseDto.getResponse().getBody().getItems().getItem() != null && !responseDto.getResponse().getBody().getItems().getItem().isEmpty()) {
                // 첫 번째 아이템만 가져와서 처리
                Item itemDto = responseDto.getResponse().getBody().getItems().getItem().get(0);

                // DTO를 Entity로 변환하여 저장
                IntroEntity introEntity = new IntroEntity(itemDto);
                introRepository.save(introEntity);
            }
            return responseDto;
        } catch (HttpClientErrorException e) {
            log.error("Tour API - 소개정보 호출 중 클라이언트 에러 발생 (URI: {}): {} - {}", uri, e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        } catch(Exception e) {
            log.error("소개 정보 처리 중 알 수 없는 에러 발생 (URI: {})", uri, e);
            return null;
        }
    }
}