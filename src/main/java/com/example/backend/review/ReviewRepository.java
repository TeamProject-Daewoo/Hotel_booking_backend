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
	
	@Query("SELECT r FROM Review r JOIN FETCH r.user WHERE r.hotel.contentid = :hotelId AND r.isDeleted = FALSE ORDER BY r.createdAt DESC")
    List<Review> findByHotelContentid(@Param("hotelId") String hotelId);

    List<Review> findByUserUsernameAndIsDeletedFalseOrderByCreatedAtDesc(String username);

    @Query("SELECT COUNT(res) FROM Reservation res WHERE res.user.username = :username AND res.hotel.contentid = :hotelId AND res.checkInDate <= :checkInDate")
    long countReservationsByUserAndHotelBeforeDate(@Param("username") String username, @Param("hotelId") String hotelId, @Param("checkInDate") LocalDate checkInDate);

    boolean existsByReservationReservationId(Long reservationId);

    @Modifying
    @Transactional
    @Query("UPDATE Review r SET r.isDeleted = :isDelete WHERE r.reviewId = :id")
    int softDeleteById(@Param("id") String id, @Param("isDelete") boolean isDelete);

    @Modifying
    @Transactional
    @Query("UPDATE Review r SET r.isDeleted = :isDelete WHERE r.reviewId IN :ids")
    int softDeleteAllByIds(@Param("ids") List<String> ids, @Param("isDelete") boolean isDelete);

    @Query("SELECT r FROM Review r " +
            "WHERE r.isDeleted = :show " +
            "AND (" +
            ":#{#searchTerm == null || #searchTerm.isEmpty()} = TRUE OR " +
            "r.contentChosung LIKE CONCAT(:searchTerm, '%') " + 
            ") "+ 
            "ORDER BY r.createdAt DESC")
    List<Review> findAllViewable(@Param("show") boolean show, @Param("searchTerm") String searchTerm);

    @Query("SELECT r FROM Review r " +
            "WHERE r.hotel.contentid = :hotelId " +
            "AND (" +
            ":#{#searchTerm == null || #searchTerm.isEmpty()} = TRUE OR " +
            "r.contentChosung LIKE CONCAT(:searchTerm, '%') " + 
            ") "+ 
            "ORDER BY r.createdAt DESC")
    List<Review> findByContentIdWithKeyword(@Param("hotelId") String hotelId, @Param("searchTerm") String searchTerm);

    @Modifying
    @Transactional
    @Query("UPDATE Review r SET r.isReported = :isReported WHERE r.reviewId = :id")
    int reportById(@Param("id") String id, @Param("isReported") boolean isReported);

    @Modifying
    @Transactional
    @Query("UPDATE Review r SET r.isReported = :isReported WHERE r.reviewId IN :ids")
    int reportAllByIds(@Param("ids") List<String> ids, @Param("isReported") boolean isReported);

    @Query("SELECT r FROM Review r " +
            "WHERE r.isReported = TRUE " +
            "AND (" +
            ":#{#searchTerm == null || #searchTerm.isEmpty()} = TRUE OR " +     
            "r.user.name LIKE %:searchTerm% OR r.content LIKE %:searchTerm% OR " +
            "r.contentChosung LIKE :searchTerm || '%' " + 
            ") "+ 
            "ORDER BY r.createdAt DESC")
    List<Review> findAllReportedList(@Param("searchTerm") String searchTerm);

}