# 💳 토스페이먼츠 정기결제(빌링) 연동 테스트 프로젝트

이 프로젝트는 Spring Boot를 사용하여 **토스페이먼츠의 자동결제(Billing)** 기능을 구현하고 테스트하기 위한 프로젝트입니다. 고객 등록부터 카드 등록, 빌링키 발급, 그리고 실제 결제 승인까지의 전체 흐름을 포함하고 있습니다.

---

## 🚀 주요 기능

1.  **고객 관리**: 유저 등록 및 고유 `customerKey` 부여
2.  **카드 등록**: 토스페이먼츠 **SDK v2**를 활용한 보안 카드 등록 창 연동
3.  **빌링키 발급 및 저장**: 인증된 `authKey`를 사용하여 결제용 `billingKey` 발급 및 DB 저장
4.  **자동 결제 승인**: 저장된 빌링키를 사용하여 즉시 결제(**100원**) 승인 요청
5.  **이력 및 삭제**: 결제 성공 이력 조회 및 등록된 카드 정보(빌링키) 삭제 기능

---

## 🛠️ 기술 스택

*   **Backend**: Java 21, Spring Boot 3.5.9, Spring Data JPA, RestClient
*   **Database**: MySQL
*   **Frontend**: Thymeleaf, **Toss Payments SDK v2 (Standard)**
*   **Tool**: Gradle, Lombok

---

## ⚙️ 시작하기 (Quick Start)

### 1. 데이터베이스 설정
로컬 MySQL에 `pg_test` 데이터베이스를 생성합니다.
```sql
CREATE DATABASE pg_test;
```

### 2. API 키 설정
`src/main/resources/application.yaml` 파일에 본인의 토스페이먼츠 **테스트 API 키**를 입력합니다.
```yaml
toss:
  client-key: "test_ck_..."
  secret-key: "test_sk_..."
```

### 3. 애플리케이션 실행
```bash
./gradlew bootRun
```

---

## 📖 상세 문서

프로젝트의 상세한 내용과 테스트 방법은 아래 문서를 참고하세요.

1.  **[구현 명세서 (Implementation Spec)](./TOSS_BILLING_IMPLEMENTATION.md)**: DB 설계, API 명세, 아키텍처 및 로직 상세 설명 (SDK v2 코드 포함)
2.  **[테스트 가이드 (Test Guide)](./TEST_GUIDE.md)**: 포스트맨과 브라우저를 이용한 단계별 테스트 시나리오
3.  **[프로젝트 컨텍스트 (Gemini Context)](./GEMINI.md)**: 초기 프로젝트 분석 데이터

---

## 🔗 관련 링크
*   [토스페이먼츠 개발자 센터](https://docs.tosspayments.com/)
*   [빌링(자동결제) API 문서](https://docs.tosspayments.com/reference#%EC%9E%90%EB%8F%99%EA%B2%B0%EC%A0%9C)
*   [토스페이먼츠 SDK v2 가이드](https://docs.tosspayments.com/sdk/v2/js)

---
*Created by Gemini CLI Agent*