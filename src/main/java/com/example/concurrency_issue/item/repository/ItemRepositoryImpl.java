package com.example.concurrency_issue.item.repository;

import com.example.concurrency_issue.item.entity.ItemEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final ItemJpaRepository jpaRepository;

    @Override
    @Transactional
    public void decreaseItem(String name, Long count) {
        ItemEntity byName = jpaRepository.findByName(name);
        byName.setCount(byName.getCount() - count);
        jpaRepository.save(byName);
    }

    @Override
//    @Transactional // 사용하지 않음. 사용할 경우 동시성 문제 발생.
    synchronized public void decreaseItemWithSynchronized(String name, Long count) {
        ItemEntity byName = jpaRepository.findByName(name);
        byName.setCount(byName.getCount() - count);
        jpaRepository.save(byName);
    }

    @Override
    @Transactional
    public void decreaseItemWithPessimisticLock(String name, Long count) {
        ItemEntity byName = jpaRepository.findByNameWithPessimisticLock(name);
        byName.setCount(byName.getCount() - count);
        jpaRepository.save(byName);
    }

    @Override
    @Transactional
    public void decreaseItemWithRedisson(String name, Long count) {
        ItemEntity byName = jpaRepository.findByName(name);
        byName.setCount(byName.getCount() - count);
        jpaRepository.save(byName);
    }
}
