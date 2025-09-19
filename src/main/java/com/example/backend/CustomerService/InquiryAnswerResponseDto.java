package com.example.backend.CustomerService;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class InquiryAnswerResponseDto {

    private Long id;
    private String answerContent;
    private LocalDateTime answeredAt;

    public static InquiryAnswerResponseDto fromEntity(InquiryAnswer answer) {
        InquiryAnswerResponseDto dto = new InquiryAnswerResponseDto();
        dto.id = answer.getId();
        dto.answerContent = answer.getAnswerContent();
        dto.answeredAt = answer.getAnsweredAt();
        return dto;
    }
}
