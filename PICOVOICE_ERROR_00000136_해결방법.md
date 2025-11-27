# Picovoice 에러 00000136 해결 방법

## 🚨 문제 상황
- 로컬(개발 PC/에뮬레이터)에서는 wake word가 잘 작동함
- 테미 봇 또는 실제 안드로이드 기기에서는 `PorcupineActivationLimitException` 발생
- 에러 코드: `00000136`

## 🔍 원인
Picovoice **무료 tier의 기기 활성화 제한** 때문입니다.
- 무료 플랜은 특정 기기에만 바인딩됩니다
- 제한된 수의 기기에서만 작동합니다
- 개발 환경에서 등록된 기기와 실제 배포 기기가 다르면 작동하지 않습니다

---

## ✅ 해결 방법

### 방법 1: Picovoice Console에서 기기 등록 (무료)

#### 1단계: 현재 기기의 정보 확인
앱을 테미 봇 또는 실제 기기에서 실행하고 Logcat에서 다음 정보를 확인하세요:

```
📱 Device Info:
   - Manufacturer: Samsung
   - Model: SM-G998B
   - Device: o1q
   - Android ID: a1b2c3d4e5f6g7h8
```

#### 2단계: Picovoice Console 접속
1. https://console.picovoice.ai/ 로그인
2. **Access Keys** 메뉴로 이동
3. 현재 사용 중인 Access Key 선택

#### 3단계: 기기 추가
1. **"Add Device"** 또는 **"Register Device"** 버튼 클릭
2. 위에서 확인한 Android ID를 입력
3. 저장

#### 4단계: 앱 재실행
- 앱을 완전히 종료한 후 다시 실행
- Wake word 기능을 다시 테스트

---

### 방법 2: 새 Access Key 발급 (무료)

현재 Access Key가 이미 다른 기기에 바인딩되어 있다면:

1. Picovoice Console에서 **새 Access Key 생성**
2. `local.properties` 파일 업데이트:
   ```properties
   PICOVOICE_ACCESS_KEY=새로_발급받은_키_입력
   ```
3. 프로젝트를 Clean & Rebuild
   ```bash
   ./gradlew clean
   ./gradlew build
   ```
4. 앱을 테미 봇과 실제 기기에 재설치

---

### 방법 3: Picovoice 유료 플랜 (상용화 시)

무료 tier의 제약을 완전히 제거하려면:

1. **Picovoice Developer Plan** 또는 **Enterprise Plan** 구독
2. 무제한 기기 활성화 가능
3. 상용 배포에 필요

**요금제 비교**: https://picovoice.ai/pricing/

---

## 🔧 임시 디버깅 팁

### 1. 에러 로그 자세히 확인
```bash
adb logcat | grep -E "WakeWordService|PICOVOICE|Porcupine"
```

### 2. Access Key 유효성 확인
- Picovoice Console에서 Access Key가 **Active** 상태인지 확인
- 만료되지 않았는지 확인
- 사용 제한(quota)이 남아있는지 확인

### 3. 여러 기기 테스트 시
무료 플랜은 보통 **3개까지 기기 등록 가능**합니다.
- 개발 PC (1개)
- 에뮬레이터 (1개)
- 실제 기기 또는 테미 봇 (1개)

더 많은 기기가 필요하면 유료 플랜이 필요합니다.

---

## 📌 현재 적용된 개선 사항

`WakeWordService.java`에 다음 디버깅 정보가 추가되었습니다:
```java
Log.i(TAG, "📱 Device Info:");
Log.i(TAG, "   - Manufacturer: " + android.os.Build.MANUFACTURER);
Log.i(TAG, "   - Model: " + android.os.Build.MODEL);
Log.i(TAG, "   - Device: " + android.os.Build.DEVICE);
Log.i(TAG, "   - Android ID: " + android.provider.Settings.Secure.getString(...));
```

앱을 실행하면 Logcat에서 기기 정보를 바로 확인할 수 있습니다.

---

## 🎯 권장 해결 순서

1. ✅ **현재 기기 정보 확인** (Logcat에서 Android ID 확인)
2. ✅ **Picovoice Console에서 기기 등록**
3. ✅ **앱 재실행 및 테스트**
4. ❌ 작동하지 않으면 → 새 Access Key 발급
5. ❌ 여전히 문제가 있으면 → Picovoice Support 문의

---

## 📞 추가 지원

- Picovoice 공식 문서: https://picovoice.ai/docs/
- GitHub Issues: https://github.com/Picovoice/porcupine/issues
- Support: support@picovoice.ai

---

**마지막 업데이트**: 2025-11-27

