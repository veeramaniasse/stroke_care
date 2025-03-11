package com.simats.strokecare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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

public class b_p_doc_record extends AppCompatActivity {

    private TextView dayTextView, monthTextView, yearTextView;
    private CheckBox cb1, cb2,cb3, cb4;
    private Toolbar tb1;
    private String hospitalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bp_doc_record);

        // Initialize views
        dayTextView = findViewById(R.id.day);
        monthTextView = findViewById(R.id.month);
        yearTextView = findViewById(R.id.year);
        cb1 = findViewById(R.id.cb1);
        cb2 = findViewById(R.id.cb2);
        cb3 = findViewById(R.id.cb3);
        cb4 = findViewById(R.id.cb4);

        Intent intent = getIntent();
        hospitalId = intent.getStringExtra("hospitalId");
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

        // Initialize Volley RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Create the request URL
        final String url = API.Doc_Positions_record_URL;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");
                            String message = jsonResponse.getString("message");

                            // Check if the status is success
                            if (status.equals("success")) {
                                JSONArray dataArray = jsonResponse.getJSONArray("data");
                                JSONObject dataObject = dataArray.getJSONObject(0);
                                int bedpositions1 = dataObject.getInt("bedpositions_1");
                                        int bedpositions2 = dataObject.getInt("bedpositions_2");
                                        int bedpositions3 = dataObject.getInt("bedpositions_3");
                                        int bedpositions4 = dataObject.getInt("bedpositions_4");

                                // Set day, month, and year
                                dayTextView.setText(day);
                                monthTextView.setText(monthName);
                                yearTextView.setText(year);

                                // Set checkboxes based on physio_morning and physio_evening values
                                cb1.setChecked(bedpositions1 == 1);
                                        cb2.setChecked(bedpositions2 == 1);
                                        cb3.setChecked(bedpositions3 == 1);
                                        cb4.setChecked(bedpositions4 == 1);

                                // Disable the checkboxes
                                cb1.setEnabled(false);
                                        cb2.setEnabled(false);
                                        cb3.setEnabled(false);
                                        cb4.setEnabled(false);
                            } else {
                                // Display error message
                                ToastUtils.showToast(getApplicationContext(), message);
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
                params.put("hospital_id", hospitalId);
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
            Intent intent1 = new Intent(this, monitoring_b_p_doc.class);
            intent1.putExtra("hospitalId", hospitalId);
            startActivity(intent1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        // Perform any action you want before navigating back
        Intent intent = new Intent(this, monitoring_b_p_doc.class);
        intent.putExtra("hospitalId", getIntent().getStringExtra("hospitalId"));
        startActivity(intent);

        // Finish the current activity to prevent returning to it
        finish();

        // Call the super method to maintain default behavior (optional based on your needs)
        super.onBackPressed();
    }

}
