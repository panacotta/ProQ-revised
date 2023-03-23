package com.example.proq_revised;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CaregiverLogin extends AppCompatActivity {
    private TextInputEditText email;
    private TextInputEditText password;
    private CollectionReference paramedCollection;
    private CollectionReference bayshoreCollection;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);
        Button loginBtn = findViewById(R.id.loginBtn);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        bayshoreCollection = db.collection("/Bayshore");
        paramedCollection = db.collection("/Paramed");
        mAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(v -> {
            //Get the input text
            String emailValue = String.valueOf(email.getText());
            String passwordValue = String.valueOf(password.getText());

            //pass in email and password to getQueryData method if user entered value for both fields
            if ((!emailValue.isEmpty()) && (!passwordValue.isEmpty())) {
                //perform query against index in firestore database
                Query queryCaregiverBS = bayshoreCollection.whereEqualTo("email", emailValue);
                Query queryCaregiverPM = paramedCollection.whereEqualTo("email", emailValue);

                //validate user credentials with returned value from query and email and password
                getQueryData(queryCaregiverBS, queryCaregiverPM, emailValue, passwordValue);
            }
            //display message if email and/or password is not provided by user
            else {
                Toast.makeText(getApplicationContext(), "Please enter email and password" + "", Toast.LENGTH_SHORT).show();
            }
            password.setText("");
        });
    }

    //get data from query and validate email and password
    private void getQueryData(Query queryBS, Query queryPM, String email, String password) {
        //get data from the query passed in as argument
        queryBS.get().addOnCompleteListener(task -> {
            //if query is performed successfully and it returns some values
            if (task.isSuccessful() && task.getResult().size() != 0) {
                //validate user
                authenticateUser(task, email, password);
            }
            //if queryBS failed to be run, run queryPM
            else {
                queryPM.get().addOnCompleteListener(task3 -> {
                    if (task3.isSuccessful() && task3.getResult().size() != 0) {
                        authenticateUser(task3, email, password);
                    }
                    //if both queries does not return matching result, display error message
                    else {
                        Toast.makeText(getApplicationContext(), "Incorrect email and/or password. Try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void authenticateUser(Task<QuerySnapshot> task, String email, String password) {
        ArrayList<String> user = new ArrayList<>(); //declare & initialize arraylist

        //get data from the returned document
        for (QueryDocumentSnapshot document : task.getResult()) {
            //add values to arrayList
            String id = document.getId();
            String name = document.get("firstName") + " " + document.get("lastName");
            user.add(id);
            user.add(name);

            //perform authentication with Firestore Authentication library
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(CaregiverLogin.this, task1 -> {
                //successful login
                if (task1.isSuccessful()) {
                    //Sign in succeed and go to next activity if email & password matches
                    Intent intent = new Intent(getApplicationContext(), Search.class);
                    intent.putExtra("caregiverID", user); //pass caregiver info to next activity
                    startActivity(intent);
                }
                //unsuccessful login, display message
                else {
                    Toast.makeText(getApplicationContext(), "Incorrect email and/or password. Try again", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}