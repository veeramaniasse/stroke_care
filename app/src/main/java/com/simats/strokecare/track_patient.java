package com.simats.strokecare;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class track_patient extends AppCompatActivity implements FetchMissingDaysTask.OnMissingDaysFetchedListener {

    private RecyclerView recyclerView;
    private MissingDaysAdapter adapter;
    private String hospitalId;
    private Toolbar tb1;
    private int totalDaysExpected = 5; // Example value, replace with your actual logic

    // Inside your activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_patient);

        tb1 = findViewById(R.id.tb);
        setSupportActionBar(tb1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.my_button_toolbar);
        }
        // Retrieve hospital ID from intent extras
        Intent intent = getIntent();
        hospitalId = intent.getStringExtra("hospitalId");

        recyclerView = findViewById(R.id.rc);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Replace "hospital_id" with the actual hospital ID
//        new FetchMissingDaysTask(this).execute(hospitalId);
        new FetchMissingDaysTask(this, this).execute(hospitalId);

    }

    @Override
    public void onMissingDaysFetched(ArrayList<String> missingDays) {
        // Set up the adapter with the missing days data and attach it to the RecyclerView
        adapter = new MissingDaysAdapter(missingDays);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Navigate back to Patient_status activity
            Intent intent1 = new Intent(this, patient_details_doctor.class);
//            intent1.putExtra("hospitalId", getIntent().getStringExtra("hospitalId"));
            intent1.putExtra("hospital_id", hospitalId);
            startActivity(intent1);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
