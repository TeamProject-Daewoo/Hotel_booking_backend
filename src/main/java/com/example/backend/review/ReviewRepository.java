package com.example.backend.review;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	
	 // 특정 호텔의 모든 리뷰를 조회 (최신순)
    @Query("SELECT r FROM Review r JOIN FETCH r.user WHERE r.hotel.contentid = :hotelId ORDER BY r.createdAt DESC")
    List<Review> findByHotelContentid(@Param("hotelId") String hotelId);

    // 특정 사용자가 작성한 모든 리뷰를 조회 (최신순)
    List<Review> findByUserUsernameOrderByCreatedAtDesc(String username);
}