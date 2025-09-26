package com.example.backend.reservation;

import com.example.backend.api.Hotels;
import com.example.backend.api.HotelsRepa;
import com.example.backend.api2.Detail;
import com.example.backend.api2.DetailRepa;
import com.example.backend.authentication.User;
import com.example.backend.authentication.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final HotelsRepa accommodationRepository;
    private final DetailRepa detailRepa;

    @Transactional(readOnly = true)
    public ReservationDto findReservationById(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다. ID: " + reservationId));

        Detail roomDetail = detailRepa.findById(Long.parseLong(reservation.getRoomcode()))
                .orElseThrow(() -> new IllegalArgumentException("객실 정보를 찾을 수 없습니다. RoomCode: " + reservation.getRoomcode()));

        return ReservationDto.from(reservation, roomDetail);
    }

    @Transactional
    public Reservation createReservation(ReservationRequestDto requestDto, String username) {

        User user = null;
        if (username != null) {
            user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        }

        Hotels hotel = accommodationRepository.findByContentid(requestDto.getContentid())
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
                .basePrice(requestDto.getBasePrice())
                .discountPrice(requestDto.getDiscountPrice())
                .reservName(requestDto.getGuestName())
                .reservPhone(requestDto.getPhone())
                .status("PENDING")
                .build();

        return reservationRepository.save(newReservation);
    }

    @Transactional(readOnly = true)
    public Map<LocalDate, Map<Long, Integer>> getRoomAvailability(AvailabilityRequestDto requestDto) {
        String contentId = requestDto.getContentId();
        LocalDate startDate = requestDto.getStartDate();
        LocalDate endDate = requestDto.getEndDate();
        

        List<Detail> roomDetails = detailRepa.findByContentid(contentId);
        Map<Long, Integer> totalRoomCounts = roomDetails.stream()
                .collect(Collectors.toMap(Detail::getId, Detail::getRoomcount));

        List<Reservation> reservations = reservationRepository.findPaidReservationsForDateRange(contentId, startDate, endDate);

        
        
        Map<LocalDate, Map<Long, Integer>> usage = new HashMap<>();
        for (Reservation r : reservations) {
            try {
                Long roomId = Long.parseLong(r.getRoomcode());
                for (LocalDate date = r.getCheckInDate(); date.isBefore(r.getCheckOutDate()); date = date.plusDays(1)) {
                    usage.computeIfAbsent(date, k -> new HashMap<>())
                            .merge(roomId, 1, Integer::sum);
                }
            } catch (NumberFormatException e) {
                // roomcode가 숫자로 변환될 수 없는 경우, 해당 예약을 건너뜁니다.
                System.err.println("Invalid room ID format, skipping reservation ID: " + r.getReservationId());
            }
        }

        Map<LocalDate, Map<Long, Integer>> availability = new HashMap<>();
        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            Map<Long, Integer> dailyAvailability = new HashMap<>();
            Map<Long, Integer> dailyUsage = usage.getOrDefault(date, new HashMap<>());

            for (Map.Entry<Long, Integer> roomEntry : totalRoomCounts.entrySet()) {
                Long roomId = roomEntry.getKey();
                int totalCount = roomEntry.getValue();
                int reservedCount = dailyUsage.getOrDefault(roomId, 0);

                dailyAvailability.put(roomId, totalCount - reservedCount);
            }
            availability.put(date, dailyAvailability);
        }

        return availability;
    }
}