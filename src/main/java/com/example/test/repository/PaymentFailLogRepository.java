package com.example.test.repository;

import com.example.test.domain.PaymentFailLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentFailLogRepository extends JpaRepository<PaymentFailLog, Long> {
}
