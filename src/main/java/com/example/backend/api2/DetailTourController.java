package com.example.backend.api2;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/tour")
public class DetailTourController {

    @Autowired
    private DetailTourService detailTourService;

    @GetMapping("/detail/{id}")
    public DetailResponseDTO getTourDetail(@PathVariable Long id) {
        DetailRequestDTO requestDTO = new DetailRequestDTO();

        // 모두 고정값으로 세팅
        requestDTO.setMobileOS("WEB");
        requestDTO.setMobileApp("AppTest");
        requestDTO.set_type("json");
        requestDTO.setContentId(id.toString());
        requestDTO.setContentTypeId("32");
        requestDTO.setNumOfRows(10);
        requestDTO.setPageNo(1);
        requestDTO.setServiceKey("d3edf95d6c9d0b621067fbce1f7fd2521372055015a6d19f6dd61b5c9879b661");

        return detailTourService.getDetailInfo(requestDTO);
    }
}

