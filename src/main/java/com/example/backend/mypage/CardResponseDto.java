package com.example.backend.mypage;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CardResponseDto {
    private Long cardId;
    private String cardCompany;
    private String cardNumberMasked;
    private String cardNickname;
    private boolean isDefault;
}