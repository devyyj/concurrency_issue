package com.example.concurrency_issue.product.service;

import com.example.concurrency_issue.product.entity.ProductEntity;
import com.example.concurrency_issue.product.repository.ProductJpaRepository;
import com.example.concurrency_issue.TestThread;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private TestThread testThread;

    private final String name = "당근"; // item 이름
    private final int count = 100; // item 수량, 스레드 수

    /**
     * 데이터 생성
     */
    @BeforeEach
    void setUp() {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setName(name);
        productEntity.setCount((long) count);
        productJpaRepository.save(productEntity);
    }

    /**
     * 데이터 초기화
     */
    @AfterEach
    void tearDown() {
        productJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("낙관적 락을 사용한 재고 감소 테스트")
    void buyItemWithOptimisticLock() throws InterruptedException {
        testThread.createThreadAndCallFunction(
                (n, c) -> {
                    try {
                        productService.buyItemWithOptimisticLock(n, c);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                , name
                , count);
        ProductEntity byName = productJpaRepository.findByName(name);
        Long byNameCount = byName.getCount();
        assertEquals(0L, byNameCount);
    }
}