package com.example.backend.api2;


import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tour")
@RequiredArgsConstructor
public class DetailTourController {

    private final DetailTourService detailTourService;
    private final PriceService priceService;

    @GetMapping("/detail/db/content/{contentid}")
    public ResponseEntity<List<RoomDetailDTO>> getTourDetailByContentid(
            @PathVariable String contentid,
            @RequestParam("checkIn") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam("checkOut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut
    ) {
        // 👇 새로운 서비스 메소드 호출
        List<RoomDetailDTO> list = detailTourService.getDistinctByContentidWithDynamicPricing(contentid, checkIn, checkOut);
        
        if (list.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(list);
    }
    
}