package com.example.backend.api2;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DetailRepa extends JpaRepository<Detail, Long> {
    Optional<Detail> findByRoomcode(String roomcode);

    List<Detail> findByContentid(String contentid);

    // contentid 기준으로 객실 이름(roomtitle) 중복 제거해서 조회
    @Query("""
        SELECT d FROM Detail d
        WHERE d.contentid = :contentid
          AND d.id IN (
            SELECT MIN(d2.id) FROM Detail d2
            WHERE d2.contentid = :contentid
            GROUP BY d2.roomtitle
          )
        ORDER BY d.roomtitle ASC
    """)
    List<Detail> findDistinctRoomsByContentid(@Param("contentid") String contentid);
}
