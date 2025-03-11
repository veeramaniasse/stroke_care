package com.simats.strokecare;

import static com.simats.strokecare.API.Plogin_URL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;


public class plogin extends AppCompatActivity {
    Button btn;
    private EditText eid, epassword;
    private String username, password;
    LinearLayout parentLayout;
    private String URL = Plogin_URL;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plogin);

        eid = findViewById(R.id.txt);
        epassword = findViewById(R.id.txt1);
        btn = findViewById(R.id.button);
        parentLayout = findViewById(R.id.parent_layout);

        parentLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = eid.getText().toString().trim();
                password = epassword.getText().toString().trim();
                if (!username.isEmpty() && !password.isEmpty()) {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // Handle the response
                                    handleResponse(response);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handleError(error);
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            // Send the username and password as POST parameters
                            Map<String, String> data = new HashMap<>();
                            data.put("username", username);
                            data.put("password", password);
                            return data;
                        }
                    };

                    // Customize the retry policy
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                            60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                    // Initialize the Volley request queue and add the request
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(stringRequest);
                } else if (username.isEmpty() && !password.isEmpty()) {
                    // Display custom toast message for missing Hospital ID
                    ToastUtils.showToast(getApplicationContext(), "Hospital ID is missing");
                } else if (!username.isEmpty() && password.isEmpty()) {
                    // Display custom toast message for missing password
                    ToastUtils.showToast(getApplicationContext(), "Password is missing");
                } else {
                    // Display custom toast message for both fields missing
                    ToastUtils.showToast(getApplicationContext(), "Both fields are required");
                }
            }
        });
    }
    // Handle the JSON response
    private void handleResponse(String response) {
        Gson gson = new Gson();
        Log.d("JSON Response", response);
        JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
        String status = jsonObject.get("status").getAsString();
        Log.d("JSON Response", status);

        if ("success".equals(status)) {
            Intent intent = new Intent(plogin.this, patient_dashboard.class);
            Log.d("mes", username);
            intent.putExtra("HID", username);
            startActivity(intent);
        } else if ("failure".equals(status)) {
            // Display custom toast message for invalid login
            ToastUtils.showToast(getApplicationContext(), "Invalid login");
        }
    }

    // Handle network request errors
    // Handle network request errors
    private void handleError(VolleyError error) {
        if (error instanceof TimeoutError) {
            ToastUtils.showToast(getApplicationContext(), "Request timed out. Check your internet connection.");
        } else {
            ToastUtils.showToast(getApplicationContext(), error.toString().trim());
        }
    }
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    @Override
    public void onBackPressed() {
        // Perform any action you want before navigating back
        Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtra("hospital_id", getIntent().getStringExtra("hospitalId"));
        startActivity(intent);

        // Finish the current activity to prevent returning to it
        finish();

        // Call the super method to maintain default behavior (optional based on your needs)
        super.onBackPressed();
    }

}