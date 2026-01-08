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
public class PaymentFailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;     // 주문 ID
    private String customerKey; // 고객 키

    private String errorCode;   // 토스 에러 코드 (예: BALANCE_INSUFFICIENT)
    private String errorMessage; // 에러 메시지 (예: 잔액이 부족합니다)

    @CreatedDate
    private LocalDateTime failedAt; // 실패 시각
}
