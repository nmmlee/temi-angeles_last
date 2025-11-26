package com.example.temidummyapp;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class AdminMappingStore {

    private static final String PREF = "admin_mapping_pref";
    private static final String KEY = "map_button_mappings";

    private AdminMappingStore() {}

    public static Map<String, String> load(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String json = sp.getString(KEY, "{}");
        Map<String, String> map = new HashMap<>();
        try {
            JSONObject obj = new JSONObject(json);
            Iterator<String> it = obj.keys();
            while (it.hasNext()) {
                String k = it.next();
                map.put(k, obj.optString(k, ""));
            }
        } catch (JSONException ignored) {}
        return map;
    }

    public static void save(Context context, Map<String, String> map) {
        JSONObject obj = new JSONObject();
        for (Map.Entry<String, String> e : map.entrySet()) {
            try {
                obj.put(e.getKey(), e.getValue());
            } catch (JSONException ignored) {}
        }
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().putString(KEY, obj.toString()).apply();
    }
}

