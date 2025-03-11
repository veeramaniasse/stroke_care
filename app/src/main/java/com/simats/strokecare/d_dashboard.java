package com.simats.strokecare;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class d_dashboard extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<PatientInfo> dataList;
    private List<PatientInfo> filteredList;
    private PatientAdapter adapter;
    private SearchView searchView;
    private Button button1;
    DrawerLayout  parentLayout;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ddashboard);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.tool_bar1);
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchview);
        button1 = findViewById(R.id.btnAddPatient);

//        parentLayout = findViewById(R.id.drawerLayout);

        drawerLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
//        String doctorUsername = getIntent().getStringExtra("doc_id");
//        String doctorId = getIntent().getStringExtra("doctor_id");
//
//        // Get the drawable resource ID of your menu icon
//        Drawable menuIcon = getResources().getDrawable(R.drawable.baseline_menu_24);
//
//      // Set the tint color to white
//        menuIcon.setTint(getResources().getColor(android.R.color.white));
//
//      // Set the menu icon with the updated tint color
//        toolbar.setNavigationIcon(menuIcon);
//        toolbar.setNavigationIcon(R.drawable.baseline_menu_24);

        button1.setOnClickListener(view -> {
            Intent intent1 = new Intent(d_dashboard.this, add_patient.class);
            intent1.putExtra("doctor_id", username);
            startActivity(intent1);
        });


        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.OpenDrawer, R.string.CloseDrawer
        );
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.id1) {
                // Handle "Profile" item click
                Intent intent1 = new Intent(d_dashboard.this, doctor_profile.class);
//                 intent1.putExtra("username", doctorId);
                intent1.putExtra("username", username);
                Log.d(TAG, "docId: " + username);

                startActivity(intent1);
            } else if (id == R.id.id2) {
                Intent intent2 = new Intent(d_dashboard.this, MainActivity.class);
                intent2.putExtra("username", username);
                startActivity(intent2);
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
        // Set up RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Set up RecyclerView Adapter and data
        dataList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new PatientAdapter(filteredList);
        recyclerView.setAdapter(adapter);

        // Fetch data from MySQL database
        new FetchDataTask().execute();

        // Initialize SearchView
        searchView.setFocusable(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Call the filter method with the new text
                filter(newText);
                return true;
            }
        });
    }

    private void filter(String newText) {
        // Update the filtered list and notify the adapter
        filteredList.clear();


        // Iterate through your dataList and add matching items to the filteredList
        for (PatientInfo patientInfo : dataList) {
            if (patientInfo.getName().toLowerCase().contains(newText.toLowerCase())||patientInfo.getHospitalId().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(patientInfo);
            }
        }

        // Update your adapter with the filtered list
        adapter.notifyDataSetChanged();
    }

    private class FetchDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String urlStr = API.D_Dashboard_URL;

            try {
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Check for successful response code (HTTP 200)
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }

                    bufferedReader.close();
                    inputStream.close();

                    return stringBuilder.toString();
                } else {
                    // Check for HTML errors
                    InputStream errorStream = connection.getErrorStream();
                    if (errorStream != null) {
                        BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
                        StringBuilder errorBuilder = new StringBuilder();
                        String errorLine;

                        while ((errorLine = errorReader.readLine()) != null) {
                            errorBuilder.append(errorLine).append("\n");
                        }

                        errorReader.close();
                        errorStream.close();

                        return "Error response code: " + connection.getResponseCode() + "\n" + errorBuilder.toString();
                    } else {
                        return "Error response code: " + connection.getResponseCode();
                    }
                }
            } catch (IOException e) {
                return "Error fetching data: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null && result.startsWith("Error")) {
                // Handle the error, log it, or show a message to the user
                Log.e("FetchDataTask", result);
            } else if (result != null) {
                try {
                    // Parse the entire JSON object
                    JSONObject responseObject = new JSONObject(result);

                    // Check if the response contains a "data" key
                    if (responseObject.has("data")) {
                        // Get the JSON array under the "data" key
                        JSONArray jsonArray = responseObject.getJSONArray("data");

                        // Clear existing data
                        dataList.clear();

                        // Iterate through the JSON array and add data to the list
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            // Check if the JSON object has the expected keys
                            if (jsonObject.has("hospital_id") && jsonObject.has("name") && jsonObject.has("profile_image")) {
                                String hospitalId = jsonObject.getString("hospital_id");
                                String name = jsonObject.getString("name");
                                String profileImage = jsonObject.getString("profile_image");

                                PatientInfo patient = new PatientInfo();
                                patient.setHospitalId(hospitalId);
                                patient.setName(name);
                                patient.setProfileImage(profileImage);
                                dataList.add(0, patient);
                            } else {
                                Log.e("FetchDataTask", "JSON object is missing expected keys");
                            }
                        }

                        // Update the filtered list and notify the adapter
                        filteredList.clear();
                        filteredList.addAll(dataList);
                        adapter.notifyDataSetChanged();
                    } else {
                        // Handle the case where the response does not contain a "data" key
                        Log.e("FetchDataTask", "Invalid JSON response: " + result);
                    }
                } catch (JSONException e) {
                    Log.e("FetchDataTask", "JSON parsing error: " + e.getMessage());
                }
            }
        }
    }

    public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.ViewHolder> {

        private List<PatientInfo> dataList;

        public PatientAdapter(List<PatientInfo> dataList) {
            this.dataList = dataList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plist, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            PatientInfo patientInfo = dataList.get(position);

            holder.hospitalIdTextView.setText("Hospital ID: " + (patientInfo.getHospitalId() != null ? patientInfo.getHospitalId() : ""));
            holder.nameTextView.setText("Name: " + (patientInfo.getName() != null ? patientInfo.getName() : ""));

            // Load profile photo using Picasso
            if (patientInfo.getProfileImage() != null && !patientInfo.getProfileImage().isEmpty()) {
                // If the profile image is a   file name, construct the complete URL
                String completeImageUrl = API.BASE_URL + patientInfo.getProfileImage();
                Picasso.get().load(completeImageUrl).into(holder.profileImageView);
            } else {
                // If no profile photo, set a default image
                holder.profileImageView.setImageResource(R.drawable.baseline_account_circle_24);
            }

            holder.itemView.setOnClickListener(v -> {
                String selectedItem = patientInfo.getHospitalId() != null ? patientInfo.getHospitalId() : "";
                Intent intent = new Intent(d_dashboard.this, patient_details_doctor.class);
                intent.putExtra("hospital_id", selectedItem);
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView hospitalIdTextView, nameTextView;
            ImageView profileImageView;

            public ViewHolder(View itemView) {
                super(itemView);
                hospitalIdTextView = itemView.findViewById(R.id.id);
                nameTextView = itemView.findViewById(R.id.name);
                profileImageView = itemView.findViewById(R.id.profile);
            }
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
        Intent intent = new Intent(this, d_login.class);
//        intent.putExtra("hospitalId", getIntent().getStringExtra("hospital_id"));
        startActivity(intent);

        // Finish the current activity to prevent returning to it
        finish();

        // Call the super method to maintain default behavior (optional based on your needs)
        super.onBackPressed();
    }

}
