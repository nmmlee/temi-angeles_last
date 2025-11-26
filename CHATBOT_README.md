# 🤖 TEMI 챗봇 기능 가이드

## ✅ 완료된 작업

### 1. 채팅 UI 구현
- ✅ 말풍선 형태의 메시지 표시
  - 사용자 메시지: 파란색 말풍선 (오른쪽)
  - 봇 메시지: 흰색 말풍선 (왼쪽)
- ✅ RecyclerView를 사용한 채팅 리스트
- ✅ 입력창 및 전송 버튼

### 2. OpenAI GPT 연동
- ✅ OpenAI Chat Completions API 연동
- ✅ gpt-4o-mini 모델 사용 (비용 효율적)
- ✅ 비동기 HTTP 통신 (OkHttp)
- ✅ JSON 파싱 (Gson)

### 3. RAG 구조 구현
- ✅ 행사장 정보를 시스템 프롬프트에 포함
  - 행사명, 일시, 장소
  - 주요 부스 정보 (A/B/C홀)
  - 주요 이벤트 일정
  - 편의시설 안내
  - 운영 시간
- ✅ 대화 컨텍스트 유지 (전체 대화 기록 전송)

### 4. API 키 관리
- ✅ SharedPreferences를 통한 API 키 저장
- ✅ API 키 입력 다이얼로그
- ✅ 안전한 키 관리 시스템

## 📁 생성된 파일

### Java 클래스
1. `ChatMessage.java` - 채팅 메시지 데이터 모델
2. `ChatAdapter.java` - RecyclerView Adapter
3. `OpenAIService.java` - OpenAI API 통신 서비스
4. `ApiKeyManager.java` - API 키 관리 클래스
5. `ChatActivity.java` - 업데이트 (실제 대화 로직 추가)

### 레이아웃 파일
1. `item_chat_user.xml` - 사용자 메시지 아이템
2. `item_chat_bot.xml` - 봇 메시지 아이템
3. `bg_chat_user.xml` - 사용자 말풍선 배경
4. `bg_chat_bot.xml` - 봇 말풍선 배경

### 문서 파일
1. `API_KEY_SETUP.md` - API 키 설정 가이드
2. `CHATBOT_README.md` - 이 파일

## 🚀 사용 방법

### 1. API 키 설정

#### 방법 A: 앱 실행 시 자동 입력 다이얼로그
1. 앱을 실행하고 캐릭터를 클릭하여 챗봇 화면 진입
2. API 키 입력 다이얼로그가 표시됨
3. OpenAI API 키 입력 (`sk-proj-...`)
4. 확인 버튼 클릭

#### 방법 B: 코드에 직접 입력 (개발/테스트용)
```java
// ChatActivity.java의 setupOpenAI() 메서드에서:
private void setupOpenAI() {
    openAIService = new OpenAIService();
    apiKeyManager = new ApiKeyManager(this);
    
    // 여기에 API 키 직접 입력
    String apiKey = "sk-proj-YOUR_API_KEY_HERE";
    apiKeyManager.saveApiKey(apiKey);
    
    openAIService.setApiKey(apiKey);
}
```

⚠️ **주의**: 실제 배포 시에는 코드에 API 키를 포함하지 마세요!

### 2. 챗봇 사용
1. 메인 화면에서 오른쪽 하단 **테미 캐릭터** 클릭
2. 챗봇 화면 진입
3. 환영 메시지 확인
4. 하단 입력창에 질문 입력
5. 전송 버튼 클릭 또는 Enter 키
6. GPT 응답 대기 (로딩 메시지 표시)
7. 응답 확인

### 3. 질문 예시

```
✅ 행사 정보
- "이 행사는 언제 열리나요?"
- "행사장 위치가 어디인가요?"
- "운영 시간 알려주세요"

✅ 부스 정보
- "AI 체험할 수 있는 부스 어디있나요?"
- "테미 로봇 체험존은 어디에요?"
- "VR 게임은 어디서 할 수 있어요?"

✅ 이벤트 일정
- "오늘 진행되는 이벤트 알려주세요"
- "로봇 댄스 퍼포먼스는 언제 시작하나요?"
- "개막식은 언제인가요?"

✅ 편의시설
- "화장실 어디에 있나요?"
- "식사할 수 있는 곳 알려주세요"
- "주차는 어디에 하나요?"
```

## 🔧 기술 스택

- **UI**: RecyclerView, ConstraintLayout
- **네트워킹**: OkHttp 4.9.3
- **JSON 파싱**: Gson 2.10.1
- **AI 모델**: OpenAI GPT-4o-mini
- **아키텍처**: RAG (Retrieval-Augmented Generation)

