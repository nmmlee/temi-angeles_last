package com.example.temidummyapp;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * OpenAI Realtime API를 활용한 실시간 음성 대화 Activity
 * 사용자와 AI가 자연스럽게 음성으로 대화
 */
public class RealtimeVoiceChatActivity extends BaseActivity {
    private static final String TAG = "RealtimeVoiceChat";
    private static final int PERMISSION_REQUEST_RECORD_AUDIO = 2001;

    private AnimatedCircleView animatedCircle;
    private TextView instructionText;
    private ImageButton btnClose;
    private RecyclerView conversationList;
    private VoiceChatAdapter conversationAdapter;
    private OpenAIRealtimeService realtimeService;
    private boolean isConnected = false;
    private boolean isRecording = false;
    private boolean isAIResponding = false; // AI가 현재 응답 중인지 플래그
    
    // 현재 활성 메시지 추적
    private VoiceChatMessage currentUserMessage = null;
    private VoiceChatMessage currentAIMessage = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_voice_chat);

        // 전체화면 모드
        setupImmersiveMode();

        // UI 초기화
        initializeViews();

        // Realtime 서비스 초기화
        setupRealtimeService();

        // 권한 확인 및 연결 시작
        checkPermissionAndStart();
    }

    private void setupImmersiveMode() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
            getWindow().setNavigationBarColor(android.graphics.Color.TRANSPARENT);
        }

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    private void initializeViews() {
        animatedCircle = findViewById(R.id.animated_circle);
        instructionText = findViewById(R.id.instruction_text);
        btnClose = findViewById(R.id.btn_close);
        conversationList = findViewById(R.id.conversation_list);

        // 대화 리스트 설정
        androidx.recyclerview.widget.LinearLayoutManager layoutManager = 
            new androidx.recyclerview.widget.LinearLayoutManager(this);
        layoutManager.setStackFromEnd(false); // 위에서 아래로
        conversationList.setLayoutManager(layoutManager);
        
        conversationAdapter = new VoiceChatAdapter();
        conversationList.setAdapter(conversationAdapter);

        btnClose.setOnClickListener(v -> {
            // 버튼 비활성화 (중복 클릭 방지)
            btnClose.setEnabled(false);
            instructionText.setText("종료 중...");
            
            // 🔇 즉시 음소거 (메인 스레드에서 즉시 실행)
            realtimeService.muteAudioImmediately();
            
            // 순차적으로 종료
            stopVoiceChatGracefully();
        });
    }

    private void setupRealtimeService() {
        String apiKey = BuildConfig.OPENAI_API_KEY;
        realtimeService = new OpenAIRealtimeService(apiKey);

        realtimeService.setCallback(new OpenAIRealtimeService.RealtimeCallback() {
            @Override
            public void onConnected() {
                Log.d(TAG, "Realtime API 연결됨");
                isConnected = true;
                runOnUiThread(() -> {
                    instructionText.setText("연결되었습니다. 말씀해주세요!");
                    animatedCircle.setIdleMode();
                });
                startRecording();
            }

            @Override
            public void onAudioLevelChanged(float level) {
                // 음압 레벨에 따라 원 크기 및 애니메이션 속도 조정
                runOnUiThread(() -> {
                    animatedCircle.setAudioLevel(level);
                });
            }

            @Override
            public void onTranscriptReceived(String transcript) {
                Log.d(TAG, "📝 사용자 음성 인식: " + transcript);
                
                // AI가 응답 중이면 사용자 음성 인식 무시 (에코 방지)
                if (isAIResponding) {
                    Log.d(TAG, "⚠️ AI 응답 중 - 사용자 음성 무시 (에코)");
                    return;
                }
                
                runOnUiThread(() -> {
                    // 사용자 메시지 추가 또는 업데이트
                    if (currentUserMessage == null) {
                        Log.d(TAG, "➕ 새 사용자 메시지 추가");
                        currentUserMessage = new VoiceChatMessage(transcript, VoiceChatMessage.TYPE_USER);
                        currentUserMessage.setActive(true);
                        conversationAdapter.addMessage(currentUserMessage);
                        Log.d(TAG, "현재 메시지 수: " + conversationAdapter.getMessageCount());
                    } else {
                        Log.d(TAG, "🔄 사용자 메시지 업데이트: " + transcript);
                        currentUserMessage.setMessage(transcript);
                        conversationAdapter.updateLastMessage(transcript);
                    }
                    
                    // 마지막 메시지로 부드럽게 스크롤
                    scrollToBottom();
                    
                    // AI 응답 중이 아닐 때만 하단 텍스트 업데이트
                    if (!isAIResponding) {
                        instructionText.setText("듣고 있습니다...");
                    }
                });
            }

            @Override
            public void onResponseStarted() {
                Log.d(TAG, "🎯 AI 응답 시작");
                
                // AI 응답 중 플래그 설정
                isAIResponding = true;
                
                // 🔇 마이크 일시 중지 (에코 방지)
                realtimeService.pauseMicrophone();
                
                runOnUiThread(() -> {
                    // 사용자 메시지 비활성화
                    if (currentUserMessage != null) {
                        Log.d(TAG, "🔹 사용자 메시지 비활성화");
                        conversationAdapter.clearActiveMessage();
                        currentUserMessage = null;
                    }
                    
                    // 새 AI 메시지 시작
                    Log.d(TAG, "➕ 새 AI 메시지 추가");
                    currentAIMessage = new VoiceChatMessage("", VoiceChatMessage.TYPE_AI);
                    currentAIMessage.setActive(true);
                    conversationAdapter.addMessage(currentAIMessage);
                    Log.d(TAG, "현재 메시지 수: " + conversationAdapter.getMessageCount());
                    
                    instructionText.setText("AI가 응답하고 있습니다...");
                    animatedCircle.setSpeakingMode();
                    
                    // 마지막 메시지로 스크롤
                    scrollToBottom();
                });
            }

            @Override
            public void onResponseReceived(String response) {
                Log.d(TAG, "📤 AI 텍스트 델타: " + response);
                runOnUiThread(() -> {
                    // AI 메시지 실시간 업데이트
                    if (currentAIMessage != null) {
                        String currentText = currentAIMessage.getMessage();
                        String newText = currentText + response;
                        currentAIMessage.setMessage(newText);
                        conversationAdapter.updateLastMessage(newText);
                        Log.d(TAG, "🔄 AI 메시지 업데이트: " + newText.length() + " chars");
                        
                        // 마지막 메시지로 스크롤
                        scrollToBottom();
                        
                        // 하단 텍스트를 "답변 중입니다..."로 명확히 표시
                        instructionText.setText("답변 중입니다...");
                    }
                });
            }

            @Override
            public void onResponseComplete() {
                Log.d(TAG, "✅ AI 응답 완료");
                
                runOnUiThread(() -> {
                    // AI 메시지 비활성화
                    if (currentAIMessage != null) {
                        conversationAdapter.clearActiveMessage();
                        currentAIMessage = null;
                    }
                    
                    animatedCircle.setListeningMode();
                    instructionText.setText("잠시 후 말씀해주세요...");
                    
                    // 🎤 1초 후에 마이크 재개 (AI 응답 여운 + 에코 방지)
                    new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                        // AI 응답 중 플래그 해제
                        isAIResponding = false;
                        
                        realtimeService.resumeMicrophone();
                        instructionText.setText("말씀해주세요");
                        Log.d(TAG, "🎤 마이크 재개 (1초 딜레이) + AI 응답 플래그 해제");
                    }, 1000); // 1초 딜레이
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "오류: " + error);
                runOnUiThread(() -> {
                    Toast.makeText(RealtimeVoiceChatActivity.this, "오류: " + error, Toast.LENGTH_SHORT)
                            .show();
                    instructionText.setText("오류가 발생했습니다. 다시 시도해주세요.");
                });
            }

            @Override
            public void onDisconnected() {
                Log.d(TAG, "연결 종료됨");
                isConnected = false;
                runOnUiThread(() -> {
                    instructionText.setText("연결이 종료되었습니다.");
                });
            }
        });
    }

    private void checkPermissionAndStart() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] { android.Manifest.permission.RECORD_AUDIO },
                        PERMISSION_REQUEST_RECORD_AUDIO);
                return;
            }
        }

        startVoiceChat();
    }

    private void startVoiceChat() {
        instructionText.setText("연결 중...");
        animatedCircle.setConnectingMode();

        // Realtime API 연결
        realtimeService.connect();
    }

    private void startRecording() {
        if (!isRecording && isConnected) {
            isRecording = true;
            realtimeService.startAudioStreaming();
            animatedCircle.setListeningMode();
            Log.d(TAG, "음성 녹음 시작");
        }
    }

    /**
     * 순차적으로 안전하게 음성 대화 종료
     */
    private void stopVoiceChatGracefully() {
        new Thread(() -> {
            try {
                Log.d(TAG, "=== 음성 대화 종료 시작 ===");

                // 음소거는 이미 버튼 클릭 시 메인 스레드에서 즉시 실행됨
                Thread.sleep(100); // 음소거 처리 대기

                // 1단계: 오디오 스트리밍 중지 (녹음 및 재생)
                if (isRecording || isConnected) {
                    runOnUiThread(() -> instructionText.setText("오디오 중지 중..."));
                    realtimeService.stopAudioStreaming();
                    isRecording = false;
                    Thread.sleep(300); // 오디오 리소스 해제 대기
                    Log.d(TAG, "1단계: 오디오 스트리밍 중지 완료");
                }

                // 2단계: WebSocket 연결 종료
                if (isConnected) {
                    runOnUiThread(() -> instructionText.setText("연결 종료 중..."));
                    realtimeService.disconnect();
                    isConnected = false;
                    Thread.sleep(200); // WebSocket 종료 대기
                    Log.d(TAG, "2단계: WebSocket 연결 종료 완료");
                }

                // 3단계: 리소스 정리 완료
                runOnUiThread(() -> {
                    instructionText.setText("종료 완료");
                    Log.d(TAG, "=== 음성 대화 종료 완료 ===");
                    
                    // Activity 종료
                    finish();
                });

            } catch (Exception e) {
                Log.e(TAG, "종료 중 오류 발생", e);
                runOnUiThread(() -> {
                    Toast.makeText(RealtimeVoiceChatActivity.this, 
                        "종료 중 오류가 발생했습니다", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        }).start();
    }

    /**
     * 즉시 종료 (백그라운드로 이동 시)
     */
    private void stopVoiceChat() {
        if (isRecording) {
            realtimeService.stopAudioStreaming();
            isRecording = false;
        }

        if (isConnected) {
            realtimeService.disconnect();
            isConnected = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy 호출됨");
        stopVoiceChat();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause 호출됨 - 리소스 일시 정지");
        // 백그라운드로 가면 즉시 중지
        if (isRecording) {
            realtimeService.stopAudioStreaming();
            isRecording = false;
        }
    }

    /**
     * 마지막 메시지로 부드럽게 스크롤
     */
    private void scrollToBottom() {
        if (conversationAdapter.getMessageCount() > 0) {
            conversationList.post(() -> {
                int lastPosition = conversationAdapter.getMessageCount() - 1;
                conversationList.smoothScrollToPosition(lastPosition);
                Log.d(TAG, "📜 스크롤: position " + lastPosition);
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startVoiceChat();
            } else {
                Toast.makeText(this, "마이크 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}

