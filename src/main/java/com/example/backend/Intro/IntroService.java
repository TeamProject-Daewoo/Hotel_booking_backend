package com.example.backend.Intro;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class IntroService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();;

    @Autowired
    private IntroRepository introRepository;

    public IntroDto getIntroDTO(String uri) {
        try {
            String json = restTemplate.getForObject(uri, String.class);
            IntroResponseDto responseDto = mapper.readValue(json, IntroResponseDto.class);
            IntroDto res = new IntroDto();
            if(responseDto != null) {
                BeanUtils.copyProperties(responseDto.getResponse().getBody().getItems().getItem().get(0), res);
                introRepository.save(res);
            }
            return res;
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
        
    }
}