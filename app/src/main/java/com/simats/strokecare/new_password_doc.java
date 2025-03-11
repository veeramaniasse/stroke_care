package com.simats.strokecare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;


public class new_password_doc extends AppCompatActivity {

    EditText newPassword;
    EditText confirmPassword;
    Button submitButton;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password_doc);

        newPassword = findViewById(R.id.new_password);
        confirmPassword = findViewById(R.id.confirm_password);
        submitButton= findViewById(R.id.subimt_button);
        progressBar = findViewById(R.id.submit_progress_bar);

    }

    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.GONE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            submitButton.setVisibility(View.VISIBLE);
        }
    }

    private void navigateToLogins() {
        Intent intent = new Intent(this, d_login.class);
        // Pass any necessary data to the new activity using intent.putExtra()
        startActivity(intent);
    }
}