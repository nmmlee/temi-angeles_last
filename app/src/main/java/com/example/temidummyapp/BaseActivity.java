package com.example.temidummyapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import java.util.Locale;

/**
 * 모든 Activity의 기본 클래스
 * Wake Word 감지를 위한 권한 체크, 언어 설정, 전체화면 모드를 공통으로 처리합니다.
 */
public class BaseActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_RECORD_AUDIO = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 언어 설정을 가장 먼저 적용
        applySavedLanguage();
        super.onCreate(savedInstanceState);
        
        // 화면 항상 켜짐 유지
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        // ActionBar 숨기기
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        // 상태바 & 네비게이션바 숨김
        applyImmersiveMode();
        
        checkAndRequestAudioPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Activity가 다시 활성화될 때 Wake Word 서비스가 실행 중인지 확인
        ensureWakeWordServiceRunning();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            applyImmersiveMode(); // 포커스 복귀 시 다시 풀스크린 적용
        }
    }

    private void checkAndRequestAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST_RECORD_AUDIO);
        } else {
            // 권한이 이미 있으면 Wake Word 서비스 시작
            ensureWakeWordServiceRunning();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용되면 Wake Word 서비스 시작
                ensureWakeWordServiceRunning();
            }
        }
    }

    private void ensureWakeWordServiceRunning() {
        if (getApplication() instanceof TemiApplication) {
            TemiApplication app = (TemiApplication) getApplication();
            if (app != null && app.getWakeWordService() != null) {
                if (!app.getWakeWordService().isListening()) {
                    app.getWakeWordService().startListening();
                }
            }
        }
    }

    // ===== 언어 설정 관리 =====
    private void applySavedLanguage() {
        String languageCode = getSavedLanguage();
        if (languageCode != null && !languageCode.isEmpty()) {
            setLocale(languageCode);
        }
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private String getSavedLanguage() {
        SharedPreferences prefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        return prefs.getString("language", "ko"); // 기본값: 한국어
    }

    // ===== 전체화면 모드 =====
    private void applyImmersiveMode() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }
}

