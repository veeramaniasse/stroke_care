package com.simats.strokecare;

import static com.simats.strokecare.API.Add_Patient_URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class add_patient extends AppCompatActivity {
    EditText pid, name, phno, age, diagnosis;
    String value, name1, phno1, gender1, age1, diagnosis1;
    Spinner genderSpinner;
    Button save;
    ImageView profile;
    LinearLayout parentLayout;
    private String doctorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

         doctorId = getIntent().getStringExtra("doctor_id");


        pid = findViewById(R.id.hospitalid);
        name = findViewById(R.id.name);
        phno = findViewById(R.id.mobile);
        age = findViewById(R.id.age);
        genderSpinner = findViewById(R.id.genderspinner);
        diagnosis = findViewById(R.id.diagnosis);
        profile = findViewById(R.id.profile);
        save = findViewById(R.id.done);
        parentLayout = findViewById(R.id.parentLayout);

        parentLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });

        // Set up adapters for spinners
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.gender,
                android.R.layout.simple_spinner_item
        );
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call a method to handle data submission
                sendDataToDatabase();
            }
        });
    }

    private void showImageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image Source");
        builder.setItems(new CharSequence[]{"Camera", "Gallery"}, (dialog, which) -> {
            if (which == 0) {
                dispatchTakePictureIntent();
            } else if (which == 1) {
                pickImageFromGallery();
            }
        });
        builder.show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageCaptureLauncher.launch(takePictureIntent);
    }

    private void pickImageFromGallery() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imageGalleryLauncher.launch(pickIntent);
    }

    private void sendDataToDatabase() {
        showProgressBar();

        final String value = pid.getText().toString().trim();
        final String name1 = name.getText().toString().trim();
        final String age1 = age.getText().toString().trim();
        final String gender1 = genderSpinner.getSelectedItem().toString().trim();
        final String diagnosis1 = diagnosis.getText().toString().trim();
        final String phno1 = phno.getText().toString().trim();

        // Generate password from the last 4 characters of hospital ID
        String password = "";
        if (value.length() >= 4) {
            int n = value.length();
            password = value.substring(n - 4, n);
        } else {
            // Handle the case where hospital ID is not long enough
            ToastUtils.showToast(getApplicationContext(), "Hospital ID must be at least 4 characters long");
            hideProgressBar();
            return;
        }

        // Convert Bitmap to Base64
        String profileBase64 = "";
        if (profile.getDrawable() != null) {
            if (profile.getDrawable() instanceof BitmapDrawable) {
                Bitmap profileBitmap = ((BitmapDrawable) profile.getDrawable()).getBitmap();
                profileBase64 = convertBitmapToBase64(profileBitmap);
            } else if (profile.getDrawable() instanceof VectorDrawable) {
                Bitmap profileBitmap = getBitmapFromVectorDrawable((VectorDrawable) profile.getDrawable());
                profileBase64 = convertBitmapToBase64(profileBitmap);
            }
        }

        Intent intent = new Intent(add_patient.this, add_patient_status.class);
        intent.putExtra("hospital_id", value); // Pass hospital_id to the next activity
        intent.putExtra("doctor_id", doctorId);
        startActivity(intent);

        try {
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = Add_Patient_URL;

            // Create a StringRequest with POST method
            String finalProfileBase6 = profileBase64;
            String finalPassword = password;
            StringRequest stringRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Handle the response from the server
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                String status = jsonResponse.getString("status");
                                if ("success".equals(status)) {
                                    runOnUiThread(() -> {
                                        ToastUtils.showToast(getApplicationContext(), "Details updated successfully");
                                    });
                                } else {
                                    runOnUiThread(() -> {
                                        ToastUtils.showToast(getApplicationContext(), "Failed to update details");
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } finally {
                                hideProgressBar();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Handle errors here
                            runOnUiThread(() -> {
                                ToastUtils.showToast(getApplicationContext(), "Error: " + error.getMessage());
                            });
                            hideProgressBar();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("hospital_id", value);
                    params.put("name", name1);
                    params.put("password", finalPassword);
                    params.put("gender", gender1);
                    params.put("age", age1);
                    params.put("diagnosis", diagnosis1);
                    params.put("mobile_number", phno1);
                    params.put("profile_image", finalProfileBase6);
                    return params;
                }
            };

            // Add the request to the RequestQueue.
            queue.add(stringRequest);

        } catch (Exception e) {
            e.printStackTrace();
            hideProgressBar();
        }
    }



    private Bitmap getBitmapFromVectorDrawable(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    private void showProgressBar() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Delay the dismissal of the progress bar for 1 second (1000 milliseconds)
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                    }
                },
                1000
        );
    }

    private void hideProgressBar() {
        // Dismiss the progress dialog if it's showing
        ProgressDialog progressDialog = new ProgressDialog(this);
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    ActivityResultLauncher<Intent> imageCaptureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Bundle extras = result.getData().getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        profile.setImageBitmap(imageBitmap);
                    }
                }
            }
    );

    ActivityResultLauncher<Intent> imageGalleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            try {
                                Uri selectedImageUri = result.getData().getData();
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                        add_patient.this.getContentResolver(),
                                        selectedImageUri
                                );
                                profile.setImageBitmap(bitmap);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
    );

    // Method to convert Bitmap to Base64
    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
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

        Intent intent = new Intent(this, d_dashboard.class);
//        intent.putExtra("hospitalId", getIntent().getStringExtra("hospital_id"));
        startActivity(intent);
        // Finish the current activity to prevent returning to it
        finish();
        // Call the super method to maintain default behavior (optional based on your needs)
        super.onBackPressed();
    }

}
