package com.example.backend.mypage;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponseDto {
    private String email;
    private String nickname;
    private String phone;
}