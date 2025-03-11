package com.simats.strokecare;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class add_patient_status extends AppCompatActivity {
    EditText pid, name, phno, age;
    String value, name1, phno1, age1;

    Button save;
    ImageView profile;
    LinearLayout parentLayout;
    private Toolbar tb1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient_status);

        pid = findViewById(R.id.rul);
        name = findViewById(R.id.lul);
        phno = findViewById(R.id.rll);
        age = findViewById(R.id.lll);
        profile = findViewById(R.id.mri);
        save = findViewById(R.id.done);

        tb1 = findViewById(R.id.tb1);
        setSupportActionBar(tb1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setHomeAsUpIndicator(R.drawable.my_button_toolbar);
//        }

        parentLayout = findViewById(R.id.parentLayout);

        parentLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });

        String doctorId = getIntent().getStringExtra("doctor_id");


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if all EditText fields are filled
                String value = pid.getText().toString().trim();
                String name1 = name.getText().toString().trim();
                String age1 = age.getText().toString().trim();
                String phno1 = phno.getText().toString().trim();

                // Check if any required field is missing or invalid
                // Check if any required field is missing
                if (value.isEmpty() || name1.isEmpty() || age1.isEmpty() || phno1.isEmpty() || profile == null) {
                    // Show appropriate error message if any required field is missing
                    if (value.isEmpty() || name1.isEmpty() || age1.isEmpty() || phno1.isEmpty()) {
                        ToastUtils.showToast(getApplicationContext(), "All fields are required");
                    } else {
                        ToastUtils.showToast(getApplicationContext(), "Profile picture is required");
                    }
                } else {
                    // All required fields are filled, proceed with data submission
                    sendDataToDatabase();
                    // Proceed to move to the next activity
                    Intent intent = new Intent(add_patient_status.this, d_dashboard .class);
                    intent.putExtra("doctor_id", doctorId); // Pass the doctor's username to the dashboard
                    startActivity(intent);
                    finish();
                }


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
        final String phno1 = phno.getText().toString().trim();

        String hospitalId = getIntent().getStringExtra("hospital_id");
        if (hospitalId == null) {
            hospitalId = "";
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

        try {
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = API.Add_Patient_status_URL;

            // Create a StringRequest with POST method
            String finalProfileBase6 = profileBase64;
            String finalHospitalId = hospitalId;
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
                    params.put("hospital_id", finalHospitalId);
                    params.put("rul", value);
                    params.put("lul", name1);
                    params.put("lll", age1);
                    params.put("rll", phno1);
                    params.put("mri", finalProfileBase6);
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
                                        add_patient_status.this.getContentResolver(),
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

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            // Navigate back to Patient_status activity
//            Intent intent2 = new Intent(this, Patient_status.class);
////            intent.putExtra("HospitalId", getIntent().getStringExtra("HospitalId"));
//            intent2.putExtra("hospitalId",hospitalId);
//            startActivity(intent2);
//            finish(); // Finish the current activity
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
@Override
public void onBackPressed() {
    // Perform any action you want before navigating back
    Intent intent = new Intent(this, add_patient.class);
    intent.putExtra("hospitalId", getIntent().getStringExtra("hospital_id"));
    startActivity(intent);

    // Finish the current activity to prevent returning to it
    finish();

    // Call the super method to maintain default behavior (optional based on your needs)
    super.onBackPressed();
}

}
