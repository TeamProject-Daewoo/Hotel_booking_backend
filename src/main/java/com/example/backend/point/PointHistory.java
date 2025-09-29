package com.example.backend.point;

import com.example.backend.authentication.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private int points;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointTransactionType type; // EARNED, USED

    @Column(nullable = false)
    private String description;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime transactionDate;

    @Builder
    public PointHistory(User user, int points, PointTransactionType type, String description) {
        this.user = user;
        this.points = points;
        this.type = type;
        this.description = description;
    }
}