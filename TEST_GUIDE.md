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
*   **💡 Tip (415 에러 방지)**: Postman의 Body 탭에서 **raw**를 선택하고 형식을 **JSON**으로 꼭 변경하세요.
*   **✅ 확인사항**: 응답 JSON에서 `customerKey` 값을 복사해 두세요. (예: `"CUSTOMER_1234abcd..."`)
*   **✅ 확인사항**: 응답 JSON에서 `id` (Member ID) 값을 기억하세요. (예: `1`)

---

### 📍 Step 2: 카드 등록 (Browser)
복사한 `customerKey`를 사용하여 웹 브라우저에서 카드를 등록합니다.

1.  브라우저 주소창에 아래 형식으로 접속합니다.
    *   `http://localhost:8080/?customerKey={아까_복사한_키_붙여넣기}`
2.  **[카드 등록하기 (자동결제)]** 버튼을 클릭합니다.
    *   *시스템이 자동으로 DB에서 해당 유저의 이름과 이메일을 찾아 결제창에 채워줍니다.*
3.  토스페이먼츠 테스트 결제창이 뜨면 아무 카드나 선택하고 진행합니다.
4.  **🎉 카드 등록 및 빌링 객체 생성 완료!** 화면이 나오면 성공입니다.

---

### 📍 Step 3: 10원 결제 요청 (Postman)
등록된 카드로 10원을 즉시 결제합니다. 유저의 ID(PK)만 알면 됩니다.

*   **Method**: `POST`
*   **URL**: `http://localhost:8080/api/v1/billing/pay/member/{memberId}`
    *   예: `http://localhost:8080/api/v1/billing/pay/member/1`
*   **✅ 확인사항**:
    *   응답 코드 `200 OK`
    *   JSON 응답의 `status`가 `"DONE"`인지 확인

---

### 📍 Step 4: 결제 이력 확인 (Postman)
해당 유저의 모든 결제 내역을 조회합니다.

*   **Method**: `GET`
*   **URL**: `http://localhost:8080/api/v1/billing/history/member/{memberId}`
    *   예: `http://localhost:8080/api/v1/billing/history/member/1`
*   **✅ 확인사항**: 방금 결제한 10원 내역이 리스트에 포함되어 있어야 합니다.

---

### 📍 Step 5: 빌링키 삭제 (Postman)
등록된 카드 정보를 삭제합니다. (구독 해지 시 활용)

*   **Method**: `DELETE`
*   **URL**: `http://localhost:8080/api/v1/billing/member/{memberId}`
    *   예: `http://localhost:8080/api/v1/billing/member/1`
*   **✅ 확인사항**: "빌링키가 삭제되었습니다" 메시지 확인.

---

## 🐞 트러블슈팅

*   **Q. 415 Unsupported Media Type 에러가 나요.**
    *   A. Postman에서 Body 형식을 **JSON**으로 설정했는지 확인하세요. (Step 1 💡 Tip 참고)
*   **Q. "존재하지 않는 회원입니다" 오류가 나요.**
    *   A. Step 1에서 생성된 `id` 값이 URL 경로 변수에 정확히 들어갔는지 확인하세요.
*   **Q. "등록된 카드가 없습니다" 오류가 나요.**
    *   A. Step 2 접속 시 주소창에 `?customerKey=...`를 정확히 입력하여 카드를 등록했는지 확인하세요.
*   **Q. 401 Unauthorized 에러가 나요.**
    *   A. `application.yaml`에 본인의 **시크릿 키**가 올바르게 입력되었는지 확인하세요.