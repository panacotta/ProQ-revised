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

        caregiverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, CaregiverLogin.class);
                startActivity(intent);
            }
        });

        contactPersonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, ContactPersonLogin.class);
                startActivity(intent);
            }
        });
    }
}