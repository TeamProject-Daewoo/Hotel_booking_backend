package com.example.backend.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r JOIN FETCH r.hotel h JOIN FETCH r.user u WHERE u.username = :userName")
    List<Reservation> findReservationsWithDetailsByUserName(@Param("userName") String userName);

}