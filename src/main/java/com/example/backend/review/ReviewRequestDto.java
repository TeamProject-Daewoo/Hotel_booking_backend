package com.example.backend.review;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequestDto {
    private Long reservationId;
    private String comment;
    private int rating;
}