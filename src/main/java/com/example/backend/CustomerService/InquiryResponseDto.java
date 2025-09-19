package com.example.backend.CustomerService;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class InquiryResponseDto {

    private Long id;
    private String category;
    private String title;
    private String content;
    private String attachmentUrl;
    private LocalDateTime createdAt;
    private String status;

    // 답변용 DTO
    private AnswerDto answer;

    @Getter
    public static class AnswerDto {
        private String answerContent;
        private LocalDateTime answeredAt;

        public AnswerDto(InquiryAnswer inquiryAnswer) {
            this.answerContent = inquiryAnswer.getAnswerContent();
            this.answeredAt = inquiryAnswer.getAnsweredAt();
        }
    }

    public static InquiryResponseDto fromEntity(Inquiry inquiry) {
        InquiryResponseDto dto = new InquiryResponseDto();
        dto.id = inquiry.getId();
        dto.category = inquiry.getCategory();
        dto.title = inquiry.getTitle();
        dto.content = inquiry.getContent();
        dto.attachmentUrl = inquiry.getAttachmentUrl();
        dto.createdAt = inquiry.getCreatedAt();
        dto.status = inquiry.getStatus().name();

        if (inquiry.getInquiryAnswer() != null) {
            dto.answer = new AnswerDto(inquiry.getInquiryAnswer());
        }

        return dto;
    }
}
