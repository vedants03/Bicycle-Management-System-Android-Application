package com.example.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    TextView signup;
    Button login;
    EditText loginId;
    EditText password;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null){
            Bundle myBundle = new Bundle();
            myBundle.putString("username", firebaseUser.getEmail());
            Intent intent1 = new Intent(getApplicationContext(),LocationActivity.class);
            intent1.putExtras(myBundle);
            startActivity(intent1);
        }
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");
        signup = (TextView) findViewById(R.id.signup);
        login = (Button)findViewById(R.id.login_button); 
        loginId = (EditText) findViewById(R.id.loginidet);
        password = (EditText) findViewById(R.id.passet);


        progressDialog = new ProgressDialog(this);
        
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });
        
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = loginId.getText().toString();
                String pass = password.getText().toString();
                progressDialog.setMessage("Logging in...");
                progressDialog.setTitle("Log-In");
                progressDialog.show();
                firebaseAuth.signInWithEmailAndPassword(id,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            Intent intent = new Intent(getApplicationContext(),LocationActivity.class);
                            startActivity(intent);
                        }
                        else{
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),""+task.getException(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });

               // Query query = databaseReference.child("users").orderByChild("username").equalTo(id);
//                query.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()) {
//                            // dataSnapshot is the "issue" node with all children with id 0
//                            User user = dataSnapshot.getValue(User.class);
//                            if (user.password.equals(pass)){
//                                Bundle bundle = new Bundle();
//                                bundle.putString("username", user.username);
//                                Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
//                                intent.putExtras(bundle);
//                                startActivity(intent);
//                            } else {
//                                Toast.makeText(getApplicationContext(), "Incorrect Password", Toast.LENGTH_LONG).show();
//                            }
//                        }
//                        else {
//                            Toast.makeText(getApplicationContext(), "User not found", Toast.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
//                        Toast.makeText(LoginActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
            }
        });
    }
}