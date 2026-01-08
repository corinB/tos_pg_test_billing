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
public class Billing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerKey; // 고객 식별 키

    @Column(nullable = false)
    private String billingKey;  // 토스에서 발급받은 자동결제 키 (가장 중요)

    private String cardCompany; // 카드사 (예: 현대, 국민)
    private String cardNumber;  // 마스킹된 카드번호 (4242-****-****-4242)

    @CreatedDate
    private LocalDateTime createdAt;
}
