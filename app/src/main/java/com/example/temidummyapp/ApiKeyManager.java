package com.example.temidummyapp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * OpenAI API 키 관리 클래스
 */
public class ApiKeyManager {
    private static final String PREFS_NAME = "openai_prefs";
    private static final String KEY_API_KEY = "api_key";
    
    private final SharedPreferences prefs;
    
    public ApiKeyManager(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * API 키 저장
     */
    public void saveApiKey(String apiKey) {
        prefs.edit().putString(KEY_API_KEY, apiKey).apply();
    }
    
    /**
     * API 키 가져오기
     */
    public String getApiKey() {
        return prefs.getString(KEY_API_KEY, "");
    }
    
    /**
     * API 키가 설정되어 있는지 확인
     */
    public boolean hasApiKey() {
        String key = getApiKey();
        return key != null && !key.isEmpty();
    }
    
    /**
     * API 키 삭제
     */
    public void clearApiKey() {
        prefs.edit().remove(KEY_API_KEY).apply();
    }
}

