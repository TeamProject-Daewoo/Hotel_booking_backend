package com.example.backend.payment;

import com.example.backend.reservation.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendReservationConfirmationEmail(Reservation reservation) {
        if (reservation.getUser() == null || reservation.getUser().getUsername() == null) {
            // 비회원이거나 이메일 정보가 없는 경우 이메일을 보내지 않음
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(reservation.getUser().getUsername()); // 회원의 이메일 주소
        message.setSubject("[HotelHub] 예약이 확정되었습니다.");
        
        String emailText = String.format(
            "안녕하세요, %s님!\n\n" +
            "HotelHub 예약이 성공적으로 확정되었습니다.\n\n" +
            "■ 예약 번호: %d\n" +
            "■ 숙소명: %s\n" +
            "■ 체크인: %s\n" +
            "■ 체크아웃: %s\n" +
            "■ 총 결제 금액: %,d원\n\n" +
            "이용해주셔서 감사합니다.",
            reservation.getUser().getName(),
            reservation.getReservationId(),
            reservation.getHotel().getTitle(),
            reservation.getCheckInDate().toString(),
            reservation.getCheckOutDate().toString(),
            reservation.getTotalPrice()
        );
        
        message.setText(emailText);
        
        mailSender.send(message);
    }
}