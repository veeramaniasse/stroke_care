package com.simats.strokecare;

import static com.simats.strokecare.API.Feeds_Patient_record_URL;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class feeds_history extends AppCompatActivity {

    private TextView dayTextView, monthTextView, yearTextView;
    private CheckBox cbMorning, cbEvening,cb3,cb4,cb5,cb6;
    private Button doneButton;
    private RequestQueue requestQueue;
    private Toolbar tb1;
    private String HID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds_history);

        // Initialize views
        dayTextView = findViewById(R.id.day);
        monthTextView = findViewById(R.id.month);
        yearTextView = findViewById(R.id.year);
        cbMorning = findViewById(R.id.cb1);
        cbEvening = findViewById(R.id.cb2);
        cb3 = findViewById(R.id.cb3);
        cb4 = findViewById(R.id.cb4);
        cb5 = findViewById(R.id.cb5);
        cb6 = findViewById(R.id.cb6);
//        doneButton = findViewById(R.id.done_button);


        Intent intent = getIntent();
         HID = intent.getStringExtra("HID");
        String day = intent.getStringExtra("day");
        String monthName = intent.getStringExtra("monthName");
        String year = intent.getStringExtra("year");

        tb1 = findViewById(R.id.tb);
        setSupportActionBar(tb1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.my_button_toolbar);
        }
//        doneButton.setOnClickListener(view -> {
//            Intent intent1 = new Intent(feeds_history.this, patient_dashboard.class);
//            intent1.putExtra("HID", HID); // Pass the hospital ID
//            startActivity(intent1);
//        });

        // Initialize Volley RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Dummy hospital ID, day, month_name, and year for testing
//        final String hospitalId = "P2322";
//        final String day = "13";
//        final String monthName = "February";
//        final String year = "2024";

        // Create the request URL
        final String url = Feeds_Patient_record_URL;

        // Create a StringRequest to send a POST request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Parse JSON response
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            // Check if response contains "status" key and if it equals "success"
                            if(jsonObject.has("status") && jsonObject.getString("status").equals("success")) {
                                JSONArray dataArray = jsonObject.getJSONArray("data");
                                if(dataArray.length() > 0) {
                                    JSONObject data = dataArray.getJSONObject(0);
                                    // Set day, month, and year
                                    dayTextView.setText(day);
                                    monthTextView.setText(monthName);
                                    yearTextView.setText(year);

                                    // Set checkboxes based on feeds data
                                    int feeds1 = data.optInt("feeds_1", 0);
                                    int feeds2 = data.optInt("feeds_2", 0);
                                    int feeds3 = data.optInt("feeds_3", 0);
                                    int feeds4 = data.optInt("feeds_4", 0);
                                    int feeds5 = data.optInt("feeds_5", 0);
                                    int feeds6 = data.optInt("feeds_6", 0);

                                    cbMorning.setChecked(feeds1 == 1);
                                    cbEvening.setChecked(feeds2 == 1);
                                    cb3.setChecked(feeds3 == 1);
                                    cb4.setChecked(feeds4 == 1);
                                    cb5.setChecked(feeds5 == 1);
                                    cb6.setChecked(feeds6 == 1);

                                    // Disable the checkboxes
                                    cbMorning.setEnabled(false);
                                    cbEvening.setEnabled(false);
                                    cb3.setEnabled(false);
                                    cb4.setEnabled(false);
                                    cb5.setEnabled(false);
                                    cb6.setEnabled(false);
                                } else {
                                    // Handle empty data array
                                    ToastUtils.showToast(getApplicationContext(), "No patient records found");
                                }
                            } else {
                                // Handle error response from server
                                ToastUtils.showToast(getApplicationContext(), "Error: " + jsonObject.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtils.showToast(getApplicationContext(), "Error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                        ToastUtils.showToast(getApplicationContext(), "Volley Error: " + error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Add parameters to the request
                Map<String, String> params = new HashMap<>();
                params.put("hospital_id", HID);
                params.put("day", day);
                params.put("month_name", monthName);
                params.put("year", year);
                return params;
            }
        };

        // Add the request to the RequestQueue
        requestQueue.add(stringRequest);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Navigate back to Patient_status activity
            Intent intent1 = new Intent(this, feeds_patient.class);
//            intent1.putExtra("hospitalId", getIntent().getStringExtra("hospitalId"));
            intent1.putExtra("HID", HID);
            startActivity(intent1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        // Perform any action you want before navigating back
        Intent intent = new Intent(this, feeds_patient.class);
        intent.putExtra("HID", getIntent().getStringExtra("HID"));
        startActivity(intent);

        // Finish the current activity to prevent returning to it
        finish();

        // Call the super method to maintain default behavior (optional based on your needs)
        super.onBackPressed();
    }

}
