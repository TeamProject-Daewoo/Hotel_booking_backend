package com.example.backend.review;

import com.example.backend.api.Hotels;
import com.example.backend.authentication.User;
import com.example.backend.common.HangulUtils;
import com.example.backend.reservation.Reservation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "reviews", indexes = { 
    @Index(name = "idx_content_chosung", columnList = "content_chosung")
 })  
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_name", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", referencedColumnName = "contentid", nullable = false)
    private Hotels hotel;

    @Column(nullable = false)
    private int rating;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String imageUrl;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Column(name = "is_reported", nullable = false)
    private boolean isReported;

    @Column(name = "content_chosung", length = 255)
    private String contentChosung; 

    public static class ReviewBuilder {
        public ReviewBuilder content(String content) {
            this.content = content;
            this.contentChosung = HangulUtils.getChosung(content); 
            return this;
        }
    }
}