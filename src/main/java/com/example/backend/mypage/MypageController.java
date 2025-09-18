package com.example.backend.mypage;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.backend.wish.WishRequestDto;

import java.util.List;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final MypageService mypageService;

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

//    @GetMapping("/reviews")
//    public ResponseEntity<List<ReviewResponseDto>> getMyReviews(Authentication authentication) {
//        String currentMemberId = authentication.getName();
//        List<ReviewResponseDto> reviews = mypageService.getReviewList(currentMemberId);
//        return ResponseEntity.ok(reviews);
//    }
//
   @GetMapping("/likes")
   public ResponseEntity<List<LikeResponseDto>> getMyWishList(Authentication authentication) {
       String currentMemberId = authentication.getName();
       List<LikeResponseDto> likes = mypageService.getMyWishList(currentMemberId);
       return ResponseEntity.ok(likes);
   }
   @GetMapping("/savewish")
   public ResponseEntity<Void> saveWishList(Authentication authentication, int hotelId) {
        String currentMemberId = authentication.getName();
        mypageService.saveWishList(new WishRequestDto(hotelId, currentMemberId));
        return ResponseEntity.noContent().build();
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