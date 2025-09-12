package com.example.backend.reservation;

import com.example.backend.api.Accommodation;
import com.example.backend.api.AccommodationRepa;
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
    private final AccommodationRepa accommodationRepository;

    @Transactional
    public Reservation createReservation(ReservationRequestDto requestDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Accommodation hotel = accommodationRepository.findById(requestDto.getContentid())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 숙소입니다."));

        Reservation newReservation = Reservation.builder()
                .user(user)
                .hotel(hotel)
                .roomcode(requestDto.getRoomcode())
                .checkInDate(requestDto.getCheckInDate())
                .checkOutDate(requestDto.getCheckOutDate())
                .numAdults(requestDto.getNumAdults())
                .numChildren(requestDto.getNumChildren())
                .totalPrice(requestDto.getTotalPrice()) 
                .status("PENDING")
                .build();
        
        return reservationRepository.save(newReservation);
    }
}