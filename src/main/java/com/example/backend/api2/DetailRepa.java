package com.example.backend.api2;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DetailRepa extends JpaRepository<Detail, Long> {
    Optional<Detail> findByRoomcode(String roomcode);
}
