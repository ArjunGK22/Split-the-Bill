package com.example.splitwise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class createGroup extends AppCompatActivity {

    EditText groupName_et;
    TextView save_tv;
    FirebaseAuth firebaseAuth;
    ProgressBar circular_progress;
    ImageButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        firebaseAuth = FirebaseAuth.getInstance();
        groupName_et = findViewById(R.id.groupname_et);
        save_tv = findViewById(R.id.save_tv);
        circular_progress = findViewById(R.id.circular_progress);
        back = findViewById(R.id.backbtn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        save_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDetails();
            }
        });


    }
    String group_name,group_id;
    private void getDetails() {
        group_id = ""+System.currentTimeMillis();
        group_name = groupName_et.getText().toString().trim();

        if(TextUtils.isEmpty(group_name)){
            Toast.makeText(this,"Kindly enter the group name",Toast.LENGTH_SHORT).show();
            return;
        }

        create_group();


    }



    private void create_group() {
        circular_progress.setVisibility(View.VISIBLE);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("groupID",""+group_id);
        hashMap.put("groupName",""+group_name);
        hashMap.put("createdBy",""+firebaseAuth.getUid());
        hashMap.put("Total Amount","0");
        hashMap.put("Total Participants","1");




        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(group_id).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        HashMap<String,Object> hashMap1 = new HashMap<>();
                        hashMap1.put("uid",firebaseAuth.getUid());
                        hashMap1.put("Amount_paid","0");
                        hashMap1.put("Role","admin");

                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Groups");
                        reference1.child(group_id).child("Participants").child(firebaseAuth.getUid())
                                        .setValue(hashMap1)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {


                                                        circular_progress.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(createGroup.this,"Group Created Successfully",Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(createGroup.this,home.class));

                                                    }
                                                });

                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(createGroup.this,""+e.getMessage(),Toast.LENGTH_SHORT);
                    }
                });



    }





}