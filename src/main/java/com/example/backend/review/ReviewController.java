package com.example.backend.review;

import lombok.RequiredArgsConstructor;

import org.apache.ibatis.annotations.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsForHotel(@PathVariable String hotelId) {
        List<ReviewResponseDto> reviews = reviewService.getReviewsByHotel(hotelId);
        return ResponseEntity.ok(reviews);
    }
    @GetMapping("/viewAll")
    public ResponseEntity<List<AdminReviewResponseDto>> viewAll(@RequestParam boolean deletedShow, @RequestParam(required = false) String searchTerm) {
        return ResponseEntity.ok(reviewService.getReviewList(deletedShow, searchTerm));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Integer> deleteReview(@PathVariable String id) {
        return ResponseEntity.ok(reviewService.deleteReviewsById(id));
    }
    @DeleteMapping("/deleteAll")
    public ResponseEntity<Integer> deleteReviews(@RequestParam(required = false) List<String> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            System.out.println("삭제할 ID 리스트가 비어 있습니다. 쿼리를 실행하지 않습니다.");
            return null;
        }
        return ResponseEntity.ok(reviewService.deleteReviewsByIds(reviews));
    }
    
}