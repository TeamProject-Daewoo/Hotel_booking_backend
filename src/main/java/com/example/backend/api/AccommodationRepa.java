package com.example.backend.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccommodationRepa extends JpaRepository<Accommodation, String> {
}