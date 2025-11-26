package com.example.temidummyapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminMappingAdapter extends RecyclerView.Adapter<AdminMappingAdapter.VH> {

    public static class Item {
        public final String buttonId;
        public String location;
        public Item(String buttonId, String location) {
            this.buttonId = buttonId;
            this.location = location;
        }
    }

    private final List<Item> items = new ArrayList<>();
    private final List<String> allLocations = new ArrayList<>();
    private final Context context;

    public AdminMappingAdapter(Context context, List<String> buttonIds, Map<String, String> saved, List<String> temiLocations) {
        this.context = context;
        if (buttonIds != null) {
            for (String id : buttonIds) {
                String loc = saved != null ? saved.get(id) : null;
                items.add(new Item(id, loc != null ? loc : ""));
            }
        }
        if (temiLocations != null) {
            allLocations.add("");
            allLocations.addAll(temiLocations);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_mapping, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Item item = items.get(position);
        holder.textButtonId.setText(item.buttonId);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, allLocations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinner.setAdapter(adapter);
        if (item.location != null && !item.location.isEmpty()) {
            int idx = allLocations.indexOf(item.location);
            if (idx >= 0) holder.spinner.setSelection(idx);
        } else {
            holder.spinner.setSelection(0);
        }
        holder.spinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int i, long l) {
                item.location = allLocations.get(i);
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<Item> getItems() {
        return items;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView textButtonId;
        Spinner spinner;
        VH(@NonNull View itemView) {
            super(itemView);
            textButtonId = itemView.findViewById(R.id.textButtonId);
            spinner = itemView.findViewById(R.id.spinnerLocation);
        }
    }
}

