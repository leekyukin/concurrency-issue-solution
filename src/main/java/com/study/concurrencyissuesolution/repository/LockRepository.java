package com.study.concurrencyissuesolution.repository;

import com.study.concurrencyissuesolution.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LockRepository extends JpaRepository<Stock, Long> {

    @Query(value = "select get_lock(:key, 1000)", nativeQuery = true)
    void getLock(String key);

    @Query(value = "select relesse_lock(:key)", nativeQuery = true)
    void releaseLock(String key);
}
