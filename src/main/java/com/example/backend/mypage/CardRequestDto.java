package com.example.backend.mypage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardRequestDto {
    private String customerKey;
    private String authKey;
    private String cardNickname;
    private Boolean isDefault;
}