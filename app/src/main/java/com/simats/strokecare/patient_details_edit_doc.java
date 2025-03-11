package com.simats.strokecare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class patient_details_edit_doc extends AppCompatActivity {

    private static final String TAG = "PatientDetailsEditDoc";

    private String patient_id; // Store patient_id here
    private EditText editName, editAge, editGender, editMobile, editDiagnosis;
    private Button updateButton;
    private TextView hosp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details_edit_doc);

        // Initialize UI elements
        hosp = findViewById(R.id.edit2);
        editName = findViewById(R.id.edit1);
        editAge = findViewById(R.id.edit3);
        editGender = findViewById(R.id.edit4);
        editMobile = findViewById(R.id.edit5);
        editDiagnosis = findViewById(R.id.edit6);

        Toolbar tb = findViewById(R.id.tb);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.my_button_toolbar);
        }

        updateButton = findViewById(R.id.save);

        // Get patient details from intent and populate UI fields
        Intent intent = getIntent();
        if (intent != null) {
            patient_id = intent.getStringExtra("hospitalId"); // Store patient_id here
            hosp.setText(patient_id); // Set hospital ID in TextView
            editName.setText(intent.getStringExtra("name"));
            editAge.setText(intent.getStringExtra("age"));
            editGender.setText(intent.getStringExtra("gender"));
            editMobile.setText(intent.getStringExtra("mobile_number"));
            editDiagnosis.setText(intent.getStringExtra("diagnosis"));
        } else {
            ToastUtils.showToast(getApplicationContext(), "No data passed!");
        }

        // Set click listener for update button
        updateButton.setOnClickListener(v -> updatePatientDetails());
    }

    // Update patient details on server
    private void updatePatientDetails() {
        // Perform validation
        if (editName.getText().toString().isEmpty() || editAge.getText().toString().isEmpty() ||
                editGender.getText().toString().isEmpty() || editMobile.getText().toString().isEmpty() ||
                editDiagnosis.getText().toString().isEmpty()) {
            ToastUtils.showToast(getApplicationContext(), "Please fill in all fields");
            return;
        }

        // Create a Map to hold the parameters
        Map<String, String> params = new HashMap<>();
        params.put("hospital_id", patient_id);
        params.put("name", editName.getText().toString());
        params.put("age", editAge.getText().toString());
        params.put("gender", editGender.getText().toString());
        params.put("diagnosis", editDiagnosis.getText().toString());
        params.put("mobile_number", editMobile.getText().toString());

// Make a POST request using Volley
        String url = API.Edit_Patient_Details_URL;
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");
                            String message = jsonResponse.getString("message");
                            if (status.equals("success")) {
                                ToastUtils.showToast(getApplicationContext(), "Patient details updated successfully");
                                // Navigate to patient_details_doctor activity
                                Intent intent = new Intent(patient_details_edit_doc.this, patient_details_doctor.class);
                                intent.putExtra("hospital_id", patient_id);
                                startActivity(intent);
                                finish(); // Finish the current activity
                            } else {
                                ToastUtils.showToast(getApplicationContext(), "Failed to update patient details: " + message);
                                Log.e(TAG, "Failed to update patient details: " + message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error parsing JSON response: " + e.getMessage());
                            ToastUtils.showToast(getApplicationContext(), "Error parsing JSON response");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Volley Error: " + error.getMessage());
                ToastUtils.showToast(getApplicationContext(), "Error: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };

        queue.add(request);
    }

//        @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            onBackPressed();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//    @Override
//    public void onBackPressed() {
//        // Perform any action you want before navigating back
//        Intent intent = new Intent(this, patient_details_doctor.class);
//        intent.putExtra("hospital_id", getIntent().getStringExtra("patient_id"));
//        startActivity(intent);
//
//        // Finish the current activity to prevent returning to it
//        finish();
//
//        // Call the super method to maintain default behavior (optional based on your needs)
//        super.onBackPressed();
//    }

}
