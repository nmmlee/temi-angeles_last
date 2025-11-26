package com.example.temidummyapp;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatActivity extends BaseActivity {
    private static final String TAG = "ChatActivity";
    private static final int KEYBOARD_HEIGHT_THRESHOLD = 150;

    private ChatAdapter chatAdapter;
    private OpenAIService openAIService;
    private ApiKeyManager apiKeyManager;
    private RecyclerView chatList;
    private EditText inputMessage;
    private Button btnSend;
    private boolean isWaitingForResponse = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 전체화면 모드 및 리스너 설정
        setupImmersiveMode();
        setupKeyboardListener();
        stopWakeWordService();

        // OpenAI 서비스 초기화
        setupOpenAI();

        // UI 초기화
        initializeViews();

        // 환영 메시지 추가
        addWelcomeMessage();
    }

    private WakeWordService getWakeWordService() {
        if (getApplication() instanceof TemiApplication) {
            TemiApplication app = (TemiApplication) getApplication();
            return (app != null) ? app.getWakeWordService() : null;
        }
        return null;
    }

    private void stopWakeWordService() {
        WakeWordService service = getWakeWordService();
        if (service != null && service.isListening()) {
            service.stopListening();
            Log.d(TAG, "Wake Word 감지 일시 중지");
        }
    }

    private void startWakeWordService() {
        WakeWordService service = getWakeWordService();
        if (service != null && !service.isListening()) {
            service.startListening();
            Log.d(TAG, "Wake Word 감지 다시 시작");
        }
    }

    private void setupImmersiveMode() {
        applyKeyboardFriendlyImmersiveMode();

        // 시스템 UI 변경 감지 시 자동으로 다시 숨김
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> {
            if ((visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0) {
                applyKeyboardFriendlyImmersiveMode();
            }
        });
    }

    private void initializeViews() {
        setupBackButton();
        setupChatList();
        setupInputAndSendButton();
    }

    private void setupBackButton() {
        View backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
    }

    private void setupChatList() {
        chatList = findViewById(R.id.chat_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        chatList.setLayoutManager(layoutManager);

        chatAdapter = new ChatAdapter();
        chatList.setAdapter(chatAdapter);

        chatList.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hideKeyboard();
            }
            return false;
        });
    }

    private void setupInputAndSendButton() {
        inputMessage = findViewById(R.id.input_message);
        btnSend = findViewById(R.id.btn_send);

        btnSend.setOnClickListener(v -> sendUserMessage());

        // Enter 키로도 전송 가능
        inputMessage.setOnEditorActionListener((v, actionId, event) -> {
            sendUserMessage();
            return true;
        });
    }

    private void sendUserMessage() {
        String text = inputMessage.getText() != null ? inputMessage.getText().toString().trim() : "";

        if (text.isEmpty()) {
            Toast.makeText(this, getString(R.string.chat_empty_message), Toast.LENGTH_SHORT).show();
            return;
        }

        if (isWaitingForResponse) {
            Toast.makeText(this, "응답을 기다리는 중입니다...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!openAIService.hasApiKey()) {
            showApiKeyDialog();
            return;
        }

        // 사용자 메시지 추가
        ChatMessage userMessage = new ChatMessage(text, ChatMessage.TYPE_USER);
        chatAdapter.addMessage(userMessage);
        scrollToBottom();

        // 입력창 초기화
        inputMessage.setText("");
        hideKeyboard();

        // GPT 응답 요청
        requestBotResponse();
    }

    private void requestBotResponse() {
        isWaitingForResponse = true;
        btnSend.setEnabled(false);

        // 로딩 메시지 표시
        ChatMessage loadingMessage = new ChatMessage("답변을 생성하는 중...", ChatMessage.TYPE_BOT);
        chatAdapter.addMessage(loadingMessage);
        scrollToBottom();

        openAIService.sendMessage(chatAdapter.getMessages(), new OpenAIService.ChatCallback() {
            @Override
            public void onSuccess(String response) {
                isWaitingForResponse = false;
                btnSend.setEnabled(true);

                // 로딩 메시지 제거하고 실제 응답 추가
                chatAdapter.getMessages().remove(chatAdapter.getMessages().size() - 1);
                chatAdapter.notifyItemRemoved(chatAdapter.getMessages().size());

                ChatMessage botMessage = new ChatMessage(response, ChatMessage.TYPE_BOT);
                chatAdapter.addMessage(botMessage);
                scrollToBottom();
            }

            @Override
            public void onError(String error) {
                isWaitingForResponse = false;
                btnSend.setEnabled(true);

                // 로딩 메시지 제거하고 에러 메시지 추가
                chatAdapter.getMessages().remove(chatAdapter.getMessages().size() - 1);
                chatAdapter.notifyItemRemoved(chatAdapter.getMessages().size());

                ChatMessage errorMessage = new ChatMessage(
                        "죄송합니다. 오류가 발생했습니다: " + error,
                        ChatMessage.TYPE_BOT);
                chatAdapter.addMessage(errorMessage);
                scrollToBottom();

                Log.e(TAG, "GPT 응답 오류: " + error);
            }
        });
    }

    private void applyKeyboardFriendlyImmersiveMode() {
        // Android 6.0.1: adjustResize 작동을 위해 FLAG_FULLSCREEN 제거
        // 컨텐츠는 상단바 뒤로 확장, 네비게이션 바만 숨김
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus)
            applyKeyboardFriendlyImmersiveMode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyKeyboardFriendlyImmersiveMode();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(null);
        startWakeWordService();
    }

    private void setupKeyboardListener() {
        View rootLayout = findViewById(R.id.root_layout);
        if (rootLayout == null)
            return;

        final boolean[] wasKeyboardVisible = { false };

        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            android.graphics.Rect r = new android.graphics.Rect();
            rootLayout.getWindowVisibleDisplayFrame(r);
            int keypadHeight = rootLayout.getRootView().getHeight() - r.bottom;
            boolean isKeyboardVisible = keypadHeight > KEYBOARD_HEIGHT_THRESHOLD;

            // 키보드가 내려갔을 때 전체화면 재적용
            if (!isKeyboardVisible && wasKeyboardVisible[0]) {
                rootLayout.postDelayed(this::applyKeyboardFriendlyImmersiveMode, 150);
            }

            wasKeyboardVisible[0] = isKeyboardVisible;
        });
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void scrollToBottom() {
        if (chatAdapter.getItemCount() > 0) {
            chatList.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
        }
    }

    private void setupOpenAI() {
        openAIService = new OpenAIService();
        apiKeyManager = new ApiKeyManager(this);

        // API 키 직접 설정
        String apiKey = "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";
        apiKeyManager.saveApiKey(apiKey);
        openAIService.setApiKey(apiKey);

        Log.d(TAG, "OpenAI API 키 설정 완료");
    }

    private void showApiKeyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("OpenAI API 키 설정");
        builder.setMessage("챗봇 기능을 사용하려면 OpenAI API 키가 필요합니다.\n\nAPI 키를 입력해주세요:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("sk-proj-...");
        builder.setView(input);

        builder.setPositiveButton("확인", (dialog, which) -> {
            String apiKey = input.getText().toString().trim();
            if (!apiKey.isEmpty()) {
                apiKeyManager.saveApiKey(apiKey);
                openAIService.setApiKey(apiKey);
                Toast.makeText(this, "API 키가 저장되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "API 키를 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("취소", (dialog, which) -> {
            dialog.cancel();
            Toast.makeText(this, "API 키 없이는 챗봇을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
        });

        builder.show();
    }

    private void addWelcomeMessage() {
        ChatMessage welcomeMessage = new ChatMessage(
                "안녕하세요! 행사장 안내 챗봇입니다.\n\n" +
                        "행사장 정보, 부스 위치, 이벤트 일정 등 궁금한 점을 물어보세요.",
                ChatMessage.TYPE_BOT);
        chatAdapter.addMessage(welcomeMessage);
    }
}
