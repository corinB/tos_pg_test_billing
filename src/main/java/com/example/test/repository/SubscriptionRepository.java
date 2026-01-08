package com.example.test.repository;

import com.example.test.domain.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    // 특정 유저의 구독 정보 조회
    Optional<Subscription> findByMemberId(Long memberId);

    // 오늘 결제해야 할 구독 목록 조회 (상태가 ACTIVE이고, 다음 결제일이 오늘인 것)
    List<Subscription> findByNextBillingDateAndStatus(LocalDate date, Subscription.SubscriptionStatus status);
}
