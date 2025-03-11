package com.simats.strokecare;

import static com.simats.strokecare.API.Physio_URL;
import static com.simats.strokecare.API.Vedio_URL_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class bed_change_positions extends AppCompatActivity {

    private CheckBox checkBox1, checkBox2,checkBox3,checkBox4;
    private Button button;
    private Button Button1;
    private String insertURL = Physio_URL; // Change to your PHP script URL
    private String HID;
    private VideoView videoView;

    private TextView text;
    private Toolbar tb1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bed_change_positions);

        videoView = findViewById(R.id.bedv);

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
        String videoPath = Vedio_URL_2;

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
                Toast.makeText(bed_change_positions.this, "Error loading video", Toast.LENGTH_SHORT).show();
                Log.e("VideoView Error", "Error code: " + what + ", Extra: " + extra);
                return true; // Returning true indicates that the error is handled
            });
        } else {
            // Handle case where video URL is invalid or null
            Toast.makeText(this, "Video URL is not available", Toast.LENGTH_SHORT).show();
            Log.e("SurfaceView Issue", "Vedio_URL is null or invalid");
        }

        // Start playing the video
//        videoView.start();
        Button1 = findViewById(R.id.his_btn);

        Button1.setOnClickListener(view -> {
            Intent intent1 = new Intent(bed_change_positions.this, bed_change_positions_overall_history.class);
            intent1.putExtra("HID", HID);
            startActivity(intent1);
        });

        Toolbar tb = findViewById(R.id.tb);
        setSupportActionBar(tb);
//
//        Intent intent = getIntent();
//        hospitalId = intent.getStringExtra("hospitalId");

        // If hospitalId is null, set a default value
//        if (hospitalId == null || hospitalId.isEmpty()) {
//          //  hospitalId = "13532"; // Default value
//        }

        checkBox1 = findViewById(R.id.checkbox1);
        checkBox2 = findViewById(R.id.checkbox2);
        checkBox3 = findViewById(R.id.checkbox3);
        checkBox4 = findViewById(R.id.checkbox4);
        button = findViewById(R.id.done_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if any checkbox is checked
                if (checkBox1.isChecked() || checkBox2.isChecked() || checkBox3.isChecked() || checkBox4.isChecked()) {
                    // Disable checkboxes
                    checkBox1.setEnabled(false);
                    checkBox2.setEnabled(false);
                    checkBox3.setEnabled(false);
                    checkBox4.setEnabled(false);

                    // Show custom toast message
                    ToastUtils.showToast(getApplicationContext(), "Checkbox submission completed");

                    // Prepare data for sending
                    JSONObject postData = preparePostData();
                    Intent intent2 = new Intent(bed_change_positions.this, patient_dashboard.class);
                    intent2.putExtra("HID", HID);
                    startActivity(intent2);

                    // Send data to PHP script
                    sendDataToServer(postData);
                } else {
                    // Show custom toast message if no checkbox is checked
                    ToastUtils.showToast(getApplicationContext(), "Please check at least one checkbox");
                }
            }
        });
//
//        // Check if checkboxes 1, 2, 3, and 4 are not checked
//        if (!checkBox1.isChecked() || !checkBox2.isChecked() || !checkBox3.isChecked() || !checkBox4.isChecked()) {
//            // Send notification
//            Dialog myDialog = new Dialog(bed_change_positions.this);
//            myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            myDialog.setContentView(R.layout.activity_notification_pat);
//            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            myDialog.show();
//        }

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

        checkBox3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleCheckboxClick(checkBox3);
            }
        });

        checkBox4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleCheckboxClick(checkBox4);
            }
        });

        // Initial check for time and checkbox enabling
        checkTimeAndEnableCheckbox();
    }
