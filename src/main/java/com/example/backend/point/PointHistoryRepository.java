package com.example.backend.point;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    List<PointHistory> findByUser_UsernameOrderByTransactionDateDesc(String username);
}