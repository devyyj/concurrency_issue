package com.example.concurrency_issue;

import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 단일 프로세스에서 2개 이상의 스레드를 만들기 위한 클래스.
 * 2개 이상의 스레드가 같은 함수를 호출하면서 동시성 이슈를 발생시킨다.
 * 모든 테스트는 실행하는 함수만 다르고 같은 로직을 반복한다.
 */
@Component
public class TestThread {

    private static final int THREAD_POOL_SIZE = 10; // 테스트용 스레드 풀 크기

    /**
     * 스레드를 생성하고 생성된 스레드는 함수를 호출한다.
     * @param callBack 호출할 함수
     * @param name callBack으로 넘길 데이터.
     * @param threadCount 생성할 스레드 수.
     * @throws InterruptedException
     */
    public void createThreadAndCallFunction(CallBack callBack, String name, int threadCount) throws InterruptedException {
        // 고정된 스레드 풀 생성
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    callBack.function(name, 1L); // 재고 감소 함수 호출
                } finally {
                    latch.countDown(); // 스레드 작업 완료 후 카운트 감소
                }
            });
        }

        latch.await(); // 모든 스레드 작업 완료 대기
        executorService.shutdown();
    }

    public interface CallBack {
        void function(String name, Long count);
    }
}

