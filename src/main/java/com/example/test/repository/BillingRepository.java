package com.example.test.repository;

import com.example.test.domain.Billing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BillingRepository extends JpaRepository<Billing, Long> {
    // 특정 유저의 빌링키 조회
    Optional<Billing> findByCustomerKey(String customerKey);
}
