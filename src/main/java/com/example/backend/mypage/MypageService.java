package com.example.backend.mypage;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MypageService {

    private final UserProfileRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public ProfileResponseDto getMemberProfile(String memberId) {
        UserProfile user = userRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        return ProfileResponseDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    public void updateMemberProfile(String memberId, ProfileUpdateRequestDto requestDto) {
        UserProfile user = userRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        
        user.updateProfile(requestDto.getName(), requestDto.getPhoneNumber());
        
        if (requestDto.getNewPassword() != null && !requestDto.getNewPassword().isBlank()) {
            user.updatePassword(passwordEncoder.encode(requestDto.getNewPassword()));
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
        List<Reservation> reservations = reservationRepository.findAllByUserNameWithHotel(memberId);
        return reservations.stream()
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
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewList(String memberId) {
        return Collections.emptyList();
    }

    @Transactional(readOnly = true)
    public List<LikeResponseDto> getLikeList(String memberId) {
        return Collections.emptyList();
    }

    @Transactional(readOnly = true)
    public List<CardResponseDto> getCardList(String memberId) {
        return Collections.emptyList();
    }

    public CardResponseDto addCard(String memberId, CardRequestDto cardRequestDto) {
        return null;
    }

    public void updateCard(String memberId, Long cardId, CardRequestDto cardRequestDto) {
        
    }

    public void deleteCard(String memberId, Long cardId) {
        
    }
}