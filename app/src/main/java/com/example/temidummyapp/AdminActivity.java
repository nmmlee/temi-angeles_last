package com.example.temidummyapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.robotemi.sdk.Robot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminActivity extends BaseActivity {

    private Robot robot;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        robot = Robot.getInstance();

        // 닫기 버튼
        TextView closeBtn = findViewById(R.id.admin_close);
        if (closeBtn != null) {
            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        // 저장 버튼
        View saveBtn = findViewById(R.id.admin_save);
        if (saveBtn != null) {
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveAdminMappings();
                }
            });
        }

        // RecyclerView 설정
        setupAdminRecycler();
    }

    private void setupAdminRecycler() {
        recyclerView = findViewById(R.id.recyclerMappings);
        if (recyclerView == null) return;

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 버튼 ID 목록 생성 (btn_01 ~ btn_21)
        ArrayList<String> ids = new ArrayList<>();
        for (int i = 1; i <= 21; i++) {
            ids.add(String.format(java.util.Locale.US, "btn_%02d", i));
        }

        // Temi 로봇의 위치 목록 가져오기
        List<String> temiLocations = getTemiLocationsSafe();

        // 저장된 매핑 불러오기
        Map<String, String> saved = AdminMappingStore.load(this);

        // 어댑터 설정
        AdminMappingAdapter adapter = new AdminMappingAdapter(this, ids, saved, temiLocations);
        recyclerView.setAdapter(adapter);
        recyclerView.setTag(adapter);
    }

    private List<String> getTemiLocationsSafe() {
        try {
            List<String> list = robot != null ? robot.getLocations() : null;
            return list != null ? list : new ArrayList<String>();
        } catch (Exception e) {
            return new ArrayList<String>();
        }
    }

    private void saveAdminMappings() {
        if (recyclerView == null) return;

        Object tag = recyclerView.getTag();
        if (!(tag instanceof AdminMappingAdapter)) return;

        AdminMappingAdapter adapter = (AdminMappingAdapter) tag;
        HashMap<String, String> map = new HashMap<>();

        for (AdminMappingAdapter.Item it : adapter.getItems()) {
            if (it.location != null && it.location.length() > 0) {
                map.put(it.buttonId, it.location);
            }
        }

        AdminMappingStore.save(this, map);
        Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

