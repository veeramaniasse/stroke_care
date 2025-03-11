package com.simats.strokecare;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class notification_pat extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_pat);

        // Find the notification message TextView
        TextView notificationMessageTextView = findViewById(R.id.messageTextView);

        // Receive the notification message from the intent
        String notificationMessage = getIntent().getStringExtra("data");

        // Set the notification message to the TextView
        notificationMessageTextView.setText(notificationMessage);
    }
}
