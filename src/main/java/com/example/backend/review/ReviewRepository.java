package com.example.backend.review;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	
	@Query("SELECT r FROM Review r JOIN FETCH r.user WHERE r.hotel.contentid = :hotelId ORDER BY r.createdAt DESC")
    List<Review> findByHotelContentid(@Param("hotelId") String hotelId);

    List<Review> findByUserUsernameOrderByCreatedAtDesc(String username);

    @Query("SELECT COUNT(res) FROM Reservation res WHERE res.user.username = :username AND res.hotel.contentid = :hotelId AND res.checkInDate <= :checkInDate")
    long countReservationsByUserAndHotelBeforeDate(@Param("username") String username, @Param("hotelId") String hotelId, @Param("checkInDate") LocalDate checkInDate);

    boolean existsByReservationReservationId(Long reservationId);

    @Modifying
    @Transactional
    @Query("UPDATE Review r SET r.isDeleted = TRUE WHERE r.reviewId = :id")
    int softDeleteById(@Param("id") String id);

    @Modifying
    @Transactional
    @Query("UPDATE Review r SET r.isDeleted = TRUE WHERE r.reviewId IN :ids")
    int softDeleteAllByIds(@Param("ids") List<String> ids);

    @Query("SELECT r FROM Review r WHERE r.isDeleted = :show")
    List<Review> findAllViewable(@Param("show") boolean show);
}