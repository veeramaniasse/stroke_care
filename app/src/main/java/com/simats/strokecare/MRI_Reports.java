package com.simats.strokecare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
public class MRI_Reports extends AppCompatActivity {

    private ImageView mriImageView;
    private Toolbar tb1;
    private String hospital_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mri_reports);

        // Initialize ImageView
        mriImageView = findViewById(R.id.mri);
        Intent intent = getIntent();
        hospital_id = intent.getStringExtra("hospitalId");
//        String hospital_id  = patientId;
        // Dummy hospital ID for testing
        Log.d("HospitalID", "Hospital ID received: " + hospital_id); // Log hospital ID received

        tb1 = findViewById(R.id.tb);
        setSupportActionBar(tb1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.my_button_toolbar);
        }

        // Create String request
        String url = API.Mri_URL; // Replace with your PHP script URL
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Parse JSON response
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if (status.equals("success")) {
                                JSONArray mriImagesArray = jsonObject.getJSONArray("mri_images");
                                if (mriImagesArray.length() > 0) {
                                    // Get the first MRI image URL
                                    String mriImageUrl = mriImagesArray.getString(0);
                                    // Load MRI image using Picasso
                                    String completeImageUrl = "http://10.0.2.2/php/" + mriImageUrl;
                                    Picasso.get().load(completeImageUrl).into(mriImageView);
                                } else {
                                    ToastUtils.showToast(getApplicationContext(), "No discharge images found");
                                }
                            } else {
                                // Handle error status
                                ToastUtils.showToast(getApplicationContext(), "Error: " + status);
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
                params.put("hospital_id", hospital_id);
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
            Intent intent1 = new Intent(this, patient_details_doctor.class);
//            intent1.putExtra("hospitalId", getIntent().getStringExtra("hospitalId"));
            intent1.putExtra("hospital_id", hospital_id);
            startActivity(intent1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        // Perform any action you want before navigating back
        Intent intent = new Intent(this, patient_details_doctor.class);
        intent.putExtra("hospital_id", getIntent().getStringExtra("hospital_id"));
        startActivity(intent);

        // Finish the current activity to prevent returning to it
        finish();

        // Call the super method to maintain default behavior (optional based on your needs)
        super.onBackPressed();
    }

}