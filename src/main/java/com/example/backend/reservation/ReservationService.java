package com.example.backend.reservation;

import com.example.backend.api.Hotels;
import com.example.backend.api.HotelsRepa;
import com.example.backend.api2.Detail;
import com.example.backend.api2.DetailRepa;
import com.example.backend.authentication.User;
import com.example.backend.authentication.UserRepository;
import com.example.backend.searchRestApi.BulkAvailabilityRequestDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.Collections;
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

    @Transactional(readOnly = true)
    public List<UserPointHistoryDto> getUserPointHistory(String username) {
        List<Reservation> reservations = reservationRepository.findReservationsWithDetailsByUserName(username);

        return reservations.stream()
                .filter(r -> r.getUsedPoints() != null && r.getUsedPoints() > 0)
                .map(r -> UserPointHistoryDto.builder()
                        .reservationId(r.getReservationId())
                        .date(r.getCheckInDate())
                        .hotelName(r.getHotel().getTitle())
                        .usedPoints(r.getUsedPoints())
                        .type("used")
                        .build())
                .collect(Collectors.toList());
    }

    // 가용 객실을 한번에 받아오는 함수(N+1 문제 해결) 
    public Map<String, Map<LocalDate, Map<Long, Integer>>> getRoomAvailabilityBulk(BulkAvailabilityRequestDto bulkDto) {
        List<String> contentIds = bulkDto.getContentIds();
        LocalDate startDate = bulkDto.getStartDate();
        LocalDate endDate = bulkDto.getEndDate();

        List<Detail> allDetails = detailRepa.findByContentidIn(contentIds);
        Map<String, Map<Long, Integer>> totalRoomCountsByContentId = allDetails.stream()
                .collect(Collectors.groupingBy(
                        Detail::getContentid,
                        Collectors.toMap(Detail::getId, Detail::getRoomcount)
                ));

        // 요청된 모든 contentId와 날짜 범위에 해당하는 예약 정보를 한 번에 조회
        List<Reservation> allReservations = reservationRepository.findPaidReservationsForDateRangeAndContentIds(contentIds, startDate, endDate);

        Map<String, Map<LocalDate, Map<Long, Integer>>> usageByContentId = new HashMap<>();
        for (Reservation r : allReservations) {
            try {
                Long roomId = Long.parseLong(r.getRoomcode());
                String contentId = r.getHotel().getContentid();

                for (LocalDate date = r.getCheckInDate(); date.isBefore(r.getCheckOutDate()); date = date.plusDays(1)) {
                    // 조회 범위에 해당하는 날짜만 계산
                    if (!date.isBefore(startDate) && date.isBefore(endDate)) {
                        usageByContentId
                                .computeIfAbsent(contentId, k -> new HashMap<>())
                                .computeIfAbsent(date, k -> new HashMap<>())
                                .merge(roomId, 1, Integer::sum);
                    }
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid room ID format, skipping reservation ID: " + r.getReservationId());
            }
        }

        //최종적으로 contentId별,로 모든 정보를 묶어서 반환
        Map<String, Map<LocalDate, Map<Long, Integer>>> availabilityByContentId = new HashMap<>();
        for (String contentId : contentIds) {
            Map<LocalDate, Map<Long, Integer>> singleContentAvailability = new HashMap<>();
            Map<Long, Integer> totalCounts = totalRoomCountsByContentId.getOrDefault(contentId, Collections.emptyMap());
            Map<LocalDate, Map<Long, Integer>> usage = usageByContentId.getOrDefault(contentId, Collections.emptyMap());

            for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
                Map<Long, Integer> dailyAvailability = new HashMap<>();
                Map<Long, Integer> dailyUsage = usage.getOrDefault(date, Collections.emptyMap());

                for (Map.Entry<Long, Integer> roomEntry : totalCounts.entrySet()) {
                    Long roomId = roomEntry.getKey();
                    int totalCount = roomEntry.getValue();
                    int reservedCount = dailyUsage.getOrDefault(roomId, 0);
                    dailyAvailability.put(roomId, totalCount - reservedCount);
                }
                singleContentAvailability.put(date, dailyAvailability);
            }
            availabilityByContentId.put(contentId, singleContentAvailability);
        }

        return availabilityByContentId;
    }
}