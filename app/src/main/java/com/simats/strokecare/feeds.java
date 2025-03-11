package com.simats.strokecare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

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

public class feeds extends AppCompatActivity {

    private CheckBox checkBox1, checkBox2,checkBox3,checkBox4, checkBox5,checkBox6;
    private Button button;
    private Button Button1;
    private String insertURL = API.Physio_URL; // Change to your PHP script URL
    private String HID;
    private TextView text;

    private Toolbar tb1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds);

        Button1 = findViewById(R.id.his_btn);

        Button1.setOnClickListener(view -> {
            Intent intent1 = new Intent(feeds.this, feeds_patient.class);
            intent1.putExtra("HID", HID);
            startActivity(intent1);
        });

        Toolbar tb = findViewById(R.id.tb);
        setSupportActionBar(tb);

        tb1 = findViewById(R.id.tb);
        setSupportActionBar(tb1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.my_button_toolbar);
        }
        text = findViewById(R.id.text1);
        Intent intent = getIntent();
        HID = intent.getStringExtra("HID");
        if (HID == null) {
            // Hospital ID not found, handle appropriately
            Toast.makeText(this, "Hospital ID not found", Toast.LENGTH_SHORT).show();
            return;
        }
        text.setText(HID);
        checkBox1 = findViewById(R.id.checkbox1);
        checkBox2 = findViewById(R.id.checkbox2);
        checkBox3 = findViewById(R.id.checkbox3);
        checkBox4 = findViewById(R.id.checkbox4);
        checkBox5 = findViewById(R.id.checkbox5);
        checkBox6 = findViewById(R.id.checkbox6);
        button = findViewById(R.id.done_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if any checkbox is checked
                if (checkBox1.isChecked() || checkBox2.isChecked()|| checkBox3.isChecked()
                        ||checkBox4.isChecked() || checkBox5.isChecked()|| checkBox6.isChecked()) {
                    // Disable checkboxes
                    checkBox1.setEnabled(false);
                    checkBox2.setEnabled(false);
                    checkBox3.setEnabled(false);
                    checkBox4.setEnabled(false);
                    checkBox5.setEnabled(false);
                    checkBox6.setEnabled(false);

                    // Show custom toast message
                    ToastUtils.showToast(getApplicationContext(), "Checkbox submission completed");

                    // Prepare data for sending
                    JSONObject postData = preparePostData();
                    Intent intent2 = new Intent(feeds.this, patient_dashboard.class);
                    intent2.putExtra("HID", HID);
                    startActivity(intent2);

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

        checkBox5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleCheckboxClick(checkBox5);
            }
        });

        checkBox6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleCheckboxClick(checkBox6);
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
                postData.put("feeds_1", 1);
            }

            // Include 1 if checkBox2 is checked, otherwise exclude from postData
            if (checkBox2.isChecked()) {
                postData.put("feeds_2", 1);
            }
            // Include 1 if checkBox2 is checked, otherwise exclude from postData
            if (checkBox3.isChecked()) {
                postData.put("feeds_3", 1);
            }
            // Include 1 if checkBox2 is checked, otherwise exclude from postData
            if (checkBox4.isChecked()) {
                postData.put("feeds_4", 1);
            }
            // Include 1 if checkBox2 is checked, otherwise exclude from postData
            if (checkBox5.isChecked()) {
                postData.put("feeds_5", 1);
            }
            // Include 1 if checkBox2 is checked, otherwise exclude from postData
            if (checkBox6.isChecked()) {
                postData.put("feeds_6", 1);
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
            checkBox5.setChecked(false);
            checkBox6.setChecked(false);
        } else if (clickedCheckbox == checkBox2) {
            checkBox1.setChecked(false);
            checkBox3.setChecked(false);
            checkBox4.setChecked(false);
            checkBox5.setChecked(false);
            checkBox6.setChecked(false);
        } else if (clickedCheckbox == checkBox3) {
            checkBox2.setChecked(false);
            checkBox1.setChecked(false);
            checkBox4.setChecked(false);
            checkBox5.setChecked(false);
            checkBox6.setChecked(false);
        }else if (clickedCheckbox == checkBox4) {
            checkBox2.setChecked(false);
            checkBox3.setChecked(false);
            checkBox1.setChecked(false);
            checkBox5.setChecked(false);
            checkBox6.setChecked(false);
        }else if (clickedCheckbox == checkBox5) {
            checkBox2.setChecked(false);
            checkBox3.setChecked(false);
            checkBox4.setChecked(false);
            checkBox1.setChecked(false);
            checkBox6.setChecked(false);
        }else if (clickedCheckbox == checkBox6) {
            checkBox2.setChecked(false);
            checkBox3.setChecked(false);
            checkBox4.setChecked(false);
            checkBox5.setChecked(false);
            checkBox1.setChecked(false);
        }
