package com.simats.strokecare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class patient_physio extends AppCompatActivity {
    private CheckBox checkBox1, checkBox2;
    private Button button1;
    private String insertURL = API.Physio_URL; // Change to your PHP script URL
    private String HID;
    private Toolbar tb1;
    private VideoView videoView;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_physio);

        videoView = findViewById(R.id.physiov);
        text = findViewById(R.id.text1);

        // Get hospital ID from intent
        Intent intent = getIntent();
        HID = intent.getStringExtra("HID");
        if (HID == null) {
            // Handle case where hospital ID is not found
            Toast.makeText(this, "Hospital ID not found", Toast.LENGTH_SHORT).show();
            return;
        }
        text.setText(HID);

        // Setup toolbar
        tb1 = findViewById(R.id.tb);
        setSupportActionBar(tb1);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.my_button_toolbar);
        }

        // Prepare video playback
        String videoPath = API.Vedio_URL;
        if (videoPath != null) {
            Uri uri = Uri.parse(videoPath);
            videoView.setVideoURI(uri);

            // Set media controller
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);

            // Start video playback when prepared
            videoView.setOnPreparedListener(mp -> {
                // Ensure video playback operations are on the main thread
                runOnUiThread(() -> {
                    videoView.start();
                });
            });

            // Handle video playback errors
            videoView.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(patient_physio.this, "Error loading video", Toast.LENGTH_SHORT).show();
                Log.e("VideoView Error", "Error code: " + what + ", Extra: " + extra);
                return true; // Returning true indicates that the error is handled
            });
        } else {
            // Handle case where video URL is invalid or null
            Toast.makeText(this, "Video URL is not available", Toast.LENGTH_SHORT).show();
            Log.e("SurfaceView Issue", "Vedio_URL is null or invalid");
        }

        // Button to navigate to history activity
        button1 = findViewById(R.id.his_btn);
        button1.setOnClickListener(view -> {
            Intent intent2 = new Intent(patient_physio.this, overall_history.class);
            intent2.putExtra("HID", HID);
            startActivity(intent2);
        });

        checkBox1 = findViewById(R.id.checkbox1);
        checkBox2 = findViewById(R.id.checkbox2);
        Button doneButton = findViewById(R.id.done_button);

        doneButton.setOnClickListener(v -> {
            if (checkBox1.isChecked() || checkBox2.isChecked()) {
                // Disable checkboxes
                checkBox1.setEnabled(false);
                checkBox2.setEnabled(false);

                // Show toast message
                ToastUtils.showToast(getApplicationContext(), "Checkbox submission completed");

                // Prepare JSON data for sending
                JSONObject postData = preparePostData();

                // Send data to server
                sendDataToServer(postData);
            } else {
                // Show toast message if no checkbox is checked
                ToastUtils.showToast(getApplicationContext(), "Please check at least one checkbox");
            }
        });

        // Set initial checkbox state based on time
        checkTimeAndEnableCheckbox();
    }

    private JSONObject preparePostData() {
        JSONObject postData = new JSONObject();
        try {
            postData.put("hospital_id", HID);

            if (checkBox1.isChecked()) {
                postData.put("physio_morning", 1);
            }
            if (checkBox2.isChecked()) {
                postData.put("physio_evening", 1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postData;
    }

    private void sendDataToServer(JSONObject postData) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, insertURL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if ("success".equals(status)) {
                            ToastUtils.showToast(getApplicationContext(), "Data saved successfully");
                        } else {
                            ToastUtils.showToast(getApplicationContext(), "Error: " + jsonObject.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastUtils.showToast(getApplicationContext(), "Error parsing JSON response");
                    }
                },
                error -> {
                    ToastUtils.showToast(getApplicationContext(), "Error saving data: " + error.getMessage());
                    Log.e("Volley Error", error.getMessage());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                try {
                    params.put("hospital_id", postData.getString("hospital_id"));
                    if (postData.has("physio_morning")) {
                        params.put("physio_morning", String.valueOf(postData.getInt("physio_morning")));
                    }
                    if (postData.has("physio_evening")) {
                        params.put("physio_evening", String.valueOf(postData.getInt("physio_evening")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("SendDataToServer", "Params: " + params.toString());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    private void checkTimeAndEnableCheckbox() {
        Calendar c = Calendar.getInstance();
        int currentHour = c.get(Calendar.HOUR_OF_DAY);

        // Enable/disable checkboxes based on current time
        boolean isTimeBetween7to9AM = currentHour >= 8 && currentHour <= 9;
        boolean isTimeBetween1to5PM = currentHour >= 12 && currentHour <= 13;

        checkBox1.setEnabled(isTimeBetween7to9AM);
        checkBox2.setEnabled(isTimeBetween1to5PM);

        if (isTimeBetween7to9AM) {
            checkBox2.setChecked(false);
            checkBox2.setEnabled(false);
        } else if (isTimeBetween1to5PM) {
            checkBox1.setChecked(false);
            checkBox1.setEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Navigate back to patient_dashboard activity
            Intent intent3 = new Intent(this, patient_dashboard.class);
            intent3.putExtra("HID", HID);
            startActivity(intent3);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release VideoView resources when the activity is destroyed
        if (videoView != null) {
            videoView.stopPlayback();
            videoView = null;
        }
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

}
