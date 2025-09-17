package com.example.backend.api;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface HotelsRepa extends JpaRepository<Hotels, Long> {
    Optional<Hotels> findByContentid(String contentid);
}
