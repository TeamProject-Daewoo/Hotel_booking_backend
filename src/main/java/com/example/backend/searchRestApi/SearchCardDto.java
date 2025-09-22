package com.example.backend.searchRestApi;

import lombok.Data;

@Data
public class SearchCardDto {
    private String contentId;
    private String title;
    private String image;
    private int price;
    private String address;
    private double rating;
    private int roomCount;
    private int totalAminities;
    private Long totalReviews;
    private String mapX;
    private String mapY;
    private String status;
}