package com.example.backend.payment;

import com.example.backend.authentication.User;
import com.example.backend.authentication.UserRepository;
import com.example.backend.reservation.Reservation;
import com.example.backend.reservation.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;
    private final EmailService emailService;
    private final UserRepository userRepository;

    @Value("${toss.widget-secret-key}")
    private String tossWidgetSecretKey;

    @Transactional
    public String cancelPayment(Long reservationId, String cancelReason) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약을 찾을 수 없습니다."));

        if (!"PAID".equals(reservation.getStatus())) {
            throw new IllegalStateException("결제가 완료된 예약만 취소할 수 있습니다.");
        }

        LocalDate today = LocalDate.now();
        LocalDate checkInDate = reservation.getCheckInDate();

        if (today.isAfter(checkInDate) || today.isEqual(checkInDate)) {
            throw new IllegalStateException("체크인 날짜가 지났거나 당일인 예약은 취소할 수 없습니다.");
        }

        Payment payment = paymentRepository.findByReservation(reservation)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

        long daysBeforeCheckIn = ChronoUnit.DAYS.between(today, checkInDate);
        int originalAmount = payment.getPaymentAmount();
        int cancelFee = calculateCancelFee(originalAmount, daysBeforeCheckIn);
        int refundAmount = originalAmount - cancelFee;

        String url = "https://api.tosspayments.com/v1/payments/" + payment.getPaymentKey() + "/cancel";

        HttpHeaders headers = new HttpHeaders();
        String encodedAuth = new String(Base64.getEncoder().encode((tossWidgetSecretKey + ":").getBytes(StandardCharsets.UTF_8)));
        headers.set("Authorization", "Basic " + encodedAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("cancelReason", cancelReason);
        if (cancelFee > 0) {
            body.put("cancelAmount", refundAmount);
        }

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForObject(url, request, Map.class);

            if (reservation.getUsedPoints() != null && reservation.getUsedPoints() > 0) {
                User user = reservation.getUser();
                if (user != null) {
                    System.out.println("=== 포인트 환불 시작 ===");
                    System.out.println("사용자: " + user.getUsername());
                    System.out.println("현재 포인트: " + user.getPoint());
                    System.out.println("환불할 포인트: " + reservation.getUsedPoints());

                    // 포인트 환불
                    int currentPoints = user.getPoint() != null ? user.getPoint() : 0;
                    user.addPoints(currentPoints + reservation.getUsedPoints());
                    userRepository.save(user);

                    System.out.println("환불 후 포인트: " + user.getPoint());
                    System.out.println("=== 포인트 환불 완료 ===");
                }
            }

            reservation.setStatus("CANCELLED");
            payment.setPaymentStatus("CANCELED");

            reservationRepository.save(reservation);
            paymentRepository.save(payment);

            emailService.sendCancellationConfirmationEmail(reservation, refundAmount, cancelFee);


            return String.format("취소가 완료되었습니다. 환불 금액: %,d원 (수수료: %,d원)", refundAmount, cancelFee);

        } catch (Exception e) {
            throw new RuntimeException("결제 취소 중 오류가 발생했습니다.", e);
        }
    }

    public int calculateCancelFee(int totalAmount, long daysBeforeCheckIn) {
        if (daysBeforeCheckIn >= 3) {
            return 0;
        } else if (daysBeforeCheckIn == 2) {
            return (int) (totalAmount * 0.2);
        } else if (daysBeforeCheckIn == 1) {
            return (int) (totalAmount * 0.5);
        }
        return totalAmount;
    }
}