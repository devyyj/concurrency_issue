package com.example.concurrency_issue.product.repository;

import com.example.concurrency_issue.product.entity.ProductEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {

    ProductEntity findByName(String name);

    @Lock(LockModeType.OPTIMISTIC)
    @Query(value = "SELECT i FROM ProductEntity i WHERE i.name = :name")
    ProductEntity findByNameWithOptimisticLock(String name);
}
