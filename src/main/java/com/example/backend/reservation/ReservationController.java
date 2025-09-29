package com.example.backend.reservation;

import com.example.backend.api2.Detail;
import com.example.backend.api2.DetailRepa;
import com.example.backend.authentication.User;
import com.example.backend.authentication.UserRepository;
import com.example.backend.coupon.entity.Coupon;
import com.example.backend.coupon.entity.UserCoupon;
import com.example.backend.coupon.repository.CouponRepository;
import com.example.backend.coupon.repository.UserCouponRepository;
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
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final DetailRepa detailRepa;
    private final UserCouponRepository userCouponRepository;

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

    @PostMapping("/{reservationId}/apply-points")
    public ResponseEntity<?> applyPoints(
            @PathVariable Long reservationId,
            @RequestBody ReservationRequestDto requestDto,
            Authentication authentication) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약이 존재하지 않습니다."));
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        int pointsToUse = requestDto.getUsedPoints();
        if (user.getPoint() < pointsToUse) {
            return ResponseEntity.badRequest().body("보유 포인트가 부족합니다.");
        }

        // 쿠폰 할인이 먼저 적용되었다면 그 가격을 기준으로 포인트 차감
        int priceBeforePoints = reservation.getBasePrice();
        if (reservation.getUsedCoupon() != null) {
            priceBeforePoints = calculateDiscountedPrice(reservation.getBasePrice(), reservation.getUsedCoupon());
        }

        int finalPrice = priceBeforePoints - pointsToUse;

        reservation.setUsedPoints(pointsToUse); // 사용한 포인트 기록
        reservation.setTotalPrice(finalPrice);
        reservation.setDiscountPrice(reservation.getBasePrice() - finalPrice); // 총 할인액 업데이트
        reservationRepository.save(reservation);

        Detail roomDetail = detailRepa.findById(Long.parseLong(reservation.getRoomcode()))
                .orElseThrow(() -> new IllegalArgumentException("객실 정보를 찾을 수 없습니다."));
        return ResponseEntity.ok(ReservationDto.from(reservation, roomDetail));
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


    @PatchMapping("/{reservationId}/update-price")
    public ResponseEntity<?> updateReservationPrice(
            @PathVariable Long reservationId,
            @RequestBody Map<String, Object> updateData,
            Authentication authentication) {

        try {
            Reservation reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new IllegalArgumentException("예약이 존재하지 않습니다."));

            // PENDING 상태인지 확인
            if (!"PENDING".equals(reservation.getStatus())) {
                return ResponseEntity.badRequest().body("이미 처리된 예약입니다.");
            }

            // 가격 정보 업데이트
            Integer totalPrice = (Integer) updateData.get("totalPrice");
            Integer discountPrice = (Integer) updateData.get("discountPrice");
            Integer usedPoints = (Integer) updateData.get("usedPoints");
            Long userCouponId = updateData.get("userCouponId") != null ?
                    Long.valueOf(updateData.get("userCouponId").toString()) : null;

            // 총 금액 업데이트
            reservation.setTotalPrice(totalPrice);
            reservation.setDiscountPrice(discountPrice);
            reservation.setUsedPoints(usedPoints);

            // 쿠폰 정보 저장
            if (userCouponId != null) {
                UserCoupon userCoupon = userCouponRepository.findById(userCouponId).orElse(null);
                if (userCoupon != null) {
                    reservation.setUsedCoupon(userCoupon.getCoupon());
                }
            }

            reservationRepository.save(reservation);

            return ResponseEntity.ok(Map.of(
                    "message", "예약 정보가 업데이트되었습니다.",
                    "reservationId", reservationId,
                    "totalPrice", totalPrice
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("예약 업데이트 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/user/{username}/points")
    public ResponseEntity<List<UserPointHistoryDto>> getUserPoints(@PathVariable String username) {
        return ResponseEntity.ok(reservationService.getUserPointHistory(username));
    }

}
