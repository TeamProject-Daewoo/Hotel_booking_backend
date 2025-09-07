package com.example.backend.mypage;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final MypageService mypageService;

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponseDto> getMyProfile() {
    	//회원가입, 로그인 구현 시 변경
        String currentMemberId = "normal_user";
        ProfileResponseDto profile = mypageService.getMemberProfile(currentMemberId);
        return ResponseEntity.ok(profile);
    }

    @PatchMapping("/profile")
    public ResponseEntity<Void> updateMyProfile(@RequestBody ProfileUpdateRequestDto profileUpdateRequestDto) {
        String currentMemberId = "normal_user";
        mypageService.updateMemberProfile(currentMemberId, profileUpdateRequestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/member")
    public ResponseEntity<Void> withdrawMember() {
        String currentMemberId = "normal_user";
        mypageService.deleteMember(currentMemberId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingResponseDto>> getMyBookings() {
        String currentMemberId = "normal_user";
        List<BookingResponseDto> bookings = mypageService.getBookingList(currentMemberId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewResponseDto>> getMyReviews() {
        String currentMemberId = "normal_user";
        List<ReviewResponseDto> reviews = mypageService.getReviewList(currentMemberId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/likes")
    public ResponseEntity<List<LikeResponseDto>> getMyLikes() {
        String currentMemberId = "normal_user";
        List<LikeResponseDto> likes = mypageService.getLikeList(currentMemberId);
        return ResponseEntity.ok(likes);
    }

    @GetMapping("/payment-methods")
    public ResponseEntity<List<CardResponseDto>> getMyPaymentMethods() {
        String currentMemberId = "normal_user";
        List<CardResponseDto> cards = mypageService.getCardList(currentMemberId);
        return ResponseEntity.ok(cards);
    }

    @PostMapping("/payment-methods/cards")
    public ResponseEntity<CardResponseDto> addCard(@RequestBody CardRequestDto cardRequestDto) {
        String currentMemberId = "normal_user";
        CardResponseDto newCard = mypageService.addCard(currentMemberId, cardRequestDto);
        return ResponseEntity.ok(newCard);
    }

    @PatchMapping("/payment-methods/cards/{cardId}")
    public ResponseEntity<Void> updateCard(@PathVariable Long cardId, @RequestBody CardRequestDto cardRequestDto) {
        String currentMemberId = "normal_user";
        mypageService.updateCard(currentMemberId, cardId, cardRequestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/payment-methods/cards/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        String currentMemberId = "normal_user";
        mypageService.deleteCard(currentMemberId, cardId);
        return ResponseEntity.ok().build();
    }
}