package com.example.backend.api;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.common.TourApi;

@RestController
public class AccommodationController {

    @Autowired
    private AccommodationService accommodationService;

    @GetMapping("/accommodations")
    public List<AccommodationDto> getAccommodations() throws Exception {

        TourApi API = new TourApi();
        String uri = API.getAreaBase("1", "10", "");
        return accommodationService.getAccommodations(uri);
    }
}
