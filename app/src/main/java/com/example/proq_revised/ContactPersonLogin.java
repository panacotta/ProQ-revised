package com.example.proq_revised;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ContactPersonLogin extends AppCompatActivity {
    private TextInputEditText email;
    private TextInputEditText password;
    private CollectionReference conPerCollection;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);
        Button loginBtn = findViewById(R.id.loginBtn);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        conPerCollection = db.collection("/ContactPerson");
        mAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(v -> {
            //Get the input text
            String emailValue = String.valueOf(email.getText());
            String passwordValue = String.valueOf(password.getText());

            //pass in email and password to getQueryData method if user entered value for both fields
            if ((!emailValue.isEmpty()) && (!passwordValue.isEmpty())) {
                //perform query against index in firestore database
                Query queryContactPerson = conPerCollection.whereEqualTo("email", emailValue);

                //validate user credentials with returned value from query and email and password
                getQueryData(queryContactPerson, emailValue, passwordValue);
            }
            //display message if email and/or password is not provided by user
            else {
                Toast.makeText(getApplicationContext(), "Please enter email and password" + "", Toast.LENGTH_SHORT).show();
            }
            password.setText("");
        });
    }

    //get data from query and validate email and password
    private void getQueryData(Query query, String email, String password) {
        ArrayList<String> user = new ArrayList<>(); //declare & initialize arraylist

        //get data from the query passed in as argument
        query.get().addOnCompleteListener(task -> {
            //if query is performed successfully
            if (task.isSuccessful()) {

                //if query returns some values
                if (task.getResult().size() != 0) {

                    //get data from the returned document
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //add values to arrayList
                        String id = document.getId();
                        String name = document.get("firstName") + " " + document.get("lastName");
                        List<String> clientList = (List<String>) document.get("clientList");
                        user.add(id);
                        user.add(name);
                        user.add(String.valueOf(clientList));
                        Log.d("TAG", String.valueOf(user));

                        //perform authentication with Firestore Authentication library
                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(ContactPersonLogin.this, task1 -> {
                            //successful login
                            if (task1.isSuccessful()) {
                                //Sign in succeed and go to next activity if email & password matches
                                Intent intent = new Intent(getApplicationContext(), Search.class);
                                intent.putExtra("contactPersonID", user); //pass contact person ID to next activity
                                startActivity(intent);
                            }
                            //unsuccessful login, display message
                            else {
                                Toast.makeText(getApplicationContext(), "Incorrect email and/or password. Try again", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                //if query doesn't return any matching result
                else {
                    Toast.makeText(getApplicationContext(), "Incorrect email and/or password. Try again", Toast.LENGTH_SHORT).show();
                }
            }
            //if query failed to be run
            else {
                Toast.makeText(getApplicationContext(), "Sign in failed. Try again", Toast.LENGTH_SHORT).show();
            }
        });
    }
}