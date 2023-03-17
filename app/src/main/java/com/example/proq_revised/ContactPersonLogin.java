package com.example.proq_revised;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;

public class ContactPersonLogin extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private Button loginBtn;
    private FirebaseFirestore db;
    private boolean userExist = false;
    private CollectionReference conPerCollection;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);
        loginBtn = findViewById(R.id.loginBtn);
        db = FirebaseFirestore.getInstance();
        conPerCollection = db.collection("ContactPerson");
        mAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get the input text
                String emailValue = email.getText().toString();
                String passwordValue = password.getText().toString();

                //pass in email and password to caregiverLogin method if user input value for both fields
                if((!emailValue.isEmpty())&&(!passwordValue.isEmpty())){
                    contactPersonLogin(emailValue, passwordValue);
                }else{
                    Toast.makeText(ContactPersonLogin.this, "Please enter email and password" +
                            "", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void contactPersonLogin(String email, String password){
        //Perform query against Bayshore & Paramed collection to search for the inputted email
        Query queryContactPerson = conPerCollection.whereEqualTo("email", email);

        //retrieve data from the query performed against Bayshore & Paramed collection
        getQueryData(queryContactPerson);
        //if user doesn't exist in Bayshore, search in Paramed
        if (userExist==true) {
            //check if email and password matches
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //successful login
                            if (task.isSuccessful()) {
                                // Sign in success
                                Log.d("TAG", "signInWithEmail:success");
                                //go to next activity if email & password matches
                            }
                            //unsuccessful login, display message
                            else {
                                Log.d("TAG", "onComplete: signing fail");
                                Toast.makeText(ContactPersonLogin.this, "Invalid email and/or password. Try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else{
            Toast.makeText(this, "Invalid email and/or password. Try again", Toast.LENGTH_SHORT).show();
        }
    }


    //arrayList contains {id, name, status}
    //get data from query and return in arraylist
    private ArrayList<String> getQueryData(Query query){
        ArrayList<String> user = new ArrayList<>(); //declare & initialize arraylist

        //get data from the query passed in as argument
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        //get data from the returned document
                        String id = document.getId();
                        //check if the user exists before storing data to arraylist
                        if (id!=null) {
                            //add values to arrayList
                            String name = document.get("firstName") + " " + document.get("lastName");

                            userExist = true;
                            user.add(id);
                            user.add(name);
                            user.add
                            Log.d("TAG", user.toString() + " " + userExist);
                        }
                    }
                }else{
                    Toast.makeText(ContactPersonLogin.this, "Search failed. Try again or contact admin", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return user; //return the arraylist populated with user data
    }

}