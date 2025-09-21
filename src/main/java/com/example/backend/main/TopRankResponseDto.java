package com.example.backend.main;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopRankResponseDto {
    private String contentId;
    private String title;
    private String addr;
    private String image;
    private double rating;
    private long reviewCount;
    private int price;
}
