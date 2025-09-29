package com.example.backend.payment;

import com.example.backend.authentication.User;
import com.example.backend.authentication.UserRepository;
import com.example.backend.reservation.Reservation;
import com.example.backend.reservation.ReservationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.example.backend.coupon.repository.UserCouponRepository;
// 날짜 관련
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Map 관련
import java.util.HashMap;



import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final UserCouponRepository userCouponRepository;
    private final ObjectMapper objectMapper;
    private final EmailService emailService; // EmailService 주입
    private final PaymentService paymentService;
    private final UserRepository userRepository;


    @Value("${toss.widget-secret-key}")
    private String widgetSecretKey;

    @Transactional
    @PostMapping("/confirm")
    public ResponseEntity<JsonNode> confirmPayment(@RequestBody PaymentDto paymentDto) {

        Reservation reservation = reservationRepository.findByIdWithDetails(paymentDto.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다. ID: " + paymentDto.getReservationId()));

        Integer amountFromClient = paymentDto.getAmount();

        if (!Objects.equals(reservation.getTotalPrice(), amountFromClient)) {
            System.out.println("경고: 클라이언트 요청 금액과 DB 저장 금액이 일치하지 않습니다.");
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String encryptedSecretKey = "Basic " + Base64.getEncoder().encodeToString((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", encryptedSecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, Object> params = Map.of(
        		"orderId", paymentDto.getOrderId(),
        		"paymentKey", paymentDto.getPaymentKey(),
                "amount", amountFromClient
        );

        try {
            ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(
                    "https://api.tosspayments.com/v1/payments/confirm",
                    new HttpEntity<>(params, headers),
                    JsonNode.class
            );

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                JsonNode successPayload = responseEntity.getBody();

                // ✅ 포인트 차감 로직 추가 (디버깅 로그 포함)
                System.out.println("=== 포인트 차감 시작 ===");
                System.out.println("예약의 used_points: " + reservation.getUsedPoints());

                if (reservation.getUsedPoints() != null && reservation.getUsedPoints() > 0) {
                    User user = reservation.getUser();
                    if (user != null) {
                        user.usePoints(reservation.getUsedPoints());
                        userRepository.save(user);
                    } else {
                        System.out.println("사용자가 null입니다 (비회원 예약)");
                    }
                } else {
                    System.out.println("사용된 포인트가 0이거나 null입니다");
                }

                reservation.setStatus("PAID");
                reservationRepository.save(reservation);

                String userNameForPayment = (reservation.getUser() != null)
                    ? reservation.getUser().getUsername()
                    : reservation.getReservName();

                Payment newPayment = Payment.builder()
                        .reservation(reservation)
                        .userName(userNameForPayment)
                        .paymentKey(successPayload.path("paymentKey").asText())
                        .paymentAmount(successPayload.path("totalAmount").asInt())
                        .paymentMethod(successPayload.path("method").asText())
                        .paymentStatus(successPayload.path("status").asText())
                        .build();
                paymentRepository.save(newPayment);

                // ✅ 쿠폰 삭제 로직 (프론트에서 보낸 userCouponId 활용)
                if (paymentDto.getUserCouponId() != null) {
                    userCouponRepository.deleteById(paymentDto.getUserCouponId());
                }

                
                // 이메일 발송 서비스 호출
               // emailService.sendReservationConfirmationEmail(reservation);
            }

            return responseEntity;

        } catch (Exception e) {
        	reservation.setStatus("FAIL");
            reservationRepository.save(reservation);
            throw new RuntimeException("결제 승인 중 오류가 발생했습니다: " + e.getMessage(), e);
        }

    }

    @PostMapping("/cancel")
    public ResponseEntity<String> cancelPayment(@RequestBody Map<String, Object> payload) {
        try {
            Long reservationId = Long.parseLong(payload.get("reservationId").toString());
            String cancelReason = payload.get("cancelReason").toString();
            String resultMessage = paymentService.cancelPayment(reservationId, cancelReason);
            return ResponseEntity.ok(resultMessage);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

         @PostMapping("/cancel-preview")
public ResponseEntity<Map<String, Object>> previewCancel(@RequestBody Map<String, Object> payload) {
    Long reservationId = Long.parseLong(payload.get("reservationId").toString());
    Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));
    Payment payment = paymentRepository.findByReservation(reservation)
            .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

    LocalDate today = LocalDate.now();
    long daysBeforeCheckIn = ChronoUnit.DAYS.between(today, reservation.getCheckInDate());
    int cancelFee = paymentService.calculateCancelFee(payment.getPaymentAmount(), daysBeforeCheckIn);
    int refundAmount = payment.getPaymentAmount() - cancelFee;

    Map<String, Object> result = new HashMap<>();
    result.put("refundAmount", refundAmount);
    result.put("cancelFee", cancelFee);

    return ResponseEntity.ok(result);
}
}