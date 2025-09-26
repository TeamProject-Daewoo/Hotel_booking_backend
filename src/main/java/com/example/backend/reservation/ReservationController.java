package com.example.backend.reservation;

import com.example.backend.mypage.BookingResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        String username = null;
        if (authentication != null && authentication.isAuthenticated()) {
            username = authentication.getName();
        }
        
        Reservation createdReservation = reservationService.createReservation(requestDto, username);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReservation);
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDto> getReservationDetails(@PathVariable Long reservationId) {
        ReservationDto reservationDto = reservationService.findReservationById(reservationId);
        return ResponseEntity.ok(reservationDto);
    }

    @GetMapping("/lookup")
    public ResponseEntity<List<BookingResponseDto>> getNonMemberReservations(
            @RequestParam(required = false) Long reservationId,
            @RequestParam(required = false) String reservName,
            @RequestParam(required = false) String reservPhone) {

        List<Reservation> reservations = new ArrayList<>();
        if (reservationId != null) {
            reservationRepository.findByIdAndUserIsNull(reservationId)
                .ifPresent(reservations::add);
        } else if (reservName != null && !reservName.isEmpty() && reservPhone != null && !reservPhone.isEmpty()) {
            reservations = reservationRepository.findByReservNameAndReservPhoneAndUserIsNull(reservName, reservPhone);
        } else {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        List<BookingResponseDto> bookingDtos = reservations.stream()
                .map(reservation -> BookingResponseDto.builder()
                        .reservationId(reservation.getReservationId())
                        .hotelId(reservation.getHotel().getContentid())
                        .hotelName(reservation.getHotel().getTitle())
                        .checkInDate(reservation.getCheckInDate())
                        .checkOutDate(reservation.getCheckOutDate())
                        .status(reservation.getStatus())
                        .totalPrice(reservation.getTotalPrice())
                        .numAdults(reservation.getNumAdults())
                        .numChildren(reservation.getNumChildren())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(bookingDtos);
    }
    
    @DeleteMapping("/pending/{reservationId}")
    public ResponseEntity<Void> cancelPendingReservation(@PathVariable Long reservationId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = authentication.getName();
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("해당 예약을 찾을 수 없습니다."));

        if (reservation.getUser() != null && reservation.getUser().getUsername().equals(username) && "PENDING".equals(reservation.getStatus())) {
            reservationRepository.delete(reservation);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping("/availability")
    public ResponseEntity<AvailabilityResponseDto> checkAvailability(@RequestBody AvailabilityRequestDto requestDto) {
        Map<LocalDate, Map<Long, Integer>> availability = reservationService.getRoomAvailability(requestDto);
        return ResponseEntity.ok(new AvailabilityResponseDto(availability));
    }
}