package com.example.backend;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MyBackendApplicationTests {

	@Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    @Test
    void pessimisticLockTest() throws InterruptedException {

		//남은 재고 변경 및 db초기화
        Product product = new Product(1, 50);
        productRepository.saveAndFlush(product);
        Long productId = product.getId();

		//스레드 생성
        int ThreadCount = 75;
		//16개씩 동시에 처리
        ExecutorService executorService = Executors.newFixedThreadPool(16);
		//스레드 대기 공간
        CountDownLatch latch = new CountDownLatch(ThreadCount);

        //동시에 여러 스레드로 요청
        for (int i = 0; i < ThreadCount; i++) {
            executorService.execute(() -> {
                try {
					/* 비관적 락으로 조회
					 * - 접근 완전 차단(안전성 보장)
					 * - 동시성 문제가 자주 발생해도 0미만으로 내려가지 않음
					 */
                    //productService.decreaseStock(productId);

					/* 비관적 락 없이 조회
					 * - 스레드 설정에 따라 가끔 음수로 내려가거나 rollback or 무시되는 경우가 있음(불안정)
					 * - 기본적으로 hibernate에서 이미 업데이트된 칼럼에 대해서 수정하려하면, 예외 발생 시킴
					 * - 이미 업데이트 되었음을 알리는 에러 로그(Caused by: org.hibernate.exception.LockAcquisitionException: could not execute statement [(conn=326) Record has changed since last read in table 'product'])
					 */
					productService.commomDecreaseStock(productId);
                } finally {
                    latch.countDown();
                }
            });
        }
		// 모든 스레드가 작업을 마칠 때까지 대기
        latch.await(); 
		System.out.println("최종 재고: "+productRepository.findById(productId).get().getStock());
    }

}
