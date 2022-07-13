package com.example.splitwise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class login extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    EditText user_tv, password_tv;
    Button btn_login;
    TextView signup_tv;
    ProgressBar pgbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        firebaseAuth = FirebaseAuth.getInstance();

        user_tv = findViewById(R.id.email_tv);
        password_tv = findViewById(R.id.password_tv);
        btn_login = findViewById(R.id.btn_login);

        signup_tv = findViewById(R.id.signup);
        pgbar = findViewById(R.id.progressBar);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        signup_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(login.this,signup.class) );
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
            }
        });


    }
    String username, password;
    private void getData() {
        username = user_tv.getText().toString().trim();
        password = password_tv.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(username).matches()){
            Toast.makeText(login.this,"Invalid email format",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(login.this,"Enter the password",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(username)){
            Toast.makeText(login.this,"Enter the username",Toast.LENGTH_SHORT).show();
            return;
        }
        pgbar.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(username,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        pgbar.setVisibility(View.INVISIBLE);
                        startActivity(new Intent(login.this,home.class));
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pgbar.setVisibility(View.INVISIBLE);
                        Toast.makeText(login.this,"Invalid Username Or Password",Toast.LENGTH_SHORT).show();

                    }
                });



    }


}