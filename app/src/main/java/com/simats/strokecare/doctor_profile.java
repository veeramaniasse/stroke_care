package com.simats.strokecare;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.simats.strokecare.API.BASE_URL;
import static com.simats.strokecare.API.Doc_profile_URL;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class doctor_profile extends AppCompatActivity {

//    private static final String TAG = doctor_profile.class.getSimpleName();
    private TextView nameTextView, hospTextView, genderTextView, ageTextView, mobileTextView;
    private Toolbar toolbar;
    private String docId;
    private ImageView profileImageView;
    Button button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        nameTextView = findViewById(R.id.name);
        hospTextView = findViewById(R.id.hosp);
        genderTextView = findViewById(R.id.male);
        ageTextView = findViewById(R.id.age);
        mobileTextView = findViewById(R.id.mobile);

        profileImageView = findViewById(R.id.profile);

        toolbar = findViewById(R.id.tb);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.my_button_toolbar);
        }
        button1 = findViewById(R.id.done_button);
        button1.setOnClickListener(view -> {
            Intent intent1 = new Intent(doctor_profile.this, d_profile_edit.class);
            intent1.putExtra("doc_id", docId);
            intent1.putExtra("name",  nameTextView.getText().toString());
            intent1.putExtra("age", ageTextView.getText().toString());
            intent1.putExtra("gender", genderTextView.getText().toString());
            intent1.putExtra("mobile_number", mobileTextView.getText().toString());
            startActivity(intent1);
        });
        Intent intent = getIntent();
        docId = intent.getStringExtra("username");
        Log.d(TAG, "docId received from intent: " + docId);
//        Log.d(TAG, "docId before setting: " + docId);
//
//        // Assigning "D01" as the doc_id
//        docId = "D01";
//
//        Log.d(TAG, "docId after setting: " + docId);

        fetchDataFromServer();
    }
    private void fetchDataFromServer() {
        String url = Doc_profile_URL;

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");
                            if ("success".equals(status)) {
                                JSONArray dataArray = jsonResponse.getJSONArray("data");
                                if (dataArray.length() > 0) {
                                    JSONObject doctorData = dataArray.getJSONObject(0);
                                    String name = doctorData.getString("name");
                                    String gender = doctorData.getString("gender");
                                    String age = doctorData.getString("age");
                                    String mobile = doctorData.getString("contact_no");
                                    String docId = doctorData.getString("doc_id");

                                    // Load profile image using Picasso
                                    String completeImageUrl = BASE_URL + doctorData.getString("profile_image");
                                    Picasso.get().load(completeImageUrl).into(profileImageView);

                                    nameTextView.setText(name);
                                    hospTextView.setText(docId);
                                    genderTextView.setText(gender);
                                    ageTextView.setText(age);
                                    mobileTextView.setText(mobile);
                                } else {
                                    Toast.makeText(doctor_profile.this, "No data found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                String message = jsonResponse.getString("message");
                                Toast.makeText(doctor_profile.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error occurred: " + error.getMessage());
                Toast.makeText(doctor_profile.this, "Error occurred while fetching data", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("doc_id", docId);
                return params;
            }
        };

        queue.add(stringRequest);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, d_dashboard.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
