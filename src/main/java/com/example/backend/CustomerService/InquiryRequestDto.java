package com.example.backend.CustomerService;

import lombok.Getter;

@Getter
public class InquiryRequestDto {
    private String category;
    private String title;
    private String content;
    private String attachmentUrl; // Optional
}

