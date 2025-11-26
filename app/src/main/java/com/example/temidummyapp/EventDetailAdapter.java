package com.example.temidummyapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventDetailAdapter extends RecyclerView.Adapter<EventDetailAdapter.ViewHolder> {

    private List<EventData> eventList;

    public EventDetailAdapter(List<EventData> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventData event = eventList.get(position);
        
        // 왼쪽 빨간 섹션 - 이미지만 표시
        if (event.getImageResId() != 0 && holder.eventImage != null) {
            holder.eventImage.setImageResource(event.getImageResId());
        }
        
        // 오른쪽 흰 섹션
        holder.eventIntroTitle.setText(event.getIntroTitle());
        holder.eventIntro1.setText(event.getIntro1());
        holder.eventIntro2.setText(event.getIntro2());
        holder.eventHighlight.setText(event.getHighlight());
        
        // note1, note2가 비어있으면 숨기기
        if (event.getNote1() != null && !event.getNote1().isEmpty()) {
            holder.eventNote1.setVisibility(View.VISIBLE);
            holder.eventNote1.setText(event.getNote1());
        } else {
            holder.eventNote1.setVisibility(View.GONE);
        }
        
        if (event.getNote2() != null && !event.getNote2().isEmpty()) {
            holder.eventNote2.setVisibility(View.VISIBLE);
            holder.eventNote2.setText(event.getNote2());
        } else {
            holder.eventNote2.setVisibility(View.GONE);
        }
        
        holder.eventDate.setText(event.getDate());
        holder.eventLocation.setText(event.getLocation());
        holder.eventParticipationTitle.setText(event.getParticipationTitle());
        holder.eventMethod1.setText(event.getMethod1());
        holder.eventMethod2.setText(event.getMethod2());
        
        if (event.getMethod2Detail1() != null && !event.getMethod2Detail1().isEmpty()) {
            holder.eventMethod2Detail1.setVisibility(View.VISIBLE);
            holder.eventMethod2Detail1.setText(event.getMethod2Detail1());
        } else {
            holder.eventMethod2Detail1.setVisibility(View.GONE);
        }
        
        if (event.getMethod2Detail2() != null && !event.getMethod2Detail2().isEmpty()) {
            holder.eventMethod2Detail2.setVisibility(View.VISIBLE);
            holder.eventMethod2Detail2.setText(event.getMethod2Detail2());
        } else {
            holder.eventMethod2Detail2.setVisibility(View.GONE);
        }
        
        if (event.getMethod2Detail3() != null && !event.getMethod2Detail3().isEmpty()) {
            holder.eventMethod2Detail3.setVisibility(View.VISIBLE);
            holder.eventMethod2Detail3.setText(event.getMethod2Detail3());
        } else {
            holder.eventMethod2Detail3.setVisibility(View.GONE);
        }
        
        holder.eventMethod3.setText(event.getMethod3());
        holder.eventMethod3Detail.setText(event.getMethod3Detail());
        
        if (event.getMethod4() != null && !event.getMethod4().isEmpty()) {
            holder.eventMethod4.setVisibility(View.VISIBLE);
            holder.eventMethod4.setText(event.getMethod4());
        } else {
            holder.eventMethod4.setVisibility(View.GONE);
        }
        
        holder.eventReward.setText(event.getReward());
        
        if (event.getClosing() != null && !event.getClosing().isEmpty()) {
            holder.eventClosing.setVisibility(View.VISIBLE);
            holder.eventClosing.setText(event.getClosing());
        } else {
            holder.eventClosing.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return eventList != null ? eventList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        // 왼쪽 빨간 섹션
        ImageView eventImage;
        
        // 오른쪽 흰 섹션
        TextView eventIntroTitle;
        TextView eventIntro1;
        TextView eventIntro2;
        TextView eventHighlight;
        TextView eventNote1;
        TextView eventNote2;
        TextView eventDate;
        TextView eventLocation;
        TextView eventParticipationTitle;
        TextView eventMethod1;
        TextView eventMethod2;
        TextView eventMethod2Detail1;
        TextView eventMethod2Detail2;
        TextView eventMethod2Detail3;
        TextView eventMethod3;
        TextView eventMethod3Detail;
        TextView eventMethod4;
        TextView eventReward;
        TextView eventClosing;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.eventImage);
            
            eventIntroTitle = itemView.findViewById(R.id.eventIntroTitle);
            eventIntro1 = itemView.findViewById(R.id.eventIntro1);
            eventIntro2 = itemView.findViewById(R.id.eventIntro2);
            eventHighlight = itemView.findViewById(R.id.eventHighlight);
            eventNote1 = itemView.findViewById(R.id.eventNote1);
            eventNote2 = itemView.findViewById(R.id.eventNote2);
            eventDate = itemView.findViewById(R.id.eventDate);
            eventLocation = itemView.findViewById(R.id.eventLocation);
            eventParticipationTitle = itemView.findViewById(R.id.eventParticipationTitle);
            eventMethod1 = itemView.findViewById(R.id.eventMethod1);
            eventMethod2 = itemView.findViewById(R.id.eventMethod2);
            eventMethod2Detail1 = itemView.findViewById(R.id.eventMethod2Detail1);
            eventMethod2Detail2 = itemView.findViewById(R.id.eventMethod2Detail2);
            eventMethod2Detail3 = itemView.findViewById(R.id.eventMethod2Detail3);
            eventMethod3 = itemView.findViewById(R.id.eventMethod3);
            eventMethod3Detail = itemView.findViewById(R.id.eventMethod3Detail);
            eventMethod4 = itemView.findViewById(R.id.eventMethod4);
            eventReward = itemView.findViewById(R.id.eventReward);
            eventClosing = itemView.findViewById(R.id.eventClosing);
        }
    }
}

