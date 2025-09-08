package com.example.backend;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ProductJdbcRepository productJdbcRepository;

    @Transactional
    public void decreaseStock(Long productId) {
        // 비관적 락으로 조회
        Product product = productRepository.findByIdWithLock(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
                
        if(product.getStock() > 0)
            product.decreaseStock();
    }

    @Transactional
    public void commomDecreaseStock(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        // 비관적 락 없이 조회
        if(product.getStock() > 0)
            product.decreaseStock();
    }

    @Transactional
    public void jdbcDecreaseStock(Long productId) {
        productJdbcRepository.decreaseStockForce(productId);
    }
}