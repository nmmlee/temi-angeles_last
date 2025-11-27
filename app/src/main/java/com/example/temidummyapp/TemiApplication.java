package com.example.temidummyapp;

import android.app.Application;
import android.util.Log;

public class TemiApplication extends Application {
    private static final String TAG = "TemiApplication";
    private WakeWordService wakeWordService;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Wake Word 서비스 초기화만 수행 (자동 시작 안 함)
        // MainActivity의 "테미야" 버튼으로만 제어됨
        wakeWordService = new WakeWordService(this);
        Log.d(TAG, "Wake word service created (controlled by MainActivity toggle button)");
    }
    
    public WakeWordService getWakeWordService() {
        return wakeWordService;
    }
    
    @Override
    public void onTerminate() {
        super.onTerminate();
        if (wakeWordService != null) {
            wakeWordService.release();
        }
    }
}

