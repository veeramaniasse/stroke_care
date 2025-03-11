package com.simats.strokecare;

import static com.simats.strokecare.API.Doc_Edit_profile_URL;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class d_profile_edit extends AppCompatActivity {

    private EditText editName, editAge, editGender, editContactNo;
    private TextView textDocId;
    private Button btnSave;
    private RequestQueue requestQueue;
    private String docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dprofile_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editName = findViewById(R.id.edit1);
        textDocId = findViewById(R.id.edit2); // Changed to TextView
        editAge = findViewById(R.id.edit3);
        editGender = findViewById(R.id.edit4);
        editContactNo = findViewById(R.id.edit5);
        btnSave = findViewById(R.id.save);

        requestQueue = Volley.newRequestQueue(this);

        // Get doc_id from the previous activity
        Intent intent = getIntent();
        docId = intent.getStringExtra("doc_id");
        editName.setText(intent.getStringExtra("name"));
        editAge.setText(intent.getStringExtra("age"));
        editGender.setText(intent.getStringExtra("gender"));
        editContactNo.setText(intent.getStringExtra("mobile_number"));
        textDocId.setText(docId);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDoctorProfile();
            }
        });
    }

    private void saveDoctorProfile() {
        String url = Doc_Edit_profile_URL; // Replace with your server URL

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");
                            String message = jsonResponse.getString("message");
                            Toast.makeText(d_profile_edit.this, message, Toast.LENGTH_LONG).show();

                            // Navigate to doc_profile activity on success
                            if (status.equals("success")) {
                                Intent intent = new Intent(d_profile_edit.this, doctor_profile.class);
                                intent.putExtra("username", docId);
                                startActivity(intent);
                                finish(); // Optional: close the current activity
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(d_profile_edit.this, "JSON parse error", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(d_profile_edit.this, "Volley error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", editName.getText().toString().trim());
                params.put("doc_id", docId); // Use the doc_id passed from the previous activity
                params.put("age", editAge.getText().toString().trim());
                params.put("gender", editGender.getText().toString().trim());
                params.put("contact_no", editContactNo.getText().toString().trim());
                // If you have a file to upload, handle it here
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
}
