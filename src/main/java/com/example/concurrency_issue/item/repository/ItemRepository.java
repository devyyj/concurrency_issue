package com.example.concurrency_issue.item.repository;

public interface ItemRepository {
    void decreaseItem(String name, Long count);

    void decreaseItemWithSynchronized(String name, Long count);

    void decreaseItemWithPessimisticLock(String name, Long count);

    void decreaseItemWithRedisson(String name, Long count);
}
