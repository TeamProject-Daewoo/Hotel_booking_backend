package com.example.backend.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;

    @PostMapping
    public ResponseEntity<Reservation> createReservation(
            @RequestBody ReservationRequestDto requestDto,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String username = authentication.getName();
        Reservation createdReservation = reservationService.createReservation(requestDto, username);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReservation);
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDto> getReservationDetails(@PathVariable Long reservationId) {
        // 이 API는 인증된 사용자만 자신의 예약 정보를 볼 수 있도록 추가 보안 로직이 필요할 수 있습니다.
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약을 찾을 수 없습니다. ID: " + reservationId));
        
        ReservationDto dto = ReservationDto.builder()
                                .reservation(reservation)
                                .build();
        
        return ResponseEntity.ok(dto);
    }
    
    @DeleteMapping("/pending/{reservationId}")
    public ResponseEntity<Void> cancelPendingReservation(@PathVariable Long reservationId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = authentication.getName();
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("해당 예약을 찾을 수 없습니다."));

        // 본인의 예약이 맞는지, PENDING 상태가 맞는지 확인 후 삭제
        if (reservation.getUser().getUsername().equals(username) && "PENDING".equals(reservation.getStatus())) {
            reservationRepository.delete(reservation);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}