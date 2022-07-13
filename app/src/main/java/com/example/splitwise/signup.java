package com.example.splitwise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class signup extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    EditText uname,password,confpassword,uemail,phone_et;
    TextView login;
    Button btn_signup;
    ProgressBar pgbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();

        uname = findViewById(R.id.name_et);
        uemail = findViewById(R.id.email_et);
        password = findViewById(R.id.pwd_et);
        confpassword = findViewById(R.id.confpwd_et);
        login = findViewById(R.id.login_tv);
        btn_signup = findViewById(R.id.btn_signup);
        phone_et = findViewById(R.id.phone_et);
        pgbar = findViewById(R.id.progressBar);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(signup.this, com.example.splitwise.login.class));
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getdata();
            }
        });


    }
    String name,email,upassword,cpassword,uphone;
    private void getdata() {

        name = uname.getText().toString().trim();
        email = uemail.getText().toString().trim();
        upassword = password.getText().toString().trim();
        cpassword = confpassword.getText().toString().trim();
        uphone = phone_et.getText().toString().trim();

        if(TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Enter all the details to continue", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Enter all the details to continue",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(uphone)){
            Toast.makeText(this,"Enter all the details to continue",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(upassword)){
            Toast.makeText(this,"Enter all the details to continue",Toast.LENGTH_LONG).show();
            return;
        }


        if(TextUtils.isEmpty(cpassword)){
            Toast.makeText(this,"Enter all the details to continue",Toast.LENGTH_LONG).show();
            return;
        }

        if(!upassword.equals(cpassword)){
            Toast.makeText(this,"Password do not match",Toast.LENGTH_LONG).show();
            return;

        }

        if(password.length()<6){
            Toast.makeText(this,"Password too short...",Toast.LENGTH_LONG).show();
            return;

        }

        createAccount();

    }

    private void createAccount() {
        pgbar.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(email,upassword)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        saveData();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(signup.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveData() {

        HashMap<String,Object> hashMap = new HashMap<>();

        hashMap.put("Uid",""+firebaseAuth.getUid());
        hashMap.put("Name",""+name);
        hashMap.put("Email",""+email);
        hashMap.put("Phone",""+uphone);
        hashMap.put("Password",""+upassword);
        hashMap.put("Active","true");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(signup.this,home.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(signup.this,""+e.getMessage(),Toast.LENGTH_SHORT);
                    }
                });

    }
}