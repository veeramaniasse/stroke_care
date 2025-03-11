package com.simats.strokecare;

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

public class monitoring_physio_rec_doc extends AppCompatActivity {

    private TextView dayTextView, monthTextView, yearTextView;
    private CheckBox cbMorning, cbEvening;
    private Button doneButton;
    private Toolbar tb1;
    private String hospitalId;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_physio_rec_doc);

        // Initialize views
        dayTextView = findViewById(R.id.day);
        monthTextView = findViewById(R.id.month);
        yearTextView = findViewById(R.id.year);
        cbMorning = findViewById(R.id.cb1);
        cbEvening = findViewById(R.id.cb2);
//        doneButton = findViewById(R.id.done_button);

        tb1 = findViewById(R.id.tb);
        setSupportActionBar(tb1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.my_button_toolbar);
        }

        Intent intent = getIntent();
        hospitalId = intent.getStringExtra("hospitalId");
        String day = intent.getStringExtra("day");
        String monthName = intent.getStringExtra("monthName");
        String year = intent.getStringExtra("year");

//        doneButton.setOnClickListener(view -> {
//            Intent intent1 = new Intent(monitoring_physio_rec_doc.this, monitouring_physiotherephy.class);
//            intent1.putExtra("hospitalId", hospitalId); // Pass the hospital ID
//            startActivity(intent1);
//        });

        // Initialize Volley RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Create the request URL
        final String url = API.Doc_Physio_record_URL;

        // Create a StringRequest to send a POST request
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
                                int physioMorning = dataObject.getInt("physio_morning");
                                int physioEvening = dataObject.getInt("physio_evening");

                                // Set day, month, and year
                                dayTextView.setText(day);
                                monthTextView.setText(monthName);
                                yearTextView.setText(year);

                                // Set checkboxes based on physio_morning and physio_evening values
                                cbMorning.setChecked(physioMorning == 1);
                                cbEvening.setChecked(physioEvening == 1);

                                // Disable the checkboxes
                                cbMorning.setEnabled(false);
                                cbEvening.setEnabled(false);
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
            Intent intent1 = new Intent(this, monitouring_physiotherephy.class);
//            intent1.putExtra("hospitalId", getIntent().getStringExtra("hospitalId"));
            intent1.putExtra("hospitalId", hospitalId);
            startActivity(intent1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        // Perform any action you want before navigating back
        Intent intent = new Intent(this, monitouring_physiotherephy.class);
        intent.putExtra("hospitalId", getIntent().getStringExtra("hospitalId"));
        startActivity(intent);
        // Finish the current activity to prevent returning to it
        finish();
        // Call the super method to maintain default behavior (optional based on your needs)
        super.onBackPressed();
    }

}
