package com.example.backend.main;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.backend.api.Hotels;

public interface TopRankRepository extends JpaRepository<Hotels, String> {

    @Query(
        "SELECT new com.example.backend.main.TopRankResponseDto(" +
        "  h.contentid, " +
        "  h.title, " +
        "  h.addr1, " +
        "  h.firstimage, " +
        "  COALESCE(AVG(r.rating), 0.0), " + 
        "  COUNT(DISTINCT r.reviewId), " +
        "  (SELECT MIN(d.roomoffseasonminfee1) FROM Detail d WHERE d.contentid = h.contentid)" +
        ") " +
        "FROM Hotels h " + 
        "LEFT JOIN Review r ON h.contentid = r.hotel.contentid " +
        "LEFT JOIN Reservation res ON h.contentid = res.hotel.contentid AND res.checkInDate >= :startDate " +
        "GROUP BY h.contentid, h.title, h.addr1 " +
        "ORDER BY COUNT(DISTINCT res.reservationId) DESC, COALESCE(AVG(r.rating), 0.0) DESC"
    )
    List<TopRankResponseDto> findTopRankings(
        @Param("startDate") LocalDate startDate,
        Pageable pageable
    );
}