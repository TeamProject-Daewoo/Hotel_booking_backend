package com.example.backend.mypage;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileResponseDto {
    private String email;
    private String name;
    private String phoneNumber;
}