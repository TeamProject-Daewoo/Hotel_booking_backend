package com.example.backend.Intro;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.backend.Intro.IntroResponseDto.Response.Body.Items.Item;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class IntroService {

    @Autowired
    private IntroRepository introRepository;
    
    private final ObjectMapper mapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    public Intro getIntroDTO(String uri) {
        try {
            String json = restTemplate.getForObject(uri, String.class);
            IntroResponseDto responseDto = mapper.readValue(json, IntroResponseDto.class);
            List<Item> itemList = responseDto.getResponse().getBody().getItems().getItem();
            Intro res = new Intro();
            if(responseDto != null && !itemList.isEmpty()) {
                BeanUtils.copyProperties(itemList.get(0), res);
                introRepository.save(res);
            }
            return res;
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
        
    }

    public Optional<Intro> getFromDb(String contentId) {
        return introRepository.findAll().stream()
            .filter(i -> contentId.equals(i.getContentid()))
            .findFirst();
    }
}