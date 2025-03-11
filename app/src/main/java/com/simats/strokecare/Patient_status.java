package com.simats.strokecare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Patient_status extends AppCompatActivity {

    private EditText rulEditText, lulEditText, rllEditText, lllEditText;
    private Button doneButton,his;
    private TextView text;
    private boolean editTextTouched = false;
    private String hospitalId;
    LinearLayout parentLayout;
    private Toolbar tb1;
    // Replace with your actual server URL
    private static final String API_URL = API.Doc_Patient_status_new_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_status);

        parentLayout = findViewById(R.id.parentLayout);

        parentLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });

        tb1 = findViewById(R.id.tb1);
        setSupportActionBar(tb1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.my_button_toolbar);
        }
        text = findViewById(R.id.text1);
        rulEditText = findViewById(R.id.rul);
        lulEditText = findViewById(R.id.lul);
        rllEditText = findViewById(R.id.rll);
        lllEditText = findViewById(R.id.lll);

        rulEditText.setOnFocusChangeListener((v, hasFocus) -> {
            editTextTouched = true;
        });

        lulEditText.setOnFocusChangeListener((v, hasFocus) -> {
            editTextTouched = true;
        });

        rllEditText.setOnFocusChangeListener((v, hasFocus) -> {
            editTextTouched = true;
        });

        lllEditText.setOnFocusChangeListener((v, hasFocus) -> {
            editTextTouched = true;
        });

        // Retrieve hospital ID from intent extras
        Intent intent = getIntent();
        hospitalId = intent.getStringExtra("hospitalId");
        if (hospitalId == null) {
            // Hospital ID not found, handle appropriately
            ToastUtils.showToast(getApplicationContext(), "Hospital ID not found");
            return;
        }
        text.setText(hospitalId);

        doneButton = findViewById(R.id.done);
        doneButton.setOnClickListener(view -> {
            // Check if all EditText fields are filled
            if (rulEditText.getText().toString().isEmpty() ||
                    lulEditText.getText().toString().isEmpty() ||
                    rllEditText.getText().toString().isEmpty() ||
                    lllEditText.getText().toString().isEmpty()) {
                ToastUtils.showToast(getApplicationContext(), "Please fill in all fields");
            } else {
                // All fields are filled, proceed to save data and move to next activity
                insertPatientStatus(hospitalId);
                Intent intent1 = new Intent(Patient_status.this, d_dashboard.class);
                intent1.putExtra("HospitalId", hospitalId);
                startActivity(intent1);
            }
        });

        his = findViewById(R.id.history);
        his.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Insert patient status
                insertPatientStatus(hospitalId);

                // Start the next activity
                Intent intent = new Intent(Patient_status.this, Patient_status_history.class);
                intent.putExtra("HospitalId", hospitalId);
                startActivity(intent);
            }
        });
    }

    private void insertPatientStatus(String hospitalId) {
        String rulStatus = rulEditText.getText().toString().trim();
        String lulStatus = lulEditText.getText().toString().trim();
        String rllStatus = rllEditText.getText().toString().trim();
        String lllStatus = lllEditText.getText().toString().trim();

        // Create a JSON object to hold the data
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("hospital_id", hospitalId);
            jsonObject.put("rul", rulStatus);
            jsonObject.put("lul", lulStatus);
            jsonObject.put("rll", rllStatus);
            jsonObject.put("lll", lllStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");
                            String message = jsonResponse.getString("message");

                            if (status.equals("success")) {
                                // Data saved successfully
                                ToastUtils.showToast(getApplicationContext(), message);
                            } else {
                                // Error occurred while saving data
                                ToastUtils.showToast(getApplicationContext(), message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Error parsing JSON response
                            ToastUtils.showToast(getApplicationContext(), "Error parsing JSON response");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                ToastUtils.showToast(getApplicationContext(), "Error saving data: " + error.getMessage());
                Log.e("Volley Error", error.getMessage());
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                // Convert JSON object to byte array
                return jsonObject.toString().getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Set content type to JSON
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle toolbar back button click
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Check if any EditText has been touched
        if (editTextTouched) {
            // At least one EditText has been touched, allow normal back behavior
            super.onBackPressed();
        } else {
            // No EditText has been touched, navigate to patient_details_doctor activity
            // Here, you should replace Patient_details_doctorActivity.class with your actual activity class name
            Intent intent2 = new Intent(this, patient_details_doctor.class);
            intent2.putExtra("hospital_id", hospitalId);
            startActivity(intent2);
            // Finish current activity
            finish();
        }

    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
