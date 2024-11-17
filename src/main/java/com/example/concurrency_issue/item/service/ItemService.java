package com.example.concurrency_issue.item.service;

import com.example.concurrency_issue.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    private final RedissonClient redissonClient;

    /**
     * 아무런 조치를 하지 않은 재고 감소.
     * 동시성 문제가 발생할 수 있다.
     * @param name
     * @param count
     */
    public void buyItem(String name, Long count) {
        itemRepository.decreaseItem(name, count);
    }

    /**
     * synchronized를 사용한 동시성 제어. 단일 프로세스에서만 동작한다.
     * @param name
     * @param count
     */
    public void buyItemWithSynchronized(String name, Long count) {
        itemRepository.decreaseItemWithSynchronized(name, count);
    }

    /**
     * 비관적 락을 사용한 재고 감소.
     * @param name
     * @param count
     */
    public void buyItemWithPessimisticLock(String name, Long count) {
        itemRepository.decreaseItemWithPessimisticLock(name, count);
    }

    /**
     * Redisson을 사용한 재고 감소.
     * redis 인스턴스가 실행중이지 않으면 동작하지 않는다.
     * @param name
     * @param count
     * @throws InterruptedException
     */
    public void buyItemWithRedisson(String name, Long count) throws InterruptedException {
        RLock lock = redissonClient.getLock(name);
        // 락을 획득하기 위하여 최대 100초까지 대기하고
        // 락을 획득한 후에 10초 뒤에 자동으로 해제된다.
        boolean b = lock.tryLock(100, 10, TimeUnit.SECONDS);
        if (b) {
            try {
                itemRepository.decreaseItemWithRedisson(name, count);
            } finally {
                lock.unlock();
            }
        } else {
            throw new RuntimeException("락을 얻지 못해서 재고감소 로직 호출 실패");
        }
    }
}

