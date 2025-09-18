package com.example.backend.review;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewResponseDto {
    private Long reviewId;
    private String hotelId;
    private String hotelName;
    private String userName;
    private String reviewText;
    private int rating;
    private LocalDateTime reviewDate;
    private String imageUrl;
    private long visitCount;
}