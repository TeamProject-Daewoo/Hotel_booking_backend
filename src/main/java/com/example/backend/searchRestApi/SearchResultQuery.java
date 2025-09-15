package com.example.backend.searchRestApi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

public class SearchResultQuery {
private String contentId;
    private String title;
    private String image;
    private Integer price;
    private String address;
    private Long reservationCount; 
}
