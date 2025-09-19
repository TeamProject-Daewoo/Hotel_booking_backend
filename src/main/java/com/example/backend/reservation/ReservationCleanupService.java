package com.example.backend.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationCleanupService {

    private final ReservationRepository reservationRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cleanupPendingReservations() {
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);
        List<Reservation> expiredReservations = reservationRepository.findAllByStatusAndReservationDateBefore(
            "PENDING", thirtyMinutesAgo
        );

        if (!expiredReservations.isEmpty()) {
            reservationRepository.deleteAll(expiredReservations);
            System.out.println(expiredReservations.size() + "건의 만료된 PENDING 예약을 삭제했습니다.");
        }
    }
}