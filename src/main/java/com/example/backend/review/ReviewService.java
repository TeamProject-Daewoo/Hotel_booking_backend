package com.example.backend.review;

import com.example.backend.authentication.User;
import com.example.backend.authentication.UserRepository;
import com.example.backend.common.HangulUtils;
import com.example.backend.reservation.Reservation;
import com.example.backend.reservation.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    @Value("${file.upload-dir-default}")
    private String uploadDir;

    @Transactional
    public Review createReview(Long reservationId, String username, int rating, String content, MultipartFile photo) throws IOException {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));
        
        String imageUrl = null;
        if (photo != null && !photo.isEmpty()) {
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();
            }
            String originalFilename = photo.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String savedFilename = UUID.randomUUID().toString() + extension;
            File dest = new File(uploadDirFile, savedFilename);
            photo.transferTo(dest);
            imageUrl = "/uploads/" + savedFilename;
        }

        Review review = Review.builder()
                .reservation(reservation)
                .user(user)
                .hotel(reservation.getHotel())
                .rating(rating)
                .content(content)
                .imageUrl(imageUrl)
                .build();
        System.out.println(review);
        System.out.println(HangulUtils.getChosung(content)+"----------------------------");
        return reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewsByHotel(String hotelId) {
        return reviewRepository.findByHotelContentid(hotelId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewsByUser(String username) {
        return reviewRepository.findByUserUsernameAndIsDeletedFalseOrderByCreatedAtDesc(username).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    private ReviewResponseDto convertToDto(Review review) {
        long visitCount = reviewRepository.countReservationsByUserAndHotelBeforeDate(
            review.getUser().getUsername(),
            review.getHotel().getContentid(),
            review.getReservation().getCheckInDate()
        );

        return ReviewResponseDto.builder()
                .reviewId(review.getReviewId())
                .hotelId(review.getHotel().getContentid())
                .hotelName(review.getHotel().getTitle())
                .userName(review.getUser().getName())
                .reviewText(review.getContent())
                .rating(review.getRating())
                .reviewDate(review.getCreatedAt())
                .imageUrl(review.getImageUrl())
                .visitCount(visitCount) // 계산된 방문 횟수 추가
                .build();
    }

}