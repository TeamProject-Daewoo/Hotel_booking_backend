package com.example.backend.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	
	@Query("SELECT r FROM Reservation r JOIN FETCH r.user JOIN FETCH r.hotel WHERE r.reservationId = :reservationId")
	Optional<Reservation> findByIdWithDetails(@Param("reservationId") Long reservationId);

    @Query("SELECT r FROM Reservation r JOIN FETCH r.hotel h JOIN FETCH r.user u WHERE u.username = :userName")
    List<Reservation> findReservationsWithDetailsByUserName(@Param("userName") String userName);
    
    List<Reservation> findAllByStatusAndReservationDateBefore(String status, LocalDateTime thirtyMinutesAgo);
    
}