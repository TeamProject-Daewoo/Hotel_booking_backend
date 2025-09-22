package com.example.backend.api2;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.room_price_override.RoomPriceOverride;

@Repository
public interface RoomPriceOverrideRepository extends JpaRepository<RoomPriceOverride, Long>{

    @Query("SELECT p FROM RoomPriceOverride p " +
            "WHERE p.room.id = :roomId " +
            "AND p.startDate <= :checkOutDate " + // 특별가 시작일이 검색 체크아웃 날짜보다 이전이고
            "AND p.endDate >= :checkInDate " +   // 특별가 종료일이 검색 체크인 날짜보다 이후인 경우 (겹치는 조건)
            "ORDER BY p.id DESC " +             // 여러 개일 경우 최신 설정을 적용
            "LIMIT 1")
     Optional<RoomPriceOverride> findApplicableOverride(@Param("roomId") Long roomId, @Param("checkInDate") LocalDate checkInDate, @Param("checkOutDate") LocalDate checkOutDate
     );
    
 // RoomPriceOverrideRepository.java
    @Query("SELECT p FROM RoomPriceOverride p WHERE p.room.id = :roomId AND p.startDate < :checkOut AND p.endDate > :checkIn")
    List<RoomPriceOverride> findAllOverlaps(@Param("roomId") Long roomId, @Param("checkIn") LocalDate checkIn, @Param("checkOut") LocalDate checkOut);

}
