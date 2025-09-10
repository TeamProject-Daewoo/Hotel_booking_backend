package com.example.backend.mypage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateRequestDto {
    private String name;
    private String phoneNumber;
    private String currentPassword;
    private String newPassword;
}