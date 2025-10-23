package com.example.backend.mypage;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.point.PointHistoryDto;
import com.example.backend.review.ReviewResponseDto;
import com.example.backend.review.ReviewService;
import com.example.backend.wish.WishRequestDto;
import com.example.backend.wish.WishResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final MypageService mypageService;
    private final ReviewService reviewService;
    private static final Logger log = LoggerFactory.getLogger(MypageController.class);

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponseDto> getMyProfile(Authentication authentication) {
        String currentMemberId = authentication.getName();
        ProfileResponseDto profile = mypageService.getMemberProfile(currentMemberId);
        return ResponseEntity.ok(profile);
    }

    @PatchMapping("/profile")
    public ResponseEntity<Void> updateMyProfile(@RequestBody ProfileUpdateRequestDto profileUpdateRequestDto, Authentication authentication) {
        String currentMemberId = authentication.getName();
        mypageService.updateMemberProfile(currentMemberId, profileUpdateRequestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/member")
    public ResponseEntity<Void> withdrawMember(Authentication authentication) {

        // 사용자가 인증되지 않은 상태인지 확인합니다.
        if (authentication == null || !authentication.isAuthenticated()) {
        	log.warn("인증되지 않은 사용자의 요청입니다");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String currentMemberId = authentication.getName();
        mypageService.deleteMember(currentMemberId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingResponseDto>> getMyBookings(Authentication authentication) {
        String currentMemberId = authentication.getName();
        List<BookingResponseDto> bookings = mypageService.getBookingList(currentMemberId);
        return ResponseEntity.ok(bookings);
    }

    @PostMapping("/reviews")
    public ResponseEntity<Void> createReview(
            @RequestParam("reservationId") Long reservationId,
            @RequestParam("rating") int rating,
            @RequestParam("content") String content,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            Authentication authentication) throws IOException {

        String username = authentication.getName();
        reviewService.createReview(reservationId, username, rating, content, photo);
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewResponseDto>> getMyReviews(Authentication authentication) {
        String currentMemberId = authentication.getName();
        List<ReviewResponseDto> reviews = reviewService.getReviewsByUser(currentMemberId);
        return ResponseEntity.ok(reviews);
    }

   @GetMapping("/wishs")
   public ResponseEntity<List<WishResponseDto>> getMyWishList(Authentication authentication) {
       String currentMemberId = authentication.getName();
       List<WishResponseDto> likes = mypageService.getMyWishList(currentMemberId);
       return ResponseEntity.ok(likes);
   }

   @PostMapping("/savewish")
   public ResponseEntity<Void> saveWishList(Authentication authentication, @RequestBody WishRequestDto request) {
        String currentMemberId = authentication.getName();
        mypageService.saveWishList(request.getHotelId(), currentMemberId);
        return ResponseEntity.noContent().build();
   }
   @DeleteMapping("/deletewish")
   public ResponseEntity<Void> deleteWishList(Authentication authentication, @RequestBody WishRequestDto request) {
        String currentMemberId = authentication.getName();
        mypageService.deleteWishList(request.getHotelId(), currentMemberId);
        return ResponseEntity.noContent().build();
   }

    @GetMapping("/points")
    public ResponseEntity<List<PointHistoryDto>> getMyPointHistory(Authentication authentication) {
        String currentMemberId = authentication.getName();
        List<PointHistoryDto> pointHistory = mypageService.getPointHistory(currentMemberId);
        return ResponseEntity.ok(pointHistory);
    }
}