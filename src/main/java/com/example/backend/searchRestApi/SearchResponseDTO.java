package com.example.backend.searchRestApi;

import lombok.Data;

@Data
public class SearchResponseDTO {
    private String contentid;
    private String title;
    private String image;
    private int price;
    private String address;
    private double rating;
    private int totalAminities;
    private int totalReviews;
}
