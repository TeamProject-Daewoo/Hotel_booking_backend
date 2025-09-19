package com.example.backend.CustomerService;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.backend.File.InquiryFile;
import com.example.backend.authentication.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "inquiry")
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;

    private String title;

    @Lob
    private String content;

    private String attachmentUrl; // 첨부파일 경로 (nullable 가능)

    @Enumerated(EnumType.STRING)
    private InquiryStatus status;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "user_name", nullable = false)
    private User user;

    @OneToOne(mappedBy = "inquiry", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private InquiryAnswer inquiryAnswer;

    // 생성자 헬퍼
    public static Inquiry create(String category, String title, String content, String attachmentUrl, User user) {
        return Inquiry.builder()
                .category(category)
                .title(title)
                .content(content)
                .attachmentUrl(attachmentUrl)
                .status(InquiryStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();
    }

    public void markAsAnswered() {
        this.status = InquiryStatus.ANSWERED;
    }

    @OneToMany(mappedBy = "inquiry", cascade = CascadeType.ALL, orphanRemoval = true)
private List<InquiryFile> files = new ArrayList<>();
}

