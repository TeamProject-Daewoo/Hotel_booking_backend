package com.example.backend.mypage;

import com.example.backend.api.Hotels;
import com.example.backend.api.HotelsRepa;
import com.example.backend.authentication.User;
import com.example.backend.authentication.UserRepository;
import com.example.backend.reservation.Reservation;
import com.example.backend.reservation.ReservationRepository;
import com.example.backend.wish.WishRequestDto;
import com.example.backend.wish.Wishlist;
import com.example.backend.wish.WishlistRepository;

import jakarta.persistence.EntityNotFoundException;
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
    private final WishlistRepository wishlistRepository;
    private final HotelsRepa hotelsRepository;

    @Transactional(readOnly = true)
    public ProfileResponseDto getMemberProfile(String memberId) {
        User user = userRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return ProfileResponseDto.builder()
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    public void updateMemberProfile(String memberId, ProfileUpdateRequestDto requestDto) {
        User user = userRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 비밀번호 변경이 요청된 경우에만 처리
        if (requestDto.getNewPassword() != null && !requestDto.getNewPassword().isEmpty()) {
            // 현재 비밀번호 확인
            if (requestDto.getCurrentPassword() == null || !passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
            }
            // 새 비밀번호 암호화 및 업데이트
            user.updatePassword(passwordEncoder.encode(requestDto.getNewPassword()));
        }

        // 이름 또는 휴대폰 번호 업데이트
        user.updateProfile(requestDto.getName(), requestDto.getPhoneNumber());
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
                    Hotels hotel = reservation.getHotel();
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
    public List<LikeResponseDto> getMyWishList(String username) {
        return wishlistRepository.findLikedHotelsByMemberId(username);
    }
    public Wishlist saveWishList(WishRequestDto requestDto) {
        User user = userRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다."));
        Hotels hotel = hotelsRepository.findById(String.valueOf(requestDto.getHotelId())) // hotelId가 String 타입이라고 가정
                .orElseThrow(() -> new EntityNotFoundException("해당 호텔을 찾을 수 없습니다."));

        boolean alreadyExists = wishlistRepository.existsByUserAndHotel(user, hotel);
        if (alreadyExists) throw new IllegalStateException("이미 찜한 호텔입니다.");

        // 2. 존재하지 않을 때만 "새로 저장" (INSERT 쿼리 실행)
        return wishlistRepository.save(new Wishlist(user, hotel));
    }
}