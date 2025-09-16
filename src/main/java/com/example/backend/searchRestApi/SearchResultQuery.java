package com.example.backend.searchRestApi;

import lombok.Data;

@Data
public class SearchResultQuery {
    private String contentId;
    private String title;
    private String image;
    private Integer price;
    private String address;
    private Long reservationCount;
    private String mapX;
    private String mapY;
}
