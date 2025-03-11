package com.simats.strokecare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class patient_details_doctor extends AppCompatActivity {

    private TextView nameTextView, hospTextView, ageTextView, genderTextView, mobileTextView, diagnosisTextView;
    private ImageView profileImageView;
    private RequestQueue requestQueue;
    private Toolbar tb;
private Button button1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details_doctor);

        // Initialize TextViews and ImageView
        nameTextView = findViewById(R.id.name);
        hospTextView = findViewById(R.id.hosp);
        ageTextView = findViewById(R.id.age);
        genderTextView = findViewById(R.id.gender);
        mobileTextView = findViewById(R.id.mobile);
        diagnosisTextView = findViewById(R.id.diagnosis);
        profileImageView = findViewById(R.id.profile);


        button1 = findViewById(R.id.edit);
    button1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Get the hospital ID from the intent extras

            // Start the overall_history activity
            Intent intent2 = new Intent(patient_details_doctor.this, patient_details_edit_doc.class);
            intent2.putExtra("hospitalId",hospTextView.getText().toString()); // Pass the hospital ID to the next activity
            intent2.putExtra("name",  nameTextView.getText().toString());
            intent2.putExtra("age", ageTextView.getText().toString());
            intent2.putExtra("gender", genderTextView.getText().toString());
            intent2.putExtra("mobile_number", mobileTextView.getText().toString());
            intent2.putExtra("diagnosis", diagnosisTextView.getText().toString());
            startActivity(intent2);
        }
    });


        tb = findViewById(R.id.tb);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        tb.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.my_button_toolbar);
        }

        Intent intent=getIntent();
        String hospitalId=intent.getStringExtra("hospital_id");


        // Initialize Volley RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Dummy hospital ID for testing
        String hospital_id = hospitalId;

        // Create String request
        String url = API.Patient_Details_URL; // Replace with your PHP script URL
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
                                String completeImageUrl = API.BASE_URL + patient.getString("profile_image");
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
                params.put("hospital_id", hospital_id);
                return params;
            }
        };

        // Customize the retry policy
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popup_menu_doctor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent=getIntent();
        String hospitalId=intent.getStringExtra("hospital_id");
        int id = item.getItemId();
        if (id == R.id.more) {
            // Handle click on "More" menu item
            Toast.makeText(this, "More clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.id1) {
            Intent intent1 = new Intent(patient_details_doctor.this, patient_performance.class);
            intent1.putExtra("hospitalId", hospitalId);
            startActivity(intent1);
            return true;
        } else if (id == R.id.id2) {
            Intent intent2 = new Intent(this, monitouring_physiotherephy.class);
            intent2.putExtra("hospitalId", hospitalId);
            startActivity(intent2);
            return true;
        } else if (id == R.id.id3) {
            Intent intent3= new Intent(this, monitoring_b_p_doc.class);
            intent3.putExtra("hospitalId", hospitalId);
            startActivity(intent3);
            return true;
        } else if (id == R.id.id4) {
            Intent intent4 = new Intent(this, monitoring_b_s_doc.class);
            intent4.putExtra("hospitalId", hospitalId);
            startActivity(intent4);
            return true;
        } else if (id == R.id.id5) {
            Intent intent5 = new Intent(this, Foleys_and_Ryles_doc.class);
            intent5.putExtra("hospitalId", hospitalId);
            startActivity(intent5);
            return true;
        } else if (id == R.id.id6) {
            Intent intent6 = new Intent(this, Patient_status.class);
            intent6.putExtra("hospitalId", hospitalId);
            startActivity(intent6);
            return true;
        } else if (id == R.id.id7) {
            Intent intent7 = new Intent(this, track_patient.class);
            intent7.putExtra("hospitalId", hospitalId);
            startActivity(intent7);
            return true;
        } else if (id == R.id.id8) {
            Intent intent8 = new Intent(this, MRI_Reports.class);
            intent8.putExtra("hospitalId", hospitalId);
            startActivity(intent8);
            return true;
        }else if (id == android.R.id.home) {
            // Navigate back to d_dashboard activity
            Intent intent9 = new Intent(this, d_dashboard.class);
            intent9.putExtra("hospitalId", hospitalId);
            startActivity(intent9);
            finish(); // Finish the current activity
            return true;
        }
        return super.onOptionsItemSelected(item);


    }


//    @Override
//    public onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            // Navigate back to d_dashboard activity
//            Intent intent = new Intent(this, d_dashboard.class);
//            startActivity(intent);
//            finish(); // Finish the current activity
//            return true;
//        } else {
//            return super.onOptionsItemSelected(item);
//        }
//    }
@Override
public void onBackPressed() {
    // Perform any action you want before navigating back
    Intent intent = new Intent(this, d_dashboard.class);
//    intent.putExtra("hospitalId", getIntent().getStringExtra("hospital_id"));
    startActivity(intent);

    // Finish the current activity to prevent returning to it
    finish();

    // Call the super method to maintain default behavior (optional based on your needs)
    super.onBackPressed();
}
}
