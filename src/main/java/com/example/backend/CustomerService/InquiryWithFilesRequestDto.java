package com.example.backend.CustomerService;

import java.util.List;

import lombok.Getter;

@Getter
public class InquiryWithFilesRequestDto {
    private InquiryRequestDto inquiry;
    private List<String> fileUrls;
}
