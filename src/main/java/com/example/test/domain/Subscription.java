package com.example.test.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member; // 구독자

    @Column(nullable = false)
    private String planName; // 요금제 이름 (예: BASIC, PREMIUM)

    @Column(nullable = false)
    private Long price;      // 결제 금액

    @Column(nullable = false)
    private LocalDate nextBillingDate; // **다음 결제일** (스케줄러가 이 날짜를 조회)

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status; // ACTIVE(구독중), CANCELED(해지), PAUSED(일시정지)

    @CreatedDate
    private LocalDateTime startedAt;   // 구독 시작일

    @LastModifiedDate
    private LocalDateTime updatedAt;   // 상태 변경일 (해지일 등)

    public enum SubscriptionStatus {
        ACTIVE, CANCELED, PAUSED
    }
}
