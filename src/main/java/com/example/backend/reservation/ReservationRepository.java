package com.example.backend.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	
	@Query("SELECT r FROM Reservation r LEFT JOIN FETCH r.user LEFT JOIN FETCH r.hotel WHERE r.reservationId = :reservationId")
	Optional<Reservation> findByIdWithDetails(@Param("reservationId") Long reservationId);

    @Query("SELECT r FROM Reservation r JOIN FETCH r.hotel h JOIN FETCH r.user u WHERE u.username = :userName")
    List<Reservation> findReservationsWithDetailsByUserName(@Param("userName") String userName);
    
    List<Reservation> findAllByStatusAndReservationDateBefore(String status, LocalDateTime thirtyMinutesAgo);
    
    @Query("SELECT r FROM Reservation r JOIN FETCH r.hotel WHERE r.user IS NULL AND r.reservName = :reservName AND r.reservPhone = :reservPhone")
    List<Reservation> findByReservNameAndReservPhoneAndUserIsNull(@Param("reservName") String reservName, @Param("reservPhone") String reservPhone);

    @Query("SELECT r FROM Reservation r JOIN FETCH r.hotel WHERE r.reservationId = :reservationId AND r.user IS NULL")
    Optional<Reservation> findByIdAndUserIsNull(@Param("reservationId") Long reservationId);

    @Query("SELECT r FROM Reservation r WHERE r.hotel.contentid = :contentId AND r.status = 'PAID' AND r.checkInDate < :endDate AND r.checkOutDate > :startDate")
    List<Reservation> findPaidReservationsForDateRange(@Param("contentId") String contentId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    
    @Query("SELECT r FROM Reservation r WHERE r.hotel.contentid IN :contentIds AND r.status = 'PAID' AND r.checkInDate < :endDate AND r.checkOutDate > :startDate")
    List<Reservation> findPaidReservationsForDateRangeAndContentIds(
        @Param("contentIds") List<String> contentIds,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}