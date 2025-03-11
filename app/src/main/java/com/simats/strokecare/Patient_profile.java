package com.simats.strokecare;

import static com.simats.strokecare.API.BASE_URL;
import static com.simats.strokecare.API.Patient_profile_URL;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Patient_profile extends AppCompatActivity {

    private TextView nameTextView, hospTextView, ageTextView, genderTextView, mobileTextView, diagnosisTextView;
    private ImageView profileImageView;
    private RequestQueue requestQueue;
    private Button button;
    private Toolbar tb;
    private String HID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);

        // Initialize TextViews and ImageView
        nameTextView = findViewById(R.id.name);
        hospTextView = findViewById(R.id.hospid);
        ageTextView = findViewById(R.id.age);
        genderTextView = findViewById(R.id.gender);
        mobileTextView = findViewById(R.id.mobilenumber);
        diagnosisTextView = findViewById(R.id.dis);
        profileImageView = findViewById(R.id.patient_profile);
//        button = findViewById(R.id.done);

        tb = findViewById(R.id.tb);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.my_button_toolbar);
        }
//        button.setOnClickListener(view -> {
//            Intent intent1 = new Intent(Patient_profile.this, patient_dashboard.class);
//            intent1.putExtra("HID", HID);
//            startActivity(intent1);
//        });

        // Initialize Volley RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        Intent intent1 = getIntent();
        HID = intent1.getStringExtra("HID");
        // Dummy hospital ID for testing
      // String hospital_id = "P00098";

        // Create String request
        String url = Patient_profile_URL; // Replace with your PHP script URL
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("patients");
                            if (jsonArray.length() > 0) {
                                JSONObject patient = jsonArray.getJSONObject(0); // Assuming only one patient is returned
                                // Populate TextViews with patient data
                                nameTextView.setText(patient.getString("name"));
                                hospTextView.setText(patient.getString("hospital_id"));
                                ageTextView.setText(patient.getString("age"));
                                genderTextView.setText(patient.getString("gender"));
                                mobileTextView.setText(patient.getString("mobile_number"));
                                diagnosisTextView.setText(patient.getString("diagnosis"));

                                // Load profile image using Picasso
                                String completeImageUrl = BASE_URL + patient.getString("profile_image");
                                Picasso.get().load(completeImageUrl).into(profileImageView);
                            } else {
                                ToastUtils.showToast(getApplicationContext(), "No data found");
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
                // Send hospital_id to PHP script
                Map<String, String> params = new HashMap<>();
                params.put("hospital_id", HID);
                return params;
            }
        };

        // Customize the retry policy
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);

        // Set up the toolbar
        Toolbar tb = findViewById(R.id.tb);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle toolbar item clicks
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate back to patient_dashboard activity
                Intent intent2 = new Intent(this, patient_dashboard.class);
                intent2.putExtra("HID", HID);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
