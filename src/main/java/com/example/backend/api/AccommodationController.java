
package com.example.backend.api;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.example.backend.common.TourApi;

@RestController
public class AccommodationController {

    @Autowired
    private AccommodationService accommodationService;

    @GetMapping("/accommodations")
    public List<Accommodation> getAccommodations() throws Exception {

        TourApi API = new TourApi();
        String uri = API.getAreaBase("1", "100", "");
        return accommodationService.getAccommodations(uri);
    }

    
    @GetMapping("/accommodations/{contentid}")
    public ResponseEntity<AccommodationDto> getAccommodation(@PathVariable String contentid) {
        return accommodationService.getAccommodation(contentid)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
