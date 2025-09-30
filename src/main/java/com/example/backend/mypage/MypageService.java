package com.example.backend.mypage;

import com.example.backend.api.Hotels;
import com.example.backend.api.HotelsRepa;
import com.example.backend.authentication.User;
import com.example.backend.authentication.UserRepository;
import com.example.backend.point.PointHistory;
import com.example.backend.point.PointHistoryDto;
import com.example.backend.point.PointHistoryRepository;
import com.example.backend.reservation.Reservation;
import com.example.backend.reservation.ReservationRepository;
import com.example.backend.review.ReviewRepository;
import com.example.backend.wish.WishResponseDto;
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
    private final ReviewRepository reviewRepository;
    private final HotelsRepa hotelsRepository;
    private final PointHistoryRepository pointHistoryRepository; // 1. PointHistoryRepository 주입

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
                    // 해당 예약에 대한 리뷰가 존재하는지 확인
                    boolean hasReview = reviewRepository.existsByReservationReservationId(reservation.getReservationId());
                    
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
                        .basePrice(reservation.getBasePrice())
                        .discountPrice(reservation.getDiscountPrice())
                        .hasReview(hasReview)
                        .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PointHistoryDto> getPointHistory(String username) {
        List<PointHistory> histories = pointHistoryRepository.findByUser_UsernameOrderByTransactionDateDesc(username);

        return histories.stream()
                .map(PointHistoryDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<WishResponseDto> getMyWishList(String username) {
        return wishlistRepository.findLikedHotelsByMemberId(username);
    }
    @Transactional
    public Wishlist saveWishList(String hotelId, String username) {
        WishRequestDto requestDto = new WishRequestDto(hotelId, username);

        User user = userRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다."));
        Hotels hotel = hotelsRepository.findById(requestDto.getHotelId())
                .orElseThrow(() -> new EntityNotFoundException("해당 호텔을 찾을 수 없습니다."));

        boolean alreadyExists = wishlistRepository.existsByUserAndHotel(user, hotel);
        if (alreadyExists) throw new IllegalStateException("이미 찜한 호텔입니다.");

        return wishlistRepository.save(new Wishlist(user, hotel));
    }

    @Transactional
    public void deleteWishList(String hotelId, String username) {
        wishlistRepository.deleteByUser_UsernameAndHotel_Contentid(username, hotelId);
    }
}