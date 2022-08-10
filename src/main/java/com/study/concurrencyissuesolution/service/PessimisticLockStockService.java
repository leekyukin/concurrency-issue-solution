package com.study.concurrencyissuesolution.service;

import com.study.concurrencyissuesolution.domain.Stock;
import com.study.concurrencyissuesolution.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PessimisticLockStockService {

    private final StockRepository stockRepository;

    @Transactional
    public void decrease(Long id, Long quantity) {
        Stock stock = stockRepository.findByIdWithPessimistLock(id);

        stock.decrease(quantity);

        stockRepository.saveAndFlush(stock);
    }
}