//    private JSONObject preparePostData() {
//        JSONObject postData = new JSONObject();
//        try {
//            postData.put("hospital_id", HID);
//
//            // Include 1 if checkBox1 is checked, otherwise 0
//            postData.put("bedpositions_1", checkBox1.isChecked() ? 1 : 0);
//
//            // Include 1 if checkBox2 is checked, otherwise 0
//            postData.put("bedpositions_2", checkBox2.isChecked() ? 1 : 0);
//
//            // Include 1 if checkBox3 is checked, otherwise 0
//            postData.put("bedpositions_3", checkBox3.isChecked() ? 1 : 0);
//
//            // Include 1 if checkBox4 is checked, otherwise 0
//            postData.put("bedpositions_4", checkBox4.isChecked() ? 1 : 0);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return postData;
//    }
private JSONObject preparePostData() {
    JSONObject postData = new JSONObject();
    try {
        postData.put("hospital_id", HID);

        // Include 1 if checkBox1 is checked, otherwise exclude from postData
        if (checkBox1.isChecked()) {
            postData.put("bedpositions_1", 1);
        }

        // Include 1 if checkBox2 is checked, otherwise exclude from postData
        if (checkBox2.isChecked()) {
            postData.put("bedpositions_2", 1);
        }
        // Include 1 if checkBox2 is checked, otherwise exclude from postData
        if (checkBox3.isChecked()) {
            postData.put("bedpositions_3", 1);
        }
        // Include 1 if checkBox2 is checked, otherwise exclude from postData
        if (checkBox4.isChecked()) {
            postData.put("bedpositions_4", 1);
        }
    } catch (JSONException e) {
        e.printStackTrace();
    }
    return postData;
}



    private void handleCheckboxClick(CheckBox clickedCheckbox) {
        // Uncheck the other checkboxes based on which one was clicked
        if (clickedCheckbox == checkBox1) {
            checkBox2.setChecked(false);
            checkBox3.setChecked(false);
            checkBox4.setChecked(false);
        } else if (clickedCheckbox == checkBox2) {
            checkBox1.setChecked(false);
            checkBox3.setChecked(false);
            checkBox4.setChecked(false);
        } else if (clickedCheckbox == checkBox3) {
            checkBox1.setChecked(false);
            checkBox2.setChecked(false);
            checkBox4.setChecked(false);
        } else if (clickedCheckbox == checkBox4) {
            checkBox1.setChecked(false);
            checkBox2.setChecked(false);
            checkBox3.setChecked(false);
        }
    }


    private void checkTimeAndEnableCheckbox() {
        // Get the current time
        Calendar c = Calendar.getInstance();
        int currentHour = c.get(Calendar.HOUR_OF_DAY);

        // Check if the current time is between 7 am and 9 am
        boolean isTimeBetween9to10AM = currentHour >= 9 && currentHour <= 10;

        // Check if the current time is between 3 pm and 5 pm
        boolean isTimeBetween1to2PM = currentHour >= 13 && currentHour <= 14;

        boolean isTimeBetween5to6PM = currentHour >= 17 && currentHour <= 18;

        boolean isTimeBetween9to10PM = currentHour >= 21 && currentHour <= 22;

        // Enable the appropriate checkbox based on the time condition
        checkBox1.setEnabled(isTimeBetween9to10AM);
        checkBox2.setEnabled(isTimeBetween1to2PM);
        checkBox3.setEnabled(isTimeBetween5to6PM);
        checkBox4.setEnabled(isTimeBetween9to10PM);

        // Uncheck and disable the other checkbox
        if (isTimeBetween9to10AM) {
            checkBox2.setChecked(false);
            checkBox2.setEnabled(false);
            checkBox3.setChecked(false);
            checkBox3.setEnabled(false);
            checkBox4.setChecked(false);
            checkBox4.setEnabled(false);
        } else if (isTimeBetween1to2PM) {
            checkBox1.setChecked(false);
            checkBox1.setEnabled(false);
            checkBox3.setChecked(false);
            checkBox3.setEnabled(false);
            checkBox4.setChecked(false);
            checkBox4.setEnabled(false);

        } else if (isTimeBetween5to6PM) {
            checkBox2.setChecked(false);
            checkBox2.setEnabled(false);
            checkBox1.setChecked(false);
            checkBox1.setEnabled(false);
            checkBox4.setChecked(false);
            checkBox4.setEnabled(false);
        }else if (isTimeBetween9to10PM) {
            checkBox2.setChecked(false);
            checkBox2.setEnabled(false);
            checkBox3.setChecked(false);
            checkBox3.setEnabled(false);
            checkBox1.setChecked(false);
            checkBox1.setEnabled(false);
        }
//        // Check if checkboxes 1, 2, and 3 are not checked
//        if (!checkBox1.isChecked() && !checkBox2.isChecked() && !checkBox3.isChecked()&& !checkBox4.isChecked()) {
//            // Send notification
//            Dialog myDialog = new Dialog(bed_change_positions.this);
//            myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            myDialog.setContentView(R.layout.activity_notification_pat);
//            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            myDialog.show();
//        }
    }

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
                    if (postData.has("bedpositions_1")) {
                        params.put("bedpositions_1", String.valueOf(postData.getInt("bedpositions_1")));
                    }
                    if (postData.has("bedpositions_2")) {
                        params.put("bedpositions_2", String.valueOf(postData.getInt("bedpositions_2")));
                    }
                    if (postData.has("bedpositions_3")) {
                        params.put("bedpositions_3", String.valueOf(postData.getInt("bedpositions_3")));
                    }
                    if (postData.has("bedpositions_4")) {
                        params.put("bedpositions_4", String.valueOf(postData.getInt("bedpositions_4")));
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
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
//    }

    // Method to show toast message
    private void showToast(String message) {
        ToastUtils.showToast(getApplicationContext(), message);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Navigate back to Patient_status activity
            Intent intent3 = new Intent(this, patient_dashboard.class);
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

    @Override
    protected void onPause() {
        super.onPause();

        // Store the checkbox states in SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("checkbox3", checkBox1.isChecked());
        editor.putBoolean("checkbox4", checkBox2.isChecked());
        editor.putBoolean("checkbox5", checkBox3.isChecked());
        editor.putBoolean("checkbox6", checkBox4.isChecked());
        editor.apply();
    }


}