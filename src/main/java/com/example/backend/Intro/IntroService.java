package com.example.backend.Intro;

import com.example.backend.Intro.IntroResponseDTO.Response.Body.Items.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class IntroService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final IntroRepository introRepository;

    public IntroDTO getIntro(String uri) {
        try {
            IntroResponseDTO responseDto = restTemplate.getForObject(uri, IntroResponseDTO.class);

            if (responseDto != null &&
                responseDto.getResponse() != null &&
                responseDto.getResponse().getBody() != null &&
                responseDto.getResponse().getBody().getItems() != null &&
                responseDto.getResponse().getBody().getItems().getItem() != null &&
                !responseDto.getResponse().getBody().getItems().getItem().isEmpty()) {

                Item itemData = responseDto.getResponse().getBody().getItems().getItem().get(0);

                IntroEntity introEntity = new IntroEntity(itemData);
                introRepository.save(introEntity);

                IntroDTO resultDto = new IntroDTO();
                BeanUtils.copyProperties(itemData, resultDto);
                return resultDto;
            }
            return null;
        } catch (HttpClientErrorException e) {
            log.error("Tour API - 소개정보 호출 중 에러 (URI: {}): {} - {}", uri, e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        } catch(Exception e) {
            log.error("소개 정보 처리 중 알 수 없는 에러 발생 (URI: {})", uri, e);
            return null;
        }
    }
}