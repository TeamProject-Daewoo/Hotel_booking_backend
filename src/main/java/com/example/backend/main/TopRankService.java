package com.example.backend.main;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TopRankService {

    @Autowired
    private TopRankRepository topRankRepository;

    public List<TopRankResponseDto> getToprank() {
        
        LocalDate startDate = LocalDate.now().minusDays(7);
        Pageable topTen = PageRequest.of(0, 10);
        return topRankRepository.findTopRankings(startDate, topTen);
    }
    
}
