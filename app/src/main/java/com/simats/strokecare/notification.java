package com.simats.strokecare;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class notification extends AppCompatActivity implements notificationTask.OnNotificationsFetchedListener {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private ArrayList<String> notificationList;
    private String hospital_id;
    private TextView noRecordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        recyclerView = findViewById(R.id.rc);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(adapter);
        noRecordTextView = findViewById(R.id.noRecordTextView);

        Intent intent = getIntent();
        hospital_id = intent.getStringExtra("HID");

        // Fetch notifications for the hospital ID
        if (hospital_id != null && !hospital_id.isEmpty()) {
            new notificationTask(this).execute(hospital_id);
        } else {
            // Handle the case where hospital ID is not available
            // For example, show an error message or navigate back to the previous activity
            // Here, I'm just logging an error message
            Log.e(TAG, "Hospital ID is not provided");
        }
    }

    @Override
    public void onNotificationsFetched(ArrayList<String> notifications) {
        if (notifications.isEmpty()) {
            showNoRecordsMessage();
        } else {
            notificationList.clear();
            notificationList.addAll(notifications);
            adapter.notifyDataSetChanged();
            hideNoRecordsMessage();
        }
    }

    private void showNoRecordsMessage() {
        recyclerView.setVisibility(View.GONE);
        noRecordTextView.setVisibility(View.VISIBLE);
    }

    private void hideNoRecordsMessage() {
        recyclerView.setVisibility(View.VISIBLE);
        noRecordTextView.setVisibility(View.GONE);
    }
}
