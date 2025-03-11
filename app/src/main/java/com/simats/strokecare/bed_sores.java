package com.simats.strokecare;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class bed_sores extends AppCompatActivity {
    private CheckBox checkBox1, checkBox2;
    private Button button;

    private Button button1;
    private String insertURL = API.Physio_URL; // Change to your PHP script URL
    private String HID;
    private Toolbar tb1;

    private VideoView videoView;
    private Button notify;

    private TextView text;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bed_sores);

//        videoView = findViewById(R.id.bedve);

        text = findViewById(R.id.text1);
        Intent intent = getIntent();
        HID = intent.getStringExtra("HID");
        if (HID == null) {
            // Hospital ID not found, handle appropriately
            Toast.makeText(this, "Hospital ID not found", Toast.LENGTH_SHORT).show();
            return;
        }
        text.setText(HID);

        tb1 = findViewById(R.id.tb);
        setSupportActionBar(tb1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.my_button_toolbar);
        }
        // Set the path of the video
//        String videoPath = Vedio_URL;
//
//        // Parse the URI from the video path
//        Uri uri = Uri.parse(videoPath);
//
//        // Set the URI to the VideoView
//        videoView.setVideoURI(uri);
//
//        // Create a MediaController to control the video playback
//        MediaController mediaController = new MediaController(this);
//        mediaController.setAnchorView(videoView);
//
//        // Set the MediaController for the VideoView
//        videoView.setMediaController(mediaController);

        // Start playing the video
//       videoView.start();

        button1 = findViewById(R.id.his_btn);

        button1.setOnClickListener(view -> {
            Intent intent2 = new Intent(bed_sores.this, bed_sores_patient.class);
            intent2.putExtra("HID", HID);
            startActivity(intent2);
        });

//
//      notify=findViewById(R.id.noti);
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
//            if(ContextCompat.checkSelfPermission(patient_physio.this,
//                    Manifest.permission.POST_NOTIFICATIONS)!= PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(patient_physio.this,
//                        new String[]{Manifest.permission.POST_NOTIFICATIONS},101);
//            }
//        }
//
//        startService(new Intent(this, BackgroundNotificationService.class));

//        checkTimeAndShowInAppNotification();
//        private void checkTimeAndShowInAppNotification() {
//            // Get the current time
//            Calendar c = Calendar.getInstance();
//            int currentHour = c.get(Calendar.HOUR_OF_DAY);
//
//            // Check if the current time is 8 AM or 4 PM
//            if (currentHour == 8 || currentHour == 16) {
//                // Show in-app notification
//                showInAppNotification();
//            }
//        }
//        private void showInAppNotification() {
//            // Inflate the custom layout for the in-app notification
//            View customNotificationView = LayoutInflater.from(this).inflate(R.layout.custom_notification_layout, null);
//
//            // Find and set text for notification message
//            TextView messageTextView = customNotificationView.findViewById(R.id.messageTextView);
//            messageTextView.setText("It's time for your physiotherapy session!");
//
//            // Show in-app notification
//            Toast inAppNotification = new Toast(this);
//            inAppNotification.setView(customNotificationView);
//            inAppNotification.setDuration(Toast.LENGTH_LONG);
//            inAppNotification.show();
//        }

//        Intent intent = getIntent();
//        hospitalId = intent.getStringExtra("hospitalId");

