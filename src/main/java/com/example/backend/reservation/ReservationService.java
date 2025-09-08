package com.example.backend.reservation;

import com.example.backend.api.AccommodationDto;
import com.example.backend.api.AccommodationRepa;
import com.example.backend.mypage.User;
import com.example.backend.mypage.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final AccommodationRepa hotelRepository;

    @Transactional
    public Reservation createReservation(ReservationRequestDto requestDto, String userName) {
        User user = userRepository.findById(userName)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        AccommodationDto hotel = hotelRepository.findById(requestDto.getContentid())
                .orElseThrow(() -> new IllegalArgumentException("호텔을 찾을 수 없습니다."));

        Reservation reservation = Reservation.builder()
                .user(user)
                .hotel(hotel)
                .roomcode(requestDto.getRoomcode())
                .checkInDate(requestDto.getCheckInDate())
                .checkOutDate(requestDto.getCheckOutDate())
                .numAdults(requestDto.getNumAdults())
                .numChildren(requestDto.getNumChildren())
                .totalPrice(requestDto.getTotalPrice())
                .status("RESERVED")
                .build();

        return reservationRepository.save(reservation);
    }
}