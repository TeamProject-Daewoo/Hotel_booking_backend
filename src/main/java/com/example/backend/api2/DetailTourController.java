package com.example.backend.api2;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.example.backend.common.TourApi;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/tour")
public class DetailTourController {

    @Autowired
    private DetailTourService detailTourService;

    @GetMapping("/detail/{id}")
    public DetailResponseDTO getTourDetail(@PathVariable Long id) throws Exception {
        TourApi api = new TourApi();
        
        return detailTourService.getDetailInfo(api.getDetailUri("1", "10", String.valueOf(id)));
    }
}