## 💰 비용 정보

### OpenAI API 가격 (gpt-4o-mini)
- **Input**: $0.150 / 1M tokens
- **Output**: $0.600 / 1M tokens

### 예상 비용
- 대화 1회: 약 500-1000 토큰
- 100회 대화: 약 $0.05 - $0.10
- 1000회 대화: 약 $0.50 - $1.00

## 🔒 보안 권장사항

1. ✅ API 키는 절대 Git에 커밋하지 않기
2. ✅ `.gitignore`에 API 키 관련 파일 추가
3. ⚠️ 실제 배포 시 백엔드 서버 사용 권장
4. ✅ OpenAI Dashboard에서 사용량 제한 설정
5. ✅ 정기적으로 API 키 교체

## 📊 RAG 시스템 프롬프트

챗봇은 다음 정보를 미리 학습하고 있습니다:

```
=== 포함된 정보 ===
✅ 행사명, 일시, 장소
✅ A홀: AI 및 로보틱스 존 (10개 부스)
✅ B홀: 스마트 홈 & IoT 존 (12개 부스)
✅ C홀: 메타버스 & VR/AR 존 (15개 부스)
✅ 주요 이벤트 일정 (6개 이벤트)
✅ 편의시설 (푸드코트, 휴게실, 화장실, 주차장)
✅ 운영 시간
```

시스템 프롬프트는 `OpenAIService.java`의 `SYSTEM_PROMPT` 상수에서 수정 가능합니다.

## 🛠️ 커스터마이징

### 1. 모델 변경
```java
// OpenAIService.java - buildRequestBody()
requestBody.addProperty("model", "gpt-4o"); // 더 강력한 모델
requestBody.addProperty("model", "gpt-3.5-turbo"); // 더 저렴한 모델
```

### 2. 응답 길이 조절
```java
// OpenAIService.java - buildRequestBody()
requestBody.addProperty("max_tokens", 1000); // 더 긴 응답
```

### 3. 온도(창의성) 조절
```java
// OpenAIService.java - buildRequestBody()
requestBody.addProperty("temperature", 0.7); // 0.0~2.0
// 0.0: 일관적이고 정확한 답변
// 1.0: 균형잡힌 창의성
// 2.0: 매우 창의적이고 다양한 답변
```

### 4. 행사장 정보 업데이트
```java
// OpenAIService.java - SYSTEM_PROMPT 수정
private static final String SYSTEM_PROMPT = 
    "당신은 행사장 안내 도우미입니다. ...[수정된 정보]...";
```

## 🐛 문제 해결

### 1. API 키 오류
```
Error: Incorrect API key provided
```
**해결**: API 키가 올바른지 확인. `sk-proj-`로 시작해야 함.

### 2. 네트워크 오류
```
Network error: Failed to connect
```
**해결**: 
- 인터넷 연결 확인
- AndroidManifest.xml에 INTERNET 권한 확인 (이미 추가됨)

### 3. 응답 없음
```
답변을 생성하는 중...
```
**해결**:
- API 키 할당량 확인
- OpenAI 서버 상태 확인
- 로그 확인: `adb logcat | grep OpenAIService`

### 4. 한글 깨짐
**해결**: 이미 UTF-8로 설정되어 있으며 문제 없어야 함.

## 📱 테스트 방법

1. **빌드 및 실행**
```bash
./gradlew assembleDebug
./gradlew installDebug
```

2. **로그 확인**
```bash
adb logcat | grep -E "ChatActivity|OpenAIService"
```

3. **디버그 포인트**
- `ChatActivity.sendUserMessage()` - 메시지 전송
- `OpenAIService.sendMessage()` - API 호출
- `OpenAIService.onResponse()` - 응답 수신

## 🎯 다음 단계 (선택사항)

1. **음성 입력 추가**
   - 음성 인식으로 메시지 입력
   - TTS로 응답 읽어주기

2. **대화 기록 저장**
   - SQLite에 대화 내역 저장
   - 이전 대화 불러오기

3. **이미지 공유**
   - 부스 사진, 지도 등 이미지 전송
   - GPT-4 Vision API 활용

4. **다국어 지원**
   - 영어, 중국어 등 지원
   - 언어별 시스템 프롬프트

## 📞 지원

- OpenAI API 문서: https://platform.openai.com/docs
- OpenAI 커뮤니티: https://community.openai.com
- API 사용량: https://platform.openai.com/usage

---

**구현 완료 날짜**: 2025-11-26  
**개발자**: Cursor AI + User  
**버전**: 1.0.0

