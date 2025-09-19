package com.example.backend.CustomerService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InquiryAnswerService {

    private final InquiryRepository inquiryRepository;
    private final InquiryAnswerRepository inquiryAnswerRepository;

    public InquiryAnswerService(InquiryRepository inquiryRepository,
                                InquiryAnswerRepository inquiryAnswerRepository) {
        this.inquiryRepository = inquiryRepository;
        this.inquiryAnswerRepository = inquiryAnswerRepository;
    }

    public InquiryAnswer saveAnswer(Long inquiryId, String answerContent) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
            .orElseThrow(() -> new IllegalArgumentException("해당 문의가 없습니다."));

        InquiryAnswer inquiryAnswer = inquiryAnswerRepository.findByInquiry(inquiry)
            .orElse(InquiryAnswer.create(inquiry, answerContent));

        inquiryAnswer.setAnswerContent(answerContent);
        inquiryAnswer.setAnsweredAt(java.time.LocalDateTime.now());

        return inquiryAnswerRepository.save(inquiryAnswer);
    }
     public InquiryAnswerResponseDto getAnswerByInquiryId(Long inquiryId) {
        return inquiryAnswerRepository.findByInquiryId(inquiryId)
                .map(InquiryAnswerResponseDto::fromEntity)
                .orElse(null);
    }
}
