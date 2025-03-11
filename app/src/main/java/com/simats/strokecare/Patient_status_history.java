package com.simats.strokecare;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Patient_status_history extends AppCompatActivity {

    private TextView rulTextView, lulTextView, rllTextView, lllTextView;
    private Toolbar tb;
    private  String hospitalId;
    private RecyclerView recyclerView;
   private CustomAdapter adapter;
    private List<StatusInfo> dataList;
    private String url = API.Doc_Patient_status_History_URL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_status_history);

        Intent intent=getIntent();
        hospitalId=intent.getStringExtra("HospitalId");


       tb = findViewById(R.id.tb1);

        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.my_button_toolbar);
        }
        dataList = new ArrayList<>();

        recyclerView = findViewById(R.id.rc);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomAdapter(dataList);
        recyclerView.setAdapter(adapter);

        fetchFromPHP(hospitalId);
    }

    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        private List<StatusInfo> dataList;

        public CustomAdapter(List<StatusInfo> dataList) {
            this.dataList = dataList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            StatusInfo patient = dataList.get(position);
            Log.d("lastTag", patient.getDay());

            if (holder.dayTextView != null) {
                holder.dayTextView.setText("" + (patient.getDay() != null ? patient.getDay() : ""));
            }
            if (holder.monthTextView != null) {
                holder.monthTextView.setText("" + (patient.getMonth() != null ? patient.getMonth() : ""));
            }
            if (holder.yearTextView != null) {
                holder.yearTextView.setText("" + (patient.getYear() != null ? patient.getYear() : ""));
            }
            if (holder.lllTextView!= null) {
                holder.lllTextView.setText("" + (patient.getLll() != null ? patient.getLll() : ""));
            }
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public void filterList(List<StatusInfo> filteredList) {
            this.dataList = filteredList;
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView dayTextView, monthTextView, yearTextView,lllTextView;


            public ViewHolder(View itemView) {
                super(itemView);
                dayTextView = itemView.findViewById(R.id.rul);
                monthTextView = itemView.findViewById(R.id.lul);
                yearTextView = itemView.findViewById(R.id.rll);
                lllTextView = itemView.findViewById(R.id.lll);
            }
        }
    }

    public void fetchFromPHP(String hospitalId) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            // Check if the response contains the "data" array
                            if (jsonResponse.has("data")) {
                                // Parse the data array
                                JSONArray jsonArray = jsonResponse.getJSONArray("data");
                                dataList.clear();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String day = jsonObject.optString("rul");
                                    String month = jsonObject.optString("lul");
                                    String year = jsonObject.optString("rll");
                                    String lll = jsonObject.optString("lll");

                                    dataList.add(new StatusInfo(day, month, year, lll));
                                }
                                Collections.reverse(dataList);
                                adapter.notifyDataSetChanged();
                            } else {
                                // Handle the case when the "data" array is missing
                                // For example, display an error message to the user
                                Log.e("Response Error", "Data array not found in the response");
                                // Show a toast or alert dialog to inform the user about the failure
                                // For example:
                                // Toast.makeText(Patient_status_history.this, "Failed to fetch data: Data array not found in the response", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleError(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("hospital_id", hospitalId);  // Add the hospital ID as a parameter
                return params;
            }
        };

        queue.add(stringRequest);
    }


    private void handleError(VolleyError error) {
        if (error instanceof TimeoutError) {
            ToastUtils.showToast(getApplicationContext(), "Request timed out. Check your internet connection");
        } else {
            String errorMessage = error.toString().trim();
            ToastUtils.showToast(getApplicationContext(), errorMessage);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Navigate back to Patient_status activity
            Intent intent1 = new Intent(this, Patient_status.class);
//            intent1.putExtra("hospitalId", getIntent().getStringExtra("hospitalId"));
            intent1.putExtra("hospitalId", hospitalId);
            startActivity(intent1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

class StatusInfo {
    private String day;
    private String month;
    private String year;
    private String lll;
    public StatusInfo(String day, String month, String year,String lll) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.lll = lll;
    }

    public String getDay() {
        return day;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }
    public String getLll() {
        return lll;
    }
}