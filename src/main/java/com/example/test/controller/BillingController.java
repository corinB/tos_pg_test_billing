package com.example.test.controller;

import com.example.test.config.TossPaymentConfig;
import com.example.test.dto.BillingKeyResponse;
import com.example.test.repository.MemberRepository;
import com.example.test.service.TossPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BillingController {

    private final TossPaymentService tossPaymentService;
    private final TossPaymentConfig tossPaymentConfig;
    private final MemberRepository memberRepository;

    // 1. 카드 등록 화면 (index.html)
    @GetMapping("/")
    public String index(@RequestParam(required = false) String customerKey, Model model) {
        model.addAttribute("clientKey", tossPaymentConfig.getClientKey());
        
        if (customerKey != null && !customerKey.isEmpty()) {
            model.addAttribute("customerKey", customerKey);
            
            // DB에서 유저 정보를 찾아 이메일과 이름을 추가 (있으면 결제창에 자동 반영)
            memberRepository.findByCustomerKey(customerKey).ifPresent(member -> {
                model.addAttribute("customerEmail", member.getEmail());
                model.addAttribute("customerName", member.getName());
            });
        } else {
            model.addAttribute("customerKey", "test_customer_key_" + System.currentTimeMillis());
        }
        
        return "index";
    }

    // 2. 카드 등록 성공 & 빌링객체 생성 (success.html)
    @GetMapping("/success")
    public String success(@RequestParam String authKey,
                          @RequestParam String customerKey,
                          Model model) {
        try {
            // 빌링키 발급 및 DB 저장
            BillingKeyResponse response = tossPaymentService.issueBillingKey(authKey, customerKey);
            
            log.info("빌링키 발급 및 저장 성공: {}", response);
            model.addAttribute("billingResponse", response);
            
            return "success";
        } catch (Exception e) {
            log.error("빌링키 발급 실패", e);
            model.addAttribute("message", "빌링키 발급 실패: " + e.getMessage());
            return "fail";
        }
    }

    @GetMapping("/fail")
    public String fail(@RequestParam(required = false) String message,
                       @RequestParam(required = false) String code,
                       Model model) {
        model.addAttribute("message", message);
        model.addAttribute("code", code);
        return "fail";
    }
}