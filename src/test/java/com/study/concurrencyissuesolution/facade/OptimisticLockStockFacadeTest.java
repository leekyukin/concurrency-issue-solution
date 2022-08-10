package com.study.concurrencyissuesolution.facade;

import com.study.concurrencyissuesolution.domain.Stock;
import com.study.concurrencyissuesolution.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class OptimisticLockStockFacadeTest {

    @Autowired
    private OptimisticLockStockFacade optimisticLockStockFacade;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    public void before() {
        Stock stock = new Stock(1L, 100L);

        stockRepository.saveAndFlush(stock);
    }

    @AfterEach
    public void after() {
        stockRepository.deleteAll();
    }

    @Test
    public void 동시에_100개_요청() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        // newFixedThreadPool: 고정된 스레드풀 생성

        CountDownLatch latch = new CountDownLatch(threadCount);
        // 이렇게 CountDownLatch 의 값을 설정한다.
        // 하나의 thread 가 실행이 종료되면 latch.countDown() method 를 실행한다.
        // 그럼 countDown() method 는 CountDownLatch 생성자에서 미리 설정해두었던
        // 매개변수값에서 -1을 실행한다.
        // 그렇게 설정해두었던 값이 0이 되어야 await() method 의 아래 코드를 실행할 수 있다.
        // 쉽게 말해서 모든 thread 가 일을 마친 후에 await() 아래의 코드를 실행할 수 있게 하는 것이다.

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    optimisticLockStockFacade.decrease(1L, 1L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                    // 생성자에서 설정한 매개변수 -1을 실행한다.
                }
            });
        }

        // 생성자에서 설정한 값이 0이되면 await() 아래의 코드를 실행할 수 있다.
        latch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();

        assertEquals(0L, stock.getQuantity());
    }
}