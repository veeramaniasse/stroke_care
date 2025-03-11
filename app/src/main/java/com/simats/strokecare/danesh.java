package com.simats.strokecare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

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

public class danesh extends AppCompatActivity {
    EditText pid, name, phno, age, diagnosis,email;
    String value, name1, phno1, gender1, age1, diagnosis1;
    Spinner genderSpinner;
    Button save;
    ImageView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danesh);

        pid = findViewById(R.id.id);
        name = findViewById(R.id.name);
        phno = findViewById(R.id.mobile);
        age = findViewById(R.id.age);
        genderSpinner = findViewById(R.id.genderspinner);
        diagnosis = findViewById(R.id.diagnosis);
        email = findViewById(R.id.email);
        profile = findViewById(R.id.profile);
        save = findViewById(R.id.done);

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
        final String email1 = email.getText().toString().trim();


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

//        Intent intent = new Intent(danesh.this, add_patient_status.class);
//        intent.putExtra("hospital_id", value); // Pass hospital_id to the next activity
//        startActivity(intent);

        try {
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "http://10.0.2.2/php/danesh_patient_details.php";

            // Create a StringRequest with POST method
            String finalProfileBase6 = profileBase64;
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
                                        Toast.makeText(danesh.this, "Details updated successfully", Toast.LENGTH_SHORT).show();
                                    });
                                } else {
                                    runOnUiThread(() -> {
                                        Toast.makeText(danesh.this, "Failed to update details", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(danesh.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                            hideProgressBar();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("patient_id", value);
                    params.put("patient_name", name1);
                    params.put("email", email1);
                    params.put("gender", gender1);
                    params.put("age", age1);
                    params.put("diagnosis", diagnosis1);
                    params.put("mobileno", phno1);
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
                                        danesh.this.getContentResolver(),
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
}
