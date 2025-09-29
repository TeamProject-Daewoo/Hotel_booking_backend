package com.example.backend.Intro;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IntroService {

    @Autowired
    private IntroRepository introRepository;
    
    public Optional<Intro> getFromDb(String contentId) {
        return introRepository.findAll().stream()
            .filter(i -> contentId.equals(i.getContentid()))
            .findFirst();
    }
}