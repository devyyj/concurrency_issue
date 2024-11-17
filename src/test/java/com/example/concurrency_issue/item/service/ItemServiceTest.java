package com.example.concurrency_issue.item.service;

import com.example.concurrency_issue.item.entity.ItemEntity;
import com.example.concurrency_issue.item.repository.ItemJpaRepository;
import com.example.concurrency_issue.TestThread;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemJpaRepository itemJpaRepository;

    @Autowired
    private TestThread testThread;

    private final String name = "당근"; // item 이름
    private final int count = 100; // item 수량, 스레드 수

    /**
     * 데이터 생성
     */
    @BeforeEach
    void setUp() {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setName(name);
        itemEntity.setCount((long) count);
        itemJpaRepository.save(itemEntity);
    }

    /**
     * 데이터 초기화
     */
    @AfterEach
    void tearDown() {
        itemJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("동시성 문제를 일으키는 재고 감소 테스트")
    void buyItem() throws InterruptedException {
        testThread.createThreadAndCallFunction((n, c) -> itemService.buyItem(n, c), name, count);
        ItemEntity byName = itemJpaRepository.findByName(name);
        long count = byName.getCount();
        assertNotEquals(0L, count); // 수량이 0이 아님
    }

    @Test
    @DisplayName("synchronized를 사용한 재고 감소 테스트")
    void buyItemWithSynchronized() throws InterruptedException {
        testThread.createThreadAndCallFunction((n, c) -> itemService.buyItemWithSynchronized(n, c), name, count);
        ItemEntity byName = itemJpaRepository.findByName(name);
        long count = byName.getCount();
        assertEquals(0L, count);
    }

    @Test
    @DisplayName("비관적 락을 적용한 재고 감소 테스트")
    void buyItemWithPessimisticLock() throws InterruptedException {
        testThread.createThreadAndCallFunction((n, c) -> itemService.buyItemWithPessimisticLock(n, c), name, count);
        ItemEntity byName = itemJpaRepository.findByName(name);
        long count = byName.getCount();
        assertEquals(0L, count);
    }

    @Test
    @DisplayName("Redisson 분산락을 사용한 재고 감소 테스트")
        // application.yml 파일의 redis 설정에 따라 redis 인스턴스가 실행중이어야 함
    void buyItemWithRedisson() throws InterruptedException {
        testThread.createThreadAndCallFunction(
                (n, c) -> {
                    try {
                        itemService.buyItemWithRedisson(n, c);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                , name
                , count);
        ItemEntity byName = itemJpaRepository.findByName(name);
        Long byNameCount = byName.getCount();
        assertEquals(0L, byNameCount);
    }
}

