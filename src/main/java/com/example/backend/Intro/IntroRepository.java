package com.example.backend.Intro;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntroRepository extends JpaRepository<IntroEntity, Long> {
    // JpaRepository가 <엔티티, ID타입>을 받도록 수정
}