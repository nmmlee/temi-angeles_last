# OpenAI API 키 설정 가이드

## 1. API 키 발급 받기

1. [OpenAI Platform](https://platform.openai.com/)에 접속
2. 로그인 또는 회원가입
3. 우측 상단 메뉴에서 `API keys` 선택
4. `Create new secret key` 클릭
5. 생성된 API 키 복사 (한 번만 표시되므로 안전하게 보관)

## 2. API 키 설정 방법

### 방법 1: 앱 내에서 직접 설정 (권장)

1. 앱 실행
2. 챗봇 화면 진입
3. API 키 입력 다이얼로그에서 키 입력
4. 저장하면 `SharedPreferences`에 암호화되어 저장됨

### 방법 2: 코드에 직접 입력 (개발/테스트용)

**ChatActivity.java** 파일에서 다음 부분을 수정:

```java
private void setupOpenAI() {
    openAIService = new OpenAIService();
    apiKeyManager = new ApiKeyManager(this);
    
    // ⬇️ 여기에 API 키 직접 입력 (개발용)
    String apiKey = "sk-proj-YOUR_API_KEY_HERE";
    apiKeyManager.saveApiKey(apiKey);
    // ⬆️
    
    if (apiKeyManager.hasApiKey()) {
        openAIService.setApiKey(apiKeyManager.getApiKey());
    } else {
        showApiKeyDialog();
    }
}
```

**주의사항:**
- 실제 배포 시에는 절대로 코드에 API 키를 포함하지 마세요!
- Git에 커밋하기 전에 API 키를 제거하세요!

## 3. API 키 형식

```
sk-proj-XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
```

- `sk-proj-`로 시작
- 총 56자 이상의 영문/숫자 조합

## 4. API 사용량 및 비용

- **모델**: gpt-4o-mini (비용 효율적)
- **예상 비용**: 
  - Input: $0.150 / 1M tokens
  - Output: $0.600 / 1M tokens
- **예상 사용량**: 대화 1회당 약 500-1000 토큰

자세한 가격 정보: [OpenAI Pricing](https://openai.com/api/pricing/)

## 5. 보안 권장사항

1. API 키는 절대 공개 저장소에 커밋하지 않기
2. `.gitignore`에 API 키 관련 파일 추가
3. 정기적으로 API 키 교체
4. 사용량 제한 설정 (OpenAI Dashboard)
5. 프로덕션에서는 백엔드 서버를 통해 API 호출

## 6. 문제 해결

### API 키 오류
```
Error: Incorrect API key provided
```
→ API 키가 올바른지 확인하세요.

### 네트워크 오류
```
Network error: Failed to connect
```
→ 인터넷 연결을 확인하세요.

### 할당량 초과
```
Error: You exceeded your current quota
```
→ OpenAI 대시보드에서 결제 방법을 추가하거나 사용량을 확인하세요.

## 7. 추가 설정

### 시스템 프롬프트 수정

**OpenAIService.java**의 `SYSTEM_PROMPT` 변수를 수정하여 챗봇의 동작을 커스터마이징할 수 있습니다:

```java
private static final String SYSTEM_PROMPT = 
    "당신은 행사장 안내 도우미입니다. ...[행사장 정보]...";
```

### 모델 변경

**OpenAIService.java**의 `buildRequestBody()` 메서드에서 모델을 변경할 수 있습니다:

```java
requestBody.addProperty("model", "gpt-4o-mini"); // 또는 "gpt-4o", "gpt-3.5-turbo"
```

## 8. 개발자 정보

- OpenAI API 문서: https://platform.openai.com/docs
- API 참조: https://platform.openai.com/docs/api-reference
- 사용량 모니터링: https://platform.openai.com/usage

