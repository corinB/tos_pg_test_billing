package com.example.test.service;

import com.example.test.config.TossPaymentConfig;
import com.example.test.domain.Billing;
import com.example.test.domain.Member;
import com.example.test.domain.PaymentHistory;
import com.example.test.dto.BillingKeyResponse;
import com.example.test.dto.PaymentResponse;
import com.example.test.repository.BillingRepository;
import com.example.test.repository.MemberRepository;
import com.example.test.repository.PaymentHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class TossPaymentService {

    private final TossPaymentConfig tossPaymentConfig;
    private final RestClient restClient;
    private final BillingRepository billingRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final MemberRepository memberRepository;

    public TossPaymentService(TossPaymentConfig tossPaymentConfig, 
                              BillingRepository billingRepository, 
                              PaymentHistoryRepository paymentHistoryRepository,
                              MemberRepository memberRepository) {
        this.tossPaymentConfig = tossPaymentConfig;
        this.billingRepository = billingRepository;
        this.paymentHistoryRepository = paymentHistoryRepository;
        this.memberRepository = memberRepository;
        
        String encodedSecretKey = Base64.getEncoder()
                .encodeToString((tossPaymentConfig.getSecretKey() + ":").getBytes(StandardCharsets.UTF_8));

        this.restClient = RestClient.builder()
                .baseUrl(tossPaymentConfig.getUrl())
                .defaultHeader("Authorization", "Basic " + encodedSecretKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    // 1. [카드 등록 & 빌링객체 생성] authKey로 빌링키 발급 및 DB 저장
    public BillingKeyResponse issueBillingKey(String authKey, String customerKey) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("authKey", authKey);
        requestBody.put("customerKey", customerKey);

        BillingKeyResponse response = restClient.post()
                .uri("/billing/authorizations/issue")
                .body(requestBody)
                .retrieve()
                .body(BillingKeyResponse.class);

        if (response != null) {
            Billing billing = billingRepository.findByCustomerKey(customerKey)
                    .orElse(new Billing());
            
            billing.setCustomerKey(response.getCustomerKey());
            billing.setBillingKey(response.getBillingKey());
            billing.setCardCompany(response.getCard().getIssuerCode());
            billing.setCardNumber(response.getCard().getNumber());
            
            billingRepository.save(billing);
        }

        return response;
    }

    // 2. [10원 결제] Member ID로 결제 -> [수정] 100원 결제 (최소 금액 제한)
    public PaymentResponse pay10WonByMemberId(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Billing billing = billingRepository.findByCustomerKey(member.getCustomerKey())
                .orElseThrow(() -> new IllegalArgumentException("등록된 카드가 없습니다."));

        // 토스 결제 요청
        String orderId = UUID.randomUUID().toString();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("customerKey", member.getCustomerKey());
        requestBody.put("amount", 100L); // 최소 결제 금액 100원으로 수정
        requestBody.put("orderId", orderId);
        requestBody.put("orderName", "100원 테스트 결제");

        PaymentResponse response = restClient.post()
                .uri("/billing/" + billing.getBillingKey())
                .body(requestBody)
                .retrieve()
                .body(PaymentResponse.class);

        // 결제 내역 저장
        if (response != null) {
            PaymentHistory history = PaymentHistory.builder()
                    .orderId(response.getOrderId())
                    .paymentKey(response.getPaymentKey())
                    .customerKey(member.getCustomerKey())
                    .amount(response.getTotalAmount())
                    .orderName(response.getOrderName())
                    .status(response.getStatus())
                    .approvedAt(OffsetDateTime.parse(response.getApprovedAt()).toLocalDateTime())
                    .build();

            paymentHistoryRepository.save(history);
        }

        return response;
    }

    // 3. [이력 확인] 고객의 결제 내역 조회
    public List<PaymentHistory> getHistory(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        
        return paymentHistoryRepository.findAll().stream()
                .filter(h -> h.getCustomerKey().equals(member.getCustomerKey()))
                .toList();
    }

    // 4. [빌링키 삭제] 카드 정보 삭제
    public void deleteBillingKey(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Billing billing = billingRepository.findByCustomerKey(member.getCustomerKey())
                .orElseThrow(() -> new IllegalArgumentException("삭제할 빌링 정보가 없습니다."));

        billingRepository.delete(billing);
    }
}