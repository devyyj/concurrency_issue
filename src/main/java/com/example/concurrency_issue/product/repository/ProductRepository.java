package com.example.concurrency_issue.product.repository;

public interface ProductRepository {
    void decreaseProductWithOptimisticLock(String name, Long count);
}
