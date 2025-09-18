package com.example.backend.mypage;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.review.ReviewResponseDto;
import com.example.backend.review.ReviewService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final MypageService mypageService;
    private final ReviewService reviewService;

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
    @GetMapping("/likes")
    public ResponseEntity<List<LikeResponseDto>> getMyLikes(Authentication authentication) {
        String currentMemberId = authentication.getName();
        List<LikeResponseDto> likes = mypageService.getLikeList(currentMemberId);
        return ResponseEntity.ok(likes);
   }
//
//    @GetMapping("/payment-methods")
//    public ResponseEntity<List<CardResponseDto>> getMyPaymentMethods(Authentication authentication) {
//        String currentMemberId = authentication.getName();
//        List<CardResponseDto> cards = mypageService.getCardList(currentMemberId);
//        return ResponseEntity.ok(cards);
//    }
//
//    @PostMapping("/payment-methods/cards")
//    public ResponseEntity<CardResponseDto> addCard(@RequestBody CardRequestDto cardRequestDto, Authentication authentication) {
//        String currentMemberId = authentication.getName();
//        CardResponseDto newCard = mypageService.addCard(currentMemberId, cardRequestDto);
//        return ResponseEntity.ok(newCard);
//    }
//
//    @PatchMapping("/payment-methods/cards/{cardId}")
//    public ResponseEntity<Void> updateCard(@PathVariable Long cardId, @RequestBody CardRequestDto cardRequestDto, Authentication authentication) {
//        String currentMemberId = authentication.getName();
//        mypageService.updateCard(currentMemberId, cardId, cardRequestDto);
//        return ResponseEntity.ok().build();
//    }
//
//    @DeleteMapping("/payment-methods/cards/{cardId}")
//    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId, Authentication authentication) {
//        String currentMemberId = authentication.getName();
//        mypageService.deleteCard(currentMemberId, cardId);
//        return ResponseEntity.ok().build();
//    }
}