package com.example.backend.CustomerService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inquiries/answers")
@RequiredArgsConstructor
public class InquiryAnswerQueryController {

    private final InquiryAnswerService inquiryAnswerService;

    @GetMapping("/{inquiryId}")
    public ResponseEntity<InquiryAnswerResponseDto> getAnswerByInquiryId(@PathVariable Long inquiryId) {
        InquiryAnswerResponseDto answerDto = inquiryAnswerService.getAnswerByInquiryId(inquiryId);
        if (answerDto == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(answerDto);
    }
}
