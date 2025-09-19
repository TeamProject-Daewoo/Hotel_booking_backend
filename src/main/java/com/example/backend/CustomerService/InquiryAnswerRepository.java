package com.example.backend.CustomerService;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InquiryAnswerRepository extends JpaRepository<InquiryAnswer, Long> {
    // Inquiry 엔티티로 조회하는 메서드
    Optional<InquiryAnswer> findByInquiry(Inquiry inquiry);
    
    // 또는 inquiryId(Long)로 조회하는 메서드 (필요시)
    Optional<InquiryAnswer> findByInquiryId(Long inquiryId);
}