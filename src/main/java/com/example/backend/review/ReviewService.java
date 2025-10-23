package com.example.backend.review;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.authentication.User;
import com.example.backend.authentication.UserRepository;
import com.example.backend.point.PointHistory;
import com.example.backend.point.PointHistoryRepository;
import com.example.backend.point.PointTransactionType;
import com.example.backend.reservation.Reservation;
import com.example.backend.reservation.ReservationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final PointHistoryRepository pointHistoryRepository;

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
        	if (!isImageFile(photo)) {
                throw new IllegalArgumentException("이미지 파일(jpg, png, gif)만 업로드할 수 있습니다.");
            }
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
        reviewRepository.save(review);

        // 리뷰 작성 시 500 포인트 지급
        user.addPoints(5000);
        userRepository.save(user);

        PointHistory pointHistory = PointHistory.builder()
                .user(user)
                .points(5000)
                .type(PointTransactionType.EARNED)
                .description("리뷰 작성 보상")
                .build();
        pointHistoryRepository.save(pointHistory);

        return review;
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
                .visitCount(visitCount)
                .build();
    }
    
    private boolean isImageFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return false; // 파일이 없는 경우도 처리
        }

        // 허용할 MIME 타입 목록 (이미지 파일)
        List<String> allowedMimeTypes = Arrays.asList("image/jpeg", "image/png", "image/gif");

        // Apache Tika를 사용하여 파일의 실제 MIME 타입을 감지
        Tika tika = new Tika();
        String mimeType = tika.detect(file.getInputStream());
        
        System.out.println("감지된 실제 MIME 타입: " + mimeType);

        // 실제 MIME 타입이 허용 목록에 있는지 확인
        return allowedMimeTypes.contains(mimeType);
    }

}