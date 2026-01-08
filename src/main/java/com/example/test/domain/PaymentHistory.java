package com.example.test.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderId;     // 우리 시스템의 주문 ID

    private String paymentKey;  // 토스 거래 고유 키 (환불 시 필요)
    
    @Column(nullable = false)
    private String customerKey; // 고객 식별 키

    private Long amount;        // 결제 금액
    private String orderName;   // 주문명 (예: 1월 정기구독)
    private String status;      // 결제 상태 (DONE, CANCELED 등)

    private LocalDateTime approvedAt; // 결제 승인 일시

    @CreatedDate
    private LocalDateTime createdAt;
}
