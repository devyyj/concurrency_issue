package com.example.concurrency_issue.product.repository;

import com.example.concurrency_issue.product.entity.ProductEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
@Transactional
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository jpaRepository;

    @Override
    public void decreaseProductWithOptimisticLock(String name, Long count) {
        ProductEntity byName = jpaRepository.findByNameWithOptimisticLock(name);
        byName.setCount(byName.getCount() - count);
        jpaRepository.save(byName);
    }
}
