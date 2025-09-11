package com.example.backend.mypage;

import com.example.backend.api.Accommodation;
import com.example.backend.authentication.User;
import com.example.backend.authentication.UserRepository;
import com.example.backend.reservation.Reservation;
import com.example.backend.reservation.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MypageService {

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public ProfileResponseDto getMemberProfile(String memberId) {
        User user = userRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return new ProfileResponseDto(user.getUsername(), user.getEmail(), user.getPhoneNumber());
    }

    public void updateMemberProfile(String memberId, ProfileUpdateRequestDto requestDto) {
        User user = userRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (requestDto.getNewPassword() != null && !requestDto.getNewPassword().isEmpty()) {
            if (requestDto.getCurrentPassword() == null || !passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
            }
            user.updatePassword(passwordEncoder.encode(requestDto.getNewPassword()));
        }

        if (requestDto.getPhoneNumber() != null && !requestDto.getPhoneNumber().isEmpty()) {
            user.updateProfile(null, requestDto.getPhoneNumber());
        }
    }

    public void deleteMember(String memberId) {
        if (!userRepository.existsById(memberId)) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        userRepository.deleteById(memberId);
    }

    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookingList(String memberId) {
        List<Reservation> reservations = reservationRepository.findReservationsWithDetailsByUserName(memberId);

        return reservations.stream()
                .map(reservation -> {
                    Accommodation hotel = reservation.getHotel();

                    return BookingResponseDto.builder()
                        .reservationId(reservation.getReservationId())
                        .hotelId(hotel.getContentid())
                        .hotelName(hotel.getTitle())
                        .checkInDate(reservation.getCheckInDate())
                        .checkOutDate(reservation.getCheckOutDate())
                        .status(reservation.getStatus())
                        .totalPrice(reservation.getTotalPrice())
                        .numAdults(reservation.getNumAdults())
                        .numChildren(reservation.getNumChildren())
                        .build();
                })
                .collect(Collectors.toList());
    }
}