package com.example.concurrency_issue.product.service;

import com.example.concurrency_issue.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * 낙관적 락을 사용한 재고 감소.
     * @param name
     * @param count
     * @throws InterruptedException
     */
    public void buyItemWithOptimisticLock(String name, Long count) throws InterruptedException {
        while (true) {
            try {
                productRepository.decreaseProductWithOptimisticLock(name, count);
                break;
            } catch (ObjectOptimisticLockingFailureException e) {
                // 낙관적 락 예외가 발생하면 50밀리 세컨즈 대기한 뒤 다시 시도
                Thread.sleep(50);
            }
        }
    }

}

