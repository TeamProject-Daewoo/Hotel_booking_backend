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
    private final ObjectMapper objectMapper;

    @Value("${toss.widget-secret-key}")
    private String widgetSecretKey;

    @Transactional
    @PostMapping("/confirm")
    public ResponseEntity<JsonNode> confirmPayment(@RequestBody PaymentDto paymentDto) {

        Reservation reservation = reservationRepository.findByIdWithDetails(paymentDto.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다. ID: " + paymentDto.getReservationId()));

        // (이하 코드는 기존과 동일합니다)
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

                Payment newPayment = Payment.builder()
                        .reservation(reservation)
                        .userName(reservation.getUser().getUsername())
                        .paymentKey(successPayload.path("paymentKey").asText())
                        .paymentAmount(successPayload.path("totalAmount").asInt())
                        .paymentMethod(successPayload.path("method").asText())
                        .paymentStatus(successPayload.path("status").asText())
                        .build();
                paymentRepository.save(newPayment);
            }
            
            return responseEntity;

        } catch (Exception e) {
        	reservation.setStatus("FAIL");
            throw new RuntimeException("결제 승인 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}