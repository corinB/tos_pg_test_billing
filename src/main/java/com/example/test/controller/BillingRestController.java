package com.example.test.controller;

import com.example.test.domain.Member;
import com.example.test.domain.PaymentHistory;
import com.example.test.dto.PaymentResponse;
import com.example.test.dto.SignupRequest;
import com.example.test.repository.MemberRepository;
import com.example.test.service.TossPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
public class BillingRestController {

    private final TossPaymentService tossPaymentService;
    private final MemberRepository memberRepository;

    // 1. 고객 등록 (회원가입)
    @PostMapping("/signup")
    public Member signup(@RequestBody SignupRequest request) {
        Member member = Member.builder()
                .email(request.getEmail())
                .name(request.getName())
                .customerKey("CUSTOMER_" + UUID.randomUUID().toString().substring(0, 8))
                .build();
        return memberRepository.save(member);
    }

    // 2. 10원 결제 (빌링객체 사용)
    @PostMapping("/pay/member/{memberId}")
    public PaymentResponse payByMemberId(@PathVariable Long memberId) {
        return tossPaymentService.pay10WonByMemberId(memberId);
    }

    // 3. 결제 이력 확인
    @GetMapping("/history/member/{memberId}")
    public List<PaymentHistory> getHistory(@PathVariable Long memberId) {
        return tossPaymentService.getHistory(memberId);
    }

    // 4. 빌링키 삭제
    @DeleteMapping("/member/{memberId}")
    public String deleteBillingKey(@PathVariable Long memberId) {
        tossPaymentService.deleteBillingKey(memberId);
        return "빌링키가 삭제되었습니다. Member ID: " + memberId;
    }
}