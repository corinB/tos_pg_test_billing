# 🚀 토스페이먼츠 빌링(정기결제) 연동 테스트 가이드

이 문서는 Spring Boot 프로젝트에서 토스페이먼츠 자동결제 시스템을 테스트하는 절차를 설명합니다.

---

## ⚙️ 1. 사전 준비 (Configuration)

### 1-1. API 키 설정
`src/main/resources/application.yaml` 파일을 열어 본인의 토스 개발자 센터 키로 변경하세요.
> ⚠️ **주의:** 반드시 `test_ck_`로 시작하는 **테스트 키**를 사용해야 합니다.

```yaml
toss:
  client-key: "test_ck_본인키..."
  secret-key: "test_sk_본인키..."
```

### 1-2. 서버 실행
터미널에서 다음 명령어로 서버를 실행합니다.
```bash
./gradlew bootRun
```

---

## 🧪 2. 단계별 테스트 시나리오

테스트는 **Postman(API 호출)**과 **Web Browser(카드 등록)**를 번갈아 사용합니다.

### 📍 Step 1: 회원가입 (Postman)
먼저 유저를 생성하고 DB ID와 고객 키를 확보합니다.

*   **Method**: `POST`
*   **URL**: `http://localhost:8080/api/v1/billing/signup`
*   **Headers**: `Content-Type: application/json`
*   **Body (raw - JSON)**:
    ```json
    {
      "email": "user01@example.com",
      "name": "테스트유저"
    }
    ```
*   **💡 Tip**: Postman의 Body 탭에서 **raw**를 선택하고 형식을 **JSON**으로 설정하세요.
*   **✅ 확인사항**: 응답 JSON에서 `customerKey` 값을 복사해 두세요. (예: `"CUSTOMER_1234abcd..."`)
*   **✅ 확인사항**: 응답 JSON에서 `id` (Member ID) 값을 기억하세요. (예: `1`)

---

### 📍 Step 2: 카드 등록 (Browser)
복사한 `customerKey`를 사용하여 웹 브라우저에서 카드를 등록합니다.

1.  브라우저 주소창에 아래 형식으로 접속합니다.
    *   `http://localhost:8080/?customerKey={아까_복사한_키_붙여넣기}`
2.  **[카드 등록하기 (자동결제)]** 버튼을 클릭합니다.
    *   *시스템이 SDK v2를 통해 토스 결제창을 호출하며, 유저 정보를 자동으로 채웁니다.*
3.  토스페이먼츠 테스트 결제창이 뜨면 아무 카드나 선택하고 진행합니다.
4.  **🎉 빌링키 발급 성공!** 화면이 나오면 등록 완료입니다.

---

### 📍 Step 3: 100원 결제 요청 (Postman)
등록된 카드로 **100원**(최소 결제 금액)을 즉시 결제합니다.

*   **Method**: `POST`
*   **URL**: `http://localhost:8080/api/v1/billing/pay/member/{memberId}`
    *   예: `http://localhost:8080/api/v1/billing/pay/member/1`
*   **✅ 확인사항**:
    *   응답 코드 `200 OK`
    *   JSON 응답의 `status`가 `"DONE"`인지 확인
    *   `totalAmount`가 `100`인지 확인

---

### 📍 Step 4: 결제 이력 확인 (Postman)
해당 유저의 모든 결제 내역을 조회합니다.

*   **Method**: `GET`
*   **URL**: `http://localhost:8080/api/v1/billing/history/member/{memberId}`
    *   예: `http://localhost:8080/api/v1/billing/history/member/1`
*   **✅ 확인사항**: 방금 결제한 100원 내역이 리스트에 포함되어 있어야 합니다.

---

### 📍 Step 5: 빌링키 삭제 (Postman)
등록된 카드 정보를 삭제합니다. (구독 해지 시 활용)

*   **Method**: `DELETE`
*   **URL**: `http://localhost:8080/api/v1/billing/member/{memberId}`
    *   예: `http://localhost:8080/api/v1/billing/member/1`
*   **✅ 확인사항**: "빌링키가 삭제되었습니다" 메시지 확인.

---

## 🐞 트러블슈팅

*   **Q. 400 Bad Request (BELOW_MINIMUM_AMOUNT) 에러가 나요.**
    *   A. 토스페이먼츠 정책상 신용카드 최소 결제 금액은 **100원**입니다. Step 3가 100원을 요청하는지 확인하세요.
*   **Q. 415 Unsupported Media Type 에러가 나요.**
    *   A. Postman Body 설정을 **JSON**으로 변경하세요.
*   **Q. "등록된 카드가 없습니다" 오류가 나요.**
    *   A. 브라우저 URL에 `?customerKey=...`를 정확히 입력하여 카드를 등록했는지 확인하세요.
