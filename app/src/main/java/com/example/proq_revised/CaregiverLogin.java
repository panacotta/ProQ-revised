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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CaregiverLogin extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private Button loginBtn;
    private FirebaseFirestore db;
    private boolean userExist = false;
    private CollectionReference caregiverBayshore;
    private CollectionReference caregiverParamed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);
        loginBtn = findViewById(R.id.loginBtn);
        db = FirebaseFirestore.getInstance();
        caregiverBayshore = db.collection("Bayshore");
        caregiverParamed = db.collection("Paramed");


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get the input text
                String emailValue = email.getText().toString();
                String passwordValue = email.getText().toString();

                //pass in email and password to caregiverLogin method
                caregiverLogin(emailValue, passwordValue);
            }
        });
    }

    private void caregiverLogin(String email, String password){
        //Perform query against Bayshore & Paramed collection to search for the inputted email
        Query queryBS = caregiverBayshore.whereEqualTo("email", email);
        Query queryPM = caregiverParamed.whereEqualTo("email", email);

        //retrieve data from the query performed against Bayshore & Paramed collection
        getQueryData(queryBS);
        //if user doesn't exist in Bayshore, search in Paramed
        if(userExist==false) {
            getQueryData(queryPM);
        }
        //if can't find user email after checking Bayshore and Paramed collections, display message
        if (userExist==false){
            Toast.makeText(this, "User email does not exist. Try again or contact admin", Toast.LENGTH_SHORT).show();
        }
        //check if email and password matches

        //go to next activity if email & password matches
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
                            String status = (String) document.get("status");
                            //only store data of user whose status is active
                            if(status.equalsIgnoreCase("active")){
                                //add values to arrayList
                                String name = document.get("firstName") + " " + document.get("lastName");
                                userExist = true;
                                user.add(id);
                                user.add(name);
                                user.add(status);
                                Log.d("TAG", user.toString()+" "+userExist);
                            }
                        }
                    }
                }else{
                    Toast.makeText(CaregiverLogin.this, "Search failed. Try again or contact admin", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return user; //return the arraylist populated with user data
    }

}