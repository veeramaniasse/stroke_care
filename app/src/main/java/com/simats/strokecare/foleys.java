package com.simats.strokecare;

import static com.simats.strokecare.API.Foleys_URL;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class foleys extends AppCompatActivity {

    private Spinner spinner;
    private List<String> dateList;
    private ArrayAdapter<String> adapter;
    private String selectedDate;
    private Toolbar tb1;
    private Button button;
    private TextView text;
    private String HID;
    private Button button1, button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foleys);

        text = findViewById(R.id.text1);
        Intent intent = getIntent();
        HID = intent.getStringExtra("HID");

        tb1 = findViewById(R.id.tb);
        setSupportActionBar(tb1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.my_button_toolbar);
        }
        if (HID == null) {
            // Hospital ID not found, handle appropriately
            ToastUtils.showToast(this, "Hospital ID not found");
            return;
        }
        text.setText(HID);
        button1 = findViewById(R.id.previous_history_button);
        button1.setOnClickListener(view -> {
            Intent intent1 = new Intent(foleys.this, foleys_patient.class);
            intent1.putExtra("HID", HID);
            startActivity(intent1);
        });

        button2 = findViewById(R.id.ryles_button);
        button2.setOnClickListener(view -> {
            Intent intent2 = new Intent(foleys.this, ryles.class);
            intent2.putExtra("HID", HID);
            startActivity(intent2);
        });

        button = findViewById(R.id.submit_button);
        button.setOnClickListener(view -> {
            Intent intent3 = new Intent(foleys.this, patient_dashboard.class);
            intent3.putExtra("HID", HID);
            startActivity(intent3);
        });

        spinner = findViewById(R.id.spinner);

        // Initialize dateList with a default entry
        dateList = new ArrayList<>();
        dateList.add("Choose Date");

        // Create an ArrayAdapter
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                dateList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle item selection if needed
                if (position > 0) {
                    selectedDate = dateList.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Display the DatePickerDialog when the dropdown is clicked
                showDatePickerDialog();
                return true;
            }
        });
    }

    private void showDatePickerDialog() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        // Handle the selected date
                        selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                        updateSpinnerText(selectedDate);
                    }
                },
                year,
                month,
                day
        );
        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    private void updateSpinnerText(String selectedDate) {
        // Add the selected date to the list and update the spinner
        dateList.add(selectedDate);
        adapter.notifyDataSetChanged();
        spinner.setSelection(dateList.size() - 1); // Set the selection to the last item

        // Send the selected date to the PHP backend
        sendDateToBackend(selectedDate);
    }

    private void sendDateToBackend(String selectedDate) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Create form data
                    String formData = "hospital_id=" + HID + "&foleys_date=" + selectedDate;

                    // Set up the connection
                    URL url = new URL(Foleys_URL); // Replace YOUR_PHP_SCRIPT_URL with the actual URL of your PHP script
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setDoOutput(true);

                    // Write data to the connection
                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                    outputStream.write(formData.getBytes());

                    // Get the response
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Successfully sent data to the server
                        // Read the response from the server
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();

                        // Process the response
                        String jsonResponse = response.toString();
                        String message = "";

                        // Parse the JSON response to extract the message
                        try {
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            if (jsonObject.has("message")) {
                                message = jsonObject.getString("message");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // Display the message using a custom toast
                        final String finalMessage = message;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showToast(getApplicationContext(), finalMessage);
                            }
                        });
                    } else {
                        // Error handling
                        // You can handle the error here if needed
                    }

                    // Close resources
                    outputStream.flush();
                    outputStream.close();
                    connection.disconnect();

                    // Check if no date is selected in the Spinner
                    if (spinner.getSelectedItemPosition() == 0) {
                        // Send notification
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Dialog myDialog = new Dialog(foleys.this);
                                myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                myDialog.setContentView(R.layout.activity_notification_pat);
                                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                myDialog.show();
                            }
                        });
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Navigate back to Patient_status activity
            Intent intent4 = new Intent(this, patient_dashboard.class);
            intent4.putExtra("HID", HID);
            startActivity(intent4);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        // Perform any action you want before navigating back
        Intent intent = new Intent(this, patient_dashboard.class);
        intent.putExtra("HID", getIntent().getStringExtra("HID"));
        startActivity(intent);

        // Finish the current activity to prevent returning to it
        finish();

        // Call the super method to maintain default behavior (optional based on your needs)
        super.onBackPressed();
    }

}
