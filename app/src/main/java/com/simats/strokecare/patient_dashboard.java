package com.simats.strokecare;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.simats.strokecare.API.BASE_URL;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import android.os.Handler;
import java.util.concurrent.TimeUnit;

public class patient_dashboard extends AppCompatActivity {

    private Handler handler = new Handler();
    private List<NotificationData> notifications = new ArrayList<>();  // Store fetched notifications
    private DrawerLayout drawerLayout;
     // Store fetched notifications
    private List<NotificationData> shownNotifications = new ArrayList<>();  // Track already shown notifications

    private NavigationView navigationView;
    private Toolbar toolbar;
    private Button physioButton, positionsButton, bedsoresButton, feedsButton, foleysButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dashboard);

        // Existing code
        drawerLayout = findViewById(R.id.dl);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        physioButton = findViewById(R.id.physio);
        positionsButton = findViewById(R.id.bed_change_positions);
        bedsoresButton = findViewById(R.id.bed_sores);
        feedsButton = findViewById(R.id.feeds);
        foleysButton = findViewById(R.id.foleys);

        Intent intent = getIntent();
        String HID = intent.getStringExtra("HID");

        if (HID != null && !HID.isEmpty()) {
            Log.d(TAG, "Received HID: " + HID);
            fetchNotifications(HID); // Pass the HID to fetchNotifications method
        } else {
            Log.e(TAG, "Hospital ID is missing");
            Toast.makeText(this, "Hospital ID is missing", Toast.LENGTH_LONG).show();
        }

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.OpenDrawer, R.string.CloseDrawer
        );
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.id1) {
                Intent intent1 = new Intent(patient_dashboard.this, Patient_profile.class);
                intent1.putExtra("HID", HID);
                startActivity(intent1);
            } else if (id == R.id.id2) {
                Intent intent2 = new Intent(patient_dashboard.this, MainActivity.class);
                startActivity(intent2);
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        physioButton.setOnClickListener(view -> {
            Intent intent3 = new Intent(patient_dashboard.this, patient_physio.class);
            intent3.putExtra("HID", HID);
            startActivity(intent3);
        });
        positionsButton.setOnClickListener(view -> {
            Intent intent4 = new Intent(patient_dashboard.this, bed_change_positions.class);
            intent4.putExtra("HID", HID);
            startActivity(intent4);
        });
        bedsoresButton.setOnClickListener(view -> {
            Intent intent5 = new Intent(patient_dashboard.this, bed_sores.class);
            intent5.putExtra("HID", HID);
            startActivity(intent5);
        });
        feedsButton.setOnClickListener(view -> {
            Intent intent6 = new Intent(patient_dashboard.this, feeds.class);
            intent6.putExtra("HID", HID);
            startActivity(intent6);
        });
        foleysButton.setOnClickListener(view -> {
            Intent intent7 = new Intent(patient_dashboard.this, foleys.class);
            intent7.putExtra("HID", HID);
            startActivity(intent7);
        });

        // Start the time checker
        startCheckingNotifications();
    }

    private void fetchNotifications(String HID) {
        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        ApiService apiService = retrofit.create(ApiService.class);

        Call<ApiResponse> call = apiService.fetchNotifications(HID);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                int statusCode = response.code();
                Log.d(TAG, "Response Code: " + statusCode);

                if (response.isSuccessful() && response.body() != null) {
                    notifications = response.body().getData(); // Save notifications in a list

                    if (notifications != null && !notifications.isEmpty()) {
                        // Start checking for time-matching notifications
                        checkForTimeBasedNotifications();
                    } else {
                        Log.d(TAG, "No notifications available");
                        Toast.makeText(patient_dashboard.this, "No notifications available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMessage = response.errorBody() != null ? response.errorBody().toString() : "No error body";
                    Log.e(TAG, "Failed to fetch notifications - Status Code: " + statusCode + " Error: " + errorMessage);
                    Toast.makeText(patient_dashboard.this,
                            "Failed to fetch notifications - Status Code: " + statusCode + " Error: " + errorMessage,
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e(TAG, "Error fetching notifications: " + t.getMessage());
                Toast.makeText(patient_dashboard.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void startCheckingNotifications() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkForTimeBasedNotifications();
                handler.postDelayed(this, TimeUnit.MINUTES.toMillis(1)); // Check every minute
            }
        }, TimeUnit.MINUTES.toMillis(1));
    }

    private void checkForTimeBasedNotifications() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = timeFormat.format(Calendar.getInstance().getTime());

        for (NotificationData notification : notifications) {
            String notificationTime = notification.getTiming();  // Assume the time is in "HH:mm" format

            if (currentTime.equals(notificationTime)) {
                // Show notification only if it hasn't been shown before
                if (!shownNotifications.contains(notification)) {
                    showAlert(notification.getMessage());
                    shownNotifications.add(notification); // Mark as shown
                }
            }
        }
    }

    private void showAlert(String message) {
        Dialog myDialog = new Dialog(this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.notification);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView messageTextView = myDialog.findViewById(R.id.messageTextView);
        messageTextView.setText(message);

        myDialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, plogin.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
