package com.example.backend.api2;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// @Repository 어노테이션 추가를 권장합니다.
@Repository
public interface DetailRepa extends JpaRepository<DetailEntity, Long> {
    // JpaRepository가 <엔티티, ID타입>을 받도록 수정
}