package com.example.backend.reservation;

import com.example.backend.api.Hotels;
import com.example.backend.api.HotelsRepa;
import com.example.backend.authentication.User;
import com.example.backend.authentication.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final HotelsRepa accommodationRepository;

    @Transactional
    public Reservation createReservation(ReservationRequestDto requestDto, String username) {
        
        User user = null;
        // username이 null이 아닌 경우(로그인한 경우)에만 사용자 정보를 조회합니다.
        if (username != null) {
            user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        }

        Hotels hotel = accommodationRepository.findByContentid(requestDto.getContentid())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 숙소입니다."));

        Reservation newReservation = Reservation.builder()
                .user(user) // 비회원인 경우 이 값은 null이 됩니다.
                .hotel(hotel)
                .roomcode(requestDto.getRoomcode())
                .checkInDate(requestDto.getCheckInDate())
                .checkOutDate(requestDto.getCheckOutDate())
                .numAdults(requestDto.getNumAdults())
                .numChildren(requestDto.getNumChildren())
                .totalPrice(requestDto.getTotalPrice()) 
                .reservName(requestDto.getGuestName())
                .reservPhone(requestDto.getPhone())  
                .status("PENDING")
                .build();
        
        return reservationRepository.save(newReservation);
    }
}