//        if (!checkBox1.isChecked() && !checkBox2.isChecked() && !checkBox3.isChecked()) {
//            // Send notification
//            Dialog myDialog = new Dialog(feeds.this);
//            myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            myDialog.setContentView(R.layout.activity_notification_pat);
//            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            myDialog.show();
//        }
    }


    private void checkTimeAndEnableCheckbox() {
        // Get the current time
        Calendar c = Calendar.getInstance();
        int currentHour = c.get(Calendar.HOUR_OF_DAY);

        // Check if the current time is between 7 am and 9 am
        boolean isTimeBetween8to9AM = currentHour >= 8 && currentHour <= 9;

        // Check if the current time is between 3 pm and 5 pm
        boolean isTimeBetween12to1PM = currentHour >= 11 && currentHour <= 12;

        boolean isTimeBetween7to8PM = currentHour >= 13 && currentHour <= 14;
        boolean isTimeBetween9to10AM = currentHour >= 16 && currentHour <= 17;

        // Check if the current time is between 3 pm and 5 pm
        boolean isTimeBetween11to12PM = currentHour >= 18 && currentHour <= 19;

        boolean isTimeBetween8to9PM = currentHour >= 21 && currentHour <= 22;

        // Enable the appropriate checkbox based on the time condition
        checkBox1.setEnabled(isTimeBetween8to9AM);
        checkBox2.setEnabled(isTimeBetween12to1PM);
        checkBox3.setEnabled(isTimeBetween7to8PM);
        checkBox4.setEnabled(isTimeBetween9to10AM);
        checkBox5.setEnabled(isTimeBetween11to12PM);
        checkBox6.setEnabled(isTimeBetween8to9PM);

        // Uncheck and disable the other checkbox
        if (isTimeBetween8to9AM) {
            checkBox2.setChecked(false);
            checkBox2.setEnabled(false);
            checkBox3.setChecked(false);
            checkBox3.setEnabled(false);
            checkBox4.setChecked(false);
            checkBox4.setEnabled(false);
            checkBox5.setChecked(false);
            checkBox5.setEnabled(false);
            checkBox6.setChecked(false);
            checkBox6.setEnabled(false);
        } else if (isTimeBetween12to1PM) {
            checkBox1.setChecked(false);
            checkBox1.setEnabled(false);
            checkBox3.setChecked(false);
            checkBox3.setEnabled(false);
            checkBox4.setChecked(false);
            checkBox4.setEnabled(false);
            checkBox5.setChecked(false);
            checkBox5.setEnabled(false);
            checkBox6.setChecked(false);
            checkBox6.setEnabled(false);
        } else if (isTimeBetween7to8PM) {
            checkBox2.setChecked(false);
            checkBox2.setEnabled(false);
            checkBox1.setChecked(false);
            checkBox1.setEnabled(false);
            checkBox4.setChecked(false);
            checkBox4.setEnabled(false);
            checkBox5.setChecked(false);
            checkBox5.setEnabled(false);
            checkBox6.setChecked(false);
            checkBox6.setEnabled(false);
        } else if (isTimeBetween9to10AM) {
            checkBox2.setChecked(false);
            checkBox2.setEnabled(false);
            checkBox1.setChecked(false);
            checkBox1.setEnabled(false);
            checkBox3.setChecked(false);
            checkBox3.setEnabled(false);
            checkBox5.setChecked(false);
            checkBox5.setEnabled(false);
            checkBox6.setChecked(false);
            checkBox6.setEnabled(false);
        } else if (isTimeBetween11to12PM) {
            checkBox2.setChecked(false);
            checkBox2.setEnabled(false);
            checkBox1.setChecked(false);
            checkBox1.setEnabled(false);
            checkBox4.setChecked(false);
            checkBox4.setEnabled(false);
            checkBox6.setChecked(false);
            checkBox6.setEnabled(false);
            checkBox3.setChecked(false);
            checkBox3.setEnabled(false);
        } else if (isTimeBetween8to9PM) {
            checkBox2.setChecked(false);
            checkBox2.setEnabled(false);
            checkBox1.setChecked(false);
            checkBox1.setEnabled(false);
            checkBox4.setChecked(false);
            checkBox4.setEnabled(false);
            checkBox5.setChecked(false);
            checkBox5.setEnabled(false);
            checkBox3.setChecked(false);
            checkBox3.setEnabled(false);
        }
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
                    if (postData.has("feeds_1")) {
                        params.put("feeds_1", String.valueOf(postData.getInt("feeds_1")));
                    }
                    if (postData.has("feeds_2")) {
                        params.put("feeds_2", String.valueOf(postData.getInt("feeds_2")));
                    }
                    if (postData.has("feeds_3")) {
                        params.put("feeds_3", String.valueOf(postData.getInt("feeds_3")));
                    }
                    if (postData.has("feeds_4")) {
                        params.put("feeds_4", String.valueOf(postData.getInt("feeds_4")));
                    }
                    if (postData.has("feeds_5")) {
                        params.put("feeds_5", String.valueOf(postData.getInt("feeds_5")));
                    }
                    if (postData.has("feeds_6")) {
                        params.put("feeds_6", String.valueOf(postData.getInt("feeds_6")));
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

}