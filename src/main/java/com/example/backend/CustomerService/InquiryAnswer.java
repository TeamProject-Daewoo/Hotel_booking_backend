package com.example.backend.CustomerService;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "inquiry_answer")
public class InquiryAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String answerContent;

    private LocalDateTime answeredAt;

    // 문의 ID (Foreign key)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id", unique = true)
    private Inquiry inquiry;

    public static InquiryAnswer create(Inquiry inquiry, String answerContent) {
        return InquiryAnswer.builder()
                .inquiry(inquiry)
                .answerContent(answerContent)
                .answeredAt(LocalDateTime.now())
                .build();
    }
}