//        // If hospitalId is null, set a default value
//        if (hospitalId == null || hospitalId.isEmpty()) {
//           // hospitalId = "13532"; // Default value
//        }

        checkBox1 = findViewById(R.id.checkbox1);
        checkBox2 = findViewById(R.id.checkbox2);
        button = findViewById(R.id.done_button); // Changed button id to "done_button"

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if any checkbox is checked
                if (checkBox1.isChecked() || checkBox2.isChecked()) {
                    // Disable checkboxes
                    checkBox1.setEnabled(false);
                    checkBox2.setEnabled(false);

                    // Show custom toast message
                    ToastUtils.showToast(getApplicationContext(), "Checkbox submission completed");

                    // Prepare data for sending
                    JSONObject postData = preparePostData();
                    Intent intent1 = new Intent(bed_sores.this, patient_dashboard.class);
                    intent1.putExtra("HID", HID);
                    startActivity(intent1);

                    // Send data to PHP script
                    sendDataToServer(postData);
                } else {
                    // Show custom toast message if no checkbox is checked
                    ToastUtils.showToast(getApplicationContext(),"Please check at least one checkbox");
                }
            }
        });

        // Set click listeners for the checkboxes
        checkBox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleCheckboxClick(checkBox1);
            }
        });

        checkBox2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleCheckboxClick(checkBox2);
            }
        });

        // Initial check for time and checkbox enabling
        checkTimeAndEnableCheckbox();
    }

    private JSONObject preparePostData() {
        JSONObject postData = new JSONObject();
        try {
            postData.put("hospital_id", HID);

            // Include 1 if checkBox1 is checked, otherwise exclude from postData
            if (checkBox1.isChecked()) {
                postData.put("bedsores_1", 1);
            }

            // Include 1 if checkBox2 is checked, otherwise exclude from postData
            if (checkBox2.isChecked()) {
                postData.put("bedsores_2", 1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postData;
    }



    private void handleCheckboxClick(CheckBox clickedCheckbox) {
        // Uncheck the other checkbox
        if (clickedCheckbox == checkBox1) {
            checkBox2.setChecked(false);
        } else {
            checkBox1.setChecked(false);
        }
    }

    private void checkTimeAndEnableCheckbox() {
        // Get the current time
        Calendar c = Calendar.getInstance();
        int currentHour = c.get(Calendar.HOUR_OF_DAY);

        // Check if the current time is between 7 am and 9 am
        boolean isTimeBetween7to9AM = currentHour >= 8 && currentHour <= 15;
//        int currentMinute = 0;
        // Check if the current time is between 7 am and 9 am
//        boolean isTimeBetween7to9AM = currentHour == 11 && currentMinute >= 35 && currentMinute < 60;

// Check if the current time is between 3 pm and 5 pm
        boolean isTimeBetween1to5PM = currentHour >= 16&& currentHour <= 19;
//        boolean isTimeBetween1to5AM = currentHour >= 16 && currentHour <= 17;
// Enable the appropriate checkbox based on the time condition
        checkBox1.setEnabled(isTimeBetween7to9AM);
        checkBox2.setEnabled(isTimeBetween1to5PM);

// Uncheck and disable the other checkbox
        if (isTimeBetween7to9AM) {
            checkBox2.setChecked(false);
            checkBox2.setEnabled(false);
        } else if (isTimeBetween1to5PM) {
            checkBox1.setChecked(false);
            checkBox1.setEnabled(false);
        }

// Send notification if the current hour is 11 or 16 (4 PM) and neither checkbox is checked
//        if ((currentHour == 11 || currentHour == 16) && !checkBox1.isChecked() && !checkBox2.isChecked()) {
//            sendNotification();
////        }
//        if ((currentHour == 8 && currentMinute == 6) || (!checkBox1.isChecked() && !checkBox2.isChecked())) {
//            sendNotification();
//        }
//        Intent intent = new Intent(patient_physio.this, patient_dashboard.class);
//        intent.putExtra("isTimeForTask", true); // Replace true with your condition
//        startActivity(intent);

        // Check the condition for sending a notification
//        Calendar c = Calendar.getInstance();
//        int currentHour = c.get(Calendar.HOUR_OF_DAY);
//        if ((currentHour == 11 || currentHour == 16) && !checkBox1.isChecked() && !checkBox2.isChecked()) {
//            isTimeForTask();
//        }

    }

    // Inside the onClick() method of a button in patient_physio activity

//    private void sendNotification() {
//
//        // Create notification channel
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = "ReminderChannel";
//            String description = "Channel for reminder notifications";
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel("notifyID", name, importance);
//            channel.setDescription(description);
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        // Build notification
//        android.app.Notification.Builder builder = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            builder = new android.app.Notification.Builder(this, "notifyID")
//                    .setSmallIcon(R.drawable.ic_launcher_foreground)
//                    .setContentTitle("Reminder")
//                    .setContentText("Remember to check your physiotherapy schedule.")
//                    .setPriority(android.app.Notification.PRIORITY_DEFAULT);
//        }
//        Intent intent = new Intent(getApplicationContext(),patient_dashboard.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("data","Remember to check your physiotherapy schedule.");
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        // Show notification
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(0, builder.build());
//    }

    private void sendDataToServer(JSONObject postData) {
        Log.d("SendDataToServer", "Sending data to server: " + postData.toString());

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Create a String request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, insertURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if (status.equals("success")) {
                                ToastUtils.showToast(getApplicationContext(), "Data saved successfully");
                            } else {
                                ToastUtils.showToast(getApplicationContext(), "Error: " + jsonObject.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtils.showToast(getApplicationContext(), "Error parsing JSON response");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtils.showToast(getApplicationContext(), "Error saving data: " + error.getMessage());
                        Log.e("Volley Error", error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                try {
                    // Add hospital_id, physio_morning, and physio_evening to form data
                    params.put("hospital_id", postData.getString("hospital_id"));
                    if (postData.has("bedsores_1")) {
                        params.put("bedsores_1", String.valueOf(postData.getInt("bedsores_1")));
                    }
                    if (postData.has("bedsores_2")) {
                        params.put("bedsores_2", String.valueOf(postData.getInt("bedsores_2")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("SendDataToServer", "Params: " + params.toString());
                return params;
            }
        };

        // Set a custom retry policy
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    // Method to show toast message
//    private void showToast(String message) {
//        ToastUtils.showToast(getApplicationContext(), message);
//    }
// if ((currentHour == 8 && currentMinute == 6) || (!checkBox1.isChecked() && !checkBox2.isChecked())) {
//        sendNotification();
//    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Navigate back to Patient_status activity
            Intent intent3 = new Intent(bed_sores.this, patient_dashboard.class);
//            intent1.putExtra("hospitalId", getIntent().getStringExtra("hospitalId"));
            intent3.putExtra("HID", HID);
            startActivity(intent3);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        // Perform any action you want before navigating back
        Intent intent = new Intent(this, patient_dashboard.class);
        intent.putExtra("HID", getIntent().getStringExtra("HID"));
        startActivity(intent);

        // Finish the current activity to prevent returning to it
        finish();

        // Call the super method to maintain default behavior (optional based on your needs)
        super.onBackPressed();
    }

//    private void checkTimeAndShowInAppNotification() {
//        // Get the current time
//        Calendar c = Calendar.getInstance();
//        int currentHour = c.get(Calendar.HOUR_OF_DAY);
//
//        // Check if the current time is 8 AM or 4 PM
//        if (currentHour == 9 || currentHour == 16) {
//            // Show in-app notification
//            showInAppNotification();
//        }
//    }
//
//    private void showInAppNotification() {
//        // Inflate the custom layout for the in-app notification
//        View customNotificationView = LayoutInflater.from(this).inflate(R.layout.custom_notification_layout, null);
//
//        // Find and set text for notification message
//        TextView messageTextView = customNotificationView.findViewById(R.id.messageTextView);
//        messageTextView.setText("It's time for your physiotherapy session!");
//
//        // Show in-app notification
//        Toast inAppNotification = new Toast(this);
//        inAppNotification.setView(customNotificationView);
//        inAppNotification.setDuration(Toast.LENGTH_LONG);
//        inAppNotification.show();
//    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        // Store the checkbox states in SharedPreferences
//        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putBoolean("checkbox1", checkBox1.isChecked());
//        editor.putBoolean("checkbox2", checkBox2.isChecked());
//        editor.apply();
//    }

}
