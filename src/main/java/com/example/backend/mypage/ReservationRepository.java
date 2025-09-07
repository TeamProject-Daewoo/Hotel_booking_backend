package com.example.backend.mypage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r JOIN FETCH r.hotel WHERE r.user.userName = :userName ORDER BY r.checkInDate DESC")
    List<Reservation> findAllByUserNameWithHotel(@Param("userName") String userName);
}