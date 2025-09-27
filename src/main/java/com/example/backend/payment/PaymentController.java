package com.example.backend.payment;

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
}