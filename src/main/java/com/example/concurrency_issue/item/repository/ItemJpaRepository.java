package com.example.concurrency_issue.item.repository;

import com.example.concurrency_issue.item.entity.ItemEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface ItemJpaRepository extends JpaRepository<ItemEntity, Long> {

    ItemEntity findByName(String name);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT i FROM ItemEntity i WHERE i.name = :name")
    ItemEntity findByNameWithPessimisticLock(String name);
}
