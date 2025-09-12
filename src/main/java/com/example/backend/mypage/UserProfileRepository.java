package com.example.backend.mypage;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.authentication.User;

public interface UserProfileRepository extends JpaRepository<User, String> {
}