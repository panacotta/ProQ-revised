package com.example.proq_revised;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {
    private Button caregiverBtn;
    private Button contactPersonBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        caregiverBtn=findViewById(R.id.caregiverBtn);
        contactPersonBtn=findViewById(R.id.contactPersonBtn);

        caregiverBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), CaregiverLogin.class);
            startActivity(intent);
        });

        contactPersonBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ContactPersonLogin.class);
            startActivity(intent);
        });
    }
}