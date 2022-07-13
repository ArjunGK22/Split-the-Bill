
package com.example.splitwise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class profile extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    TextView name_tv, email_tv, phone_tv,name1_tv;

    Button logout_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        email_tv = findViewById(R.id.email_tv);
        phone_tv = findViewById(R.id.mobile_tv);
        name1_tv = findViewById(R.id.name);
        logout_btn = findViewById(R.id.logout_btn);

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder =new AlertDialog.Builder(profile.this);
                builder.setTitle("LOG OUT")
                        .setMessage("Do you want to Log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                logout();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.btmnav_bar);
        bottomNavigationView.setSelectedItemId(R.id.account);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.group:
                        startActivity(new Intent(getApplicationContext(),home.class));
                        overridePendingTransition(1,2);
                        finish();
                        return true;

                    case R.id.account:

                        return true;

                    case R.id.friends:
                        startActivity(new Intent(getApplicationContext(),users.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;


//                    case R.id.activity:
//                        startActivity(new Intent(getApplicationContext(),useractivity.class));
//                        overridePendingTransition(0,0);
//                        finish();
//                        return true;


                }
                return false;
            }
        });
        loadDetails();

    }

    private void logout() {
        HashMap<String,Object> hmap = new HashMap<>();
        hmap.put("Active","false");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).updateChildren(hmap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firebaseAuth.signOut();
                        startActivity(new Intent(profile.this,login.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(profile.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void loadDetails() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("Uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                                String name = ""+ds.child("Name").getValue();
                                String email = ""+ds.child("Email").getValue();
                                String phone = ""+ds.child("Phone").getValue();

                                email_tv.setText(String.valueOf(email));
                                phone_tv.setText("+91 " +phone);
                                name1_tv.setText("@"+name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}