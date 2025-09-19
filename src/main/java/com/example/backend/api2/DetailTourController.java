package com.example.backend.api2;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/tour")
public class DetailTourController {

    @Autowired
    private DetailTourService detailTourService;

    // ✅ DB에서 contentid로만 조회
    @GetMapping("/detail/db/content/{contentid}")
    public ResponseEntity<List<Detail>> getTourDetailByContentid(@PathVariable String contentid) {
        List<Detail> list = detailTourService.getDistinctByContentid(contentid);
        if (list.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(list);
    }
}