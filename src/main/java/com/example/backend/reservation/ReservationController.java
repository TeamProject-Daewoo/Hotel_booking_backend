package com.example.backend.reservation;

import com.example.backend.coupon.entity.Coupon;
import com.example.backend.coupon.repository.CouponRepository;
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
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;
    private final CouponRepository couponRepository;

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
        var availability = reservationService.getRoomAvailability(requestDto);
        return ResponseEntity.ok(new AvailabilityResponseDto(availability));
    }

    @GetMapping("/{reservationId}/apply-coupon/{couponId}")
    public ResponseEntity<?> applyCoupon(
            @PathVariable Long reservationId,
            @PathVariable Long couponId) {

        Reservation reservation = reservationRepository.findById(reservationId).orElse(null);
        if (reservation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("예약이 존재하지 않습니다. ID=" + reservationId);
        }

        Coupon coupon = couponRepository.findById(couponId).orElse(null);
        if (coupon == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("쿠폰이 존재하지 않습니다. ID=" + couponId);
        }

        int discountedPrice = calculateDiscountedPrice(reservation.getBasePrice(), coupon);

        // 할인 가격 세팅
        reservation.setDiscountPrice(discountedPrice);

        // totalPrice를 할인된 가격으로 변경
        reservation.setTotalPrice(discountedPrice);

        // DB에 변경 저장
        reservationRepository.save(reservation);

        // DTO 변환 (ReservationRequestDto에 setter가 반드시 필요함)
        ReservationRequestDto dto = new ReservationRequestDto();
        dto.setReservationId(reservation.getReservationId());
        dto.setContentid(reservation.getHotel().getContentid());
        dto.setRoomcode(reservation.getRoomcode());
        dto.setCheckInDate(reservation.getCheckInDate());
        dto.setCheckOutDate(reservation.getCheckOutDate());
        dto.setNumAdults(reservation.getNumAdults());
        dto.setNumChildren(reservation.getNumChildren());
        dto.setTotalPrice(reservation.getTotalPrice());
        dto.setBasePrice(reservation.getBasePrice());
        dto.setDiscountPrice(reservation.getDiscountPrice());
        dto.setGuestName(reservation.getReservName());
        dto.setPhone(reservation.getReservPhone());

        return ResponseEntity.ok(dto);
    }

    private int calculateDiscountedPrice(Integer basePrice, Coupon coupon) {
        if (coupon.getDiscountPercent() != null && coupon.getDiscountPercent() > 0) {
            double discountRate = coupon.getDiscountPercent() / 100.0;
            return Math.max((int)(basePrice * (1 - discountRate)), 0);
        } else {
            int discountAmount = coupon.getDiscountAmount() != null ? coupon.getDiscountAmount() : 0;
            return Math.max(basePrice - discountAmount, 0);
        }
    }
}
