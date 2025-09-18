package com.example.backend.api;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// JpaRepository<엔티티, 기본키_타입> -> 기본키 타입을 Long에서 String으로 변경
public interface HotelsRepa extends JpaRepository<Hotels, String> {
    Optional<Hotels> findByContentid(String contentid);
}