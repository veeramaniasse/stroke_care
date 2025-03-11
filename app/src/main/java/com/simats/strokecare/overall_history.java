package com.simats.strokecare;

import static com.simats.strokecare.API.Doc_Physio_URL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

public class overall_history extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private List<PatientInfo1> dataList;
    private String HID;
    LinearLayout parentLayout;
    private Toolbar tb1;
    private String url = Doc_Physio_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overall_history);

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

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomAdapter(dataList);
        recyclerView.setAdapter(adapter);

        fetchFromPHP(HID);
    }

    private void filter(String text) {
        List<PatientInfo1> filteredList = new ArrayList<>();

        for (PatientInfo1 item : dataList) {
            if (item.getDay() != null && item.getMonthName() != null && item.getYear() != null) {
                if (item.getDay().toLowerCase().contains(text.toLowerCase())
                        || item.getMonthName().toLowerCase().contains(text.toLowerCase())
                        || item.getYear().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }

        adapter.filterList(filteredList);
    }

    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        private List<PatientInfo1> dataList;

        public CustomAdapter(List<PatientInfo1> dataList) {
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
            PatientInfo1 patient = dataList.get(position);

            if (holder.dayTextView != null) {
                holder.dayTextView.setText("" + (patient.getDay() != null ? patient.getDay() : ""));
            }
            if (holder.monthnameTextView != null) {
                holder.monthnameTextView.setText("" + (patient.getMonthName() != null ? patient.getMonthName() : ""));
            }
            if (holder.yearTextView != null) {
                holder.yearTextView.setText("" + (patient.getYear() != null ? patient.getYear() : ""));
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PatientInfo1 patient = dataList.get(holder.getAdapterPosition());
                    String day = patient.getDay() != null ? patient.getDay() : "";
                    String monthName = patient.getMonthName() != null ? patient.getMonthName() : "";
                    String year = patient.getYear() != null ? patient.getYear() : "";

                    Intent intent = new Intent(overall_history.this, physio_rec_patient.class);
                    intent.putExtra("HID", HID);
                    intent.putExtra("day", day);
                    intent.putExtra("monthName", monthName);
                    intent.putExtra("year", year);
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public void filterList(List<PatientInfo1> filteredList) {
            this.dataList = filteredList;
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView dayTextView, monthnameTextView, yearTextView;
            ImageView profileImageView;

            public ViewHolder(View itemView) {
                super(itemView);
                dayTextView = itemView.findViewById(R.id.day);
                monthnameTextView = itemView.findViewById(R.id.month);
                yearTextView = itemView.findViewById(R.id.year);
                profileImageView = itemView.findViewById(R.id.profile);
            }
        }
    }

    public void fetchFromPHP(String HID) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                JSONArray jsonArray = jsonResponse.getJSONArray("data");
                                dataList.clear();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String day = jsonObject.optString("day");
                                    String monthname = jsonObject.optString("month_name");
                                    String year = jsonObject.optString("year");

                                    // Check for null values before adding to the list
                                    if (day != null && monthname != null && year != null) {
                                        dataList.add(new PatientInfo1(day, monthname, year));
                                    }
                                }
                                Collections.reverse(dataList);
                                adapter.notifyDataSetChanged();
                            } else {
                                String errorMessage = jsonResponse.optString("message", "Unknown error occurred");
                                ToastUtils.showToast(getApplicationContext(), errorMessage);
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
                params.put("hospital_id", HID);
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
            Intent intent1 = new Intent(this, patient_physio.class);
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
        Intent intent = new Intent(this, patient_physio.class);
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

class PatientInfo1 {

    private String day;
    private String monthname;
    private String year;
    private static String HID;

    public PatientInfo1(String day, String monthname, String year) {
        this.day = day;
        this.monthname = monthname;
        this.year = year;
        this.HID= HID;
    }

    public String getDay() {
        return day;
    }

    public String getMonthName() {
        return monthname;
    }

    public String getYear() {
        return year;
    }
    public static String getHID() {
        return HID;
    }
}
