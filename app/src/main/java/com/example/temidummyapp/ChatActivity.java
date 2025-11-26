package com.example.temidummyapp;

import android.content.Context;
import android.os.Bundle;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        
        // 전체화면 모드 및 리스너 설정
        setupImmersiveMode();
        setupKeyboardListener();
        stopWakeWordService();
        
        // UI 초기화
        initializeViews();
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
        RecyclerView chatList = findViewById(R.id.chat_list);
        chatList.setLayoutManager(new LinearLayoutManager(this));
        chatList.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hideKeyboard();
            }
            return false;
        });
    }
    
    private void setupInputAndSendButton() {
        EditText input = findViewById(R.id.input_message);
        Button send = findViewById(R.id.btn_send);
        
        send.setOnClickListener(v -> {
            String text = input.getText() != null ? input.getText().toString().trim() : "";
            if (text.isEmpty()) {
                Toast.makeText(this, getString(R.string.chat_empty_message), Toast.LENGTH_SHORT).show();
                return;
            }
            // TODO: 메시지를 리스트에 추가하고, GPT 응답을 요청하는 로직 연결
            Toast.makeText(this, getString(R.string.chat_message_sent, text), Toast.LENGTH_SHORT).show();
            input.setText("");
            hideKeyboard();
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
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) applyKeyboardFriendlyImmersiveMode();
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
        if (rootLayout == null) return;
        
        final boolean[] wasKeyboardVisible = {false};
        
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
}


