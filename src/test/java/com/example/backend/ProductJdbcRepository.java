package com.example.backend;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void decreaseStockForce(Long productId) {
    Integer stock = jdbcTemplate.queryForObject(
            "SELECT stock FROM product WHERE id = ?",
            Integer.class,
            productId
        );

        if (stock != null && stock > 0) {
            // 조건 만족할 때만 감소
            jdbcTemplate.update(
                "UPDATE product SET stock = stock - 1 WHERE id = ?",
                productId
            );
        }
    }
}