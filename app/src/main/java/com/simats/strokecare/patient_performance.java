package com.simats.strokecare;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class patient_performance extends AppCompatActivity {

    private BarChart barChart;
    private Toolbar tb;
    private String hospitalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_performance);

        tb = findViewById(R.id.tb);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.my_button_toolbar);
        }
        barChart = findViewById(R.id.bar);

        barChart = findViewById(R.id.bar);
        barChart.setExtraBottomOffset(30); // Set bottom padding for the chart

        // Retrieve hospital ID from intent extras
        Intent intent = getIntent();
        hospitalId = intent.getStringExtra("hospitalId");
        if (hospitalId == null) {
            // Handle the case where hospital ID is not found
            Log.d("err", "Hospital ID is null");
        } else {
            Log.d("err", hospitalId);
            // Proceed with making the request
            makeRequest();
        }
    }
    private void makeRequest() {
        // Define the hospitalId using a Map
        Map<String, String> params = new HashMap<>();
        params.put("hospital_id", hospitalId);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = API.Graph_URL;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if (status.equals("success")) {
                                JSONArray dataArray = jsonObject.getJSONArray("data");
                                processJSONData(dataArray);
                            } else {
                                // Handle error status here
                                String message = jsonObject.getString("message");
                                if (message.equals("Hospital ID not found")) {
                                    showHospitalIdNotFoundDialog();
                                } else {
                                    Log.e("Error", message);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Pass parameters to the request
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void processJSONData(JSONArray jsonArray) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String date = object.getString("date");
                int total_count = object.getInt("total_count");
                entries.add(new BarEntry(i, total_count));
                labels.add(date);
            }

            BarDataSet dataSet = new BarDataSet(entries, "Total Count ");
            dataSet.setColor(Color.parseColor("#CCED315E")); // Set color for the bars

            BarData barData = new BarData(dataSet);
            barChart.setData(barData);

            XAxis xAxis = barChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            YAxis yAxisLeft = barChart.getAxisLeft();
            YAxis yAxisRight = barChart.getAxisRight();
            yAxisLeft.setAxisMinimum(0);
            yAxisRight.setAxisMinimum(0);

            barChart.setFitBars(true);
            barChart.getDescription().setEnabled(false);
            barChart.animateY(1000);
            barChart.invalidate();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle toolbar item clicks
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                // Navigate back to d_dashboard activity
//                Intent intent = new Intent(this, patient_performance.class);
//                startActivity(intent);
//                finish(); // Finish the current activity
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
@Override
public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
        // Navigate back to patient_profile activity
        Intent intent = new Intent(this, patient_details_doctor.class);
        intent.putExtra("hospital_id", hospitalId); // Pass the hospital ID
        startActivity(intent);
        finish(); // Finish the current activity
        return true;
    } else {
        return super.onOptionsItemSelected(item);
    }
}
    private void showHospitalIdNotFoundDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialogue_box_cus, null);

        builder.setView(dialogView);

        // Find views in the custom layout
        TextView titleTextView = dialogView.findViewById(R.id.dialog_title);
        TextView messageTextView = dialogView.findViewById(R.id.dialog_message);
        Button okButton = dialogView.findViewById(R.id.ok_button);

        // Set dialog title and message
        titleTextView.setText("Alert");
        messageTextView.setText("The hospital ID has not been started yet.");

        // Create the dialog
        final AlertDialog dialog = builder.create();

        // Set corner radius
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);

        // Handle button click
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Dismiss the dialog
            }
        });

        dialog.show();
    }
    @Override
    public void onBackPressed() {
        // Perform any action you want before navigating back
        Intent intent = new Intent(this, patient_details_doctor.class);
        intent.putExtra("hospital_id", getIntent().getStringExtra("hospitalId"));
        startActivity(intent);

        // Finish the current activity to prevent returning to it
        finish();

        // Call the super method to maintain default behavior (optional based on your needs)
        super.onBackPressed();
    }


}
