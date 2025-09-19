package com.example.backend.CustomerService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/inquiry-answer")
public class InquiryAnswerController {

    private final InquiryAnswerService inquiryAnswerService;

    public InquiryAnswerController(InquiryAnswerService inquiryAnswerService) {
        this.inquiryAnswerService = inquiryAnswerService;
    }

    @PostMapping("/{inquiryId}")
    public ResponseEntity<?> answerInquiry(@PathVariable Long inquiryId,
                                           @RequestBody InquiryAnswerRequest request) {
        InquiryAnswer savedAnswer = inquiryAnswerService.saveAnswer(inquiryId, request.getAnswerContent());
        return ResponseEntity.ok(savedAnswer);
    }

    // DTO for request body
    public static class InquiryAnswerRequest {
        private String answerContent;

        public String getAnswerContent() {
            return answerContent;
        }

        public void setAnswerContent(String answerContent) {
            this.answerContent = answerContent;
        }
    }
}
