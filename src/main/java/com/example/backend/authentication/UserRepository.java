package com.example.backend.authentication;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String>{
    Optional<User> findByUsername(String username);
}
