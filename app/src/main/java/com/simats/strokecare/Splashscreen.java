package com.simats.strokecare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


public class Splashscreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        new Handler().postDelayed(new Runnable(){

            @Override
            public void run() {

                Intent iHome = new Intent(Splashscreen.this, MainActivity.class);
                startActivity(iHome);

            }
        }, 5000);



    }
}