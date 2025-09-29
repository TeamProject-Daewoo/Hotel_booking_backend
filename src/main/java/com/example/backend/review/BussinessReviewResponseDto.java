package com.example.backend.review;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BussinessReviewResponseDto {
    private Long reviewId;
    private String hotelId;
    private String hotelName;
    private String email;
    private String nickName;
    private String reviewText;
    private int rating;
    private LocalDateTime reviewDate;
    private boolean isReported;
    public BussinessReviewResponseDto(Review review) {
        // 엔티티 필드명을 Review 엔티티 구조에 맞게 매핑
        this.reviewId = review.getReviewId();
        this.hotelId = review.getHotel().getContentid(); 
        this.hotelName = review.getHotel().getTitle();
        this.email = review.getUser().getUsername();
        this.nickName = review.getUser().getName();
        this.reviewText = review.getContent();
        this.rating = review.getRating();
        this.reviewDate = review.getCreatedAt();
        this.isReported = review.isReported();
    }
}
