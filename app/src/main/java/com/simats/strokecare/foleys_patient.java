package com.simats.strokecare;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class foleys_patient extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private List<FoleysInfo> dataList;

    private Button button;
    private String HID;
    private Toolbar tb1;
    LinearLayout parentLayout;
    private String url = API.Doc_Foleys_rec_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foleys_patient);

        parentLayout = findViewById(R.id.parentLayout);

        parentLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });

        Intent intent = getIntent();
        HID = intent.getStringExtra("HID");

        tb1 = findViewById(R.id.tb);
        setSupportActionBar(tb1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.my_button_toolbar);
        }
        button = findViewById(R.id.ryles);
        button.setOnClickListener(view -> {
            Intent intent1 = new Intent(foleys_patient.this, ryles_patient.class);
            intent1.putExtra("HID", HID); // Pass the hospital ID
            startActivity(intent1);
        });


        EditText editTextSearch = findViewById(R.id.searchview);
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        dataList = new ArrayList<>();

        recyclerView = findViewById(R.id.rc);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomAdapter(dataList);
        recyclerView.setAdapter(adapter);

        // Replace "your_hospital_id" with the actual hospital ID
        fetchFromPHP(HID);
    }

    private void filter(String text) {
        List<FoleysInfo> filteredList = new ArrayList<>();

        for (FoleysInfo item : dataList) {
            if (item.getDay() != null && item.getMonth() != null && item.getYear() != null) {
                if (item.getDay().toLowerCase().contains(text.toLowerCase())
                        || item.getMonth().toLowerCase().contains(text.toLowerCase())
                        || item.getYear().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }

        adapter.filterList(filteredList);
    }

    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        private List<FoleysInfo> dataList;

        public CustomAdapter(List<FoleysInfo> dataList) {
            this.dataList = dataList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.physio_pat_item, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            FoleysInfo patient = dataList.get(position);
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

//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String selectedItem = patient.getDay() != null ? patient.getDay() : "";
//
//                    Intent intent = new Intent(foleys_patient.this, bed_sores_history.class);
//                    intent.putExtra("item", selectedItem);
//                    startActivity(intent);
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public void filterList(List<FoleysInfo> filteredList) {
            this.dataList = filteredList;
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView dayTextView, monthTextView, yearTextView;
            ImageView profileImageView;

            public ViewHolder(View itemView) {
                super(itemView);
                dayTextView = itemView.findViewById(R.id.day);
                monthTextView = itemView.findViewById(R.id.month);
                yearTextView = itemView.findViewById(R.id.year);
                profileImageView = itemView.findViewById(R.id.profile);
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

                            // Check if the response was successful
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                // Parse the data array
                                JSONArray jsonArray = jsonResponse.getJSONArray("data");
                                dataList.clear();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String day = jsonObject.optString("day");
                                    String month = jsonObject.optString("month_name");
                                    String year = jsonObject.optString("year");

                                    dataList.add(new FoleysInfo(day, month, year));
                                }
                                Collections.reverse(dataList);
                                adapter.notifyDataSetChanged();
                            } else {
                                // Handle the case when no records are found
                                TextView noRecordTextView = findViewById(R.id.noRecordTextView);
                                noRecordTextView.setVisibility(View.VISIBLE); // Show the TextView

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
                params.put("hospital_id", HID);  // Add the hospital ID as a parameter
                return params;
            }
        };

        queue.add(stringRequest);
    }


    private void handleError(VolleyError error) {
        if (error instanceof TimeoutError) {
            ToastUtils.showToast(getApplicationContext(), "Request timed out. Check your internet connection.");
        } else {
            ToastUtils.showToast(getApplicationContext(), error.toString().trim());
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Navigate back to Patient_status activity
            Intent intent1 = new Intent(this, foleys.class);
//            intent1.putExtra("hospitalId", getIntent().getStringExtra("hospitalId"));
            intent1.putExtra("HID", HID);
            startActivity(intent1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        // Perform any action you want before navigating back
        Intent intent = new Intent(this, foleys.class);
        intent.putExtra("HID", getIntent().getStringExtra("HID"));
        startActivity(intent);

        // Finish the current activity to prevent returning to it
        finish();

        // Call the super method to maintain default behavior (optional based on your needs)
        super.onBackPressed();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

class FoleysInfo {
    private String day;
    private String month;
    private String year;

    public FoleysInfo(String day, String month, String year) {
        this.day = day;
        this.month = month;
        this.year = year;
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
}