package com.example.splitwise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class home extends AppCompatActivity {
    RecyclerView recyclerView;
    private AdapterGroup adapterGroup;
    private ArrayList<ModelGroup> groupList;

    FirebaseAuth firebaseAuth;
    private TextView greetingtv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);




        recyclerView = findViewById(R.id.grp_rv);
        greetingtv = findViewById(R.id.greeting_tv);

        firebaseAuth = FirebaseAuth.getInstance();

        BottomNavigationView bottomNavigationView = findViewById(R.id.btmnav_bar);
        bottomNavigationView.setSelectedItemId(R.id.group);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.group:

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

                    case R.id.account:
                        startActivity(new Intent(getApplicationContext(),profile.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                }
                return false;
            }
        });

        ImageButton imgAdd_btn = findViewById(R.id.add_group);
        imgAdd_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(home.this,createGroup.class));

            }
        });

        loadGroups();loadName();
    }

    private void loadName() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = ""+snapshot.child("Name").getValue();
                        setGreetings(name);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setGreetings(String name) {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
            int hour = cal.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if(hour>= 12 && hour < 17){
            greetingtv.setText("Good Afternoon " + name);
        } else if(hour >= 17 && hour < 21){
            greetingtv.setText("Good Evening " + name);
        } else if(hour >= 21 && hour < 24){
            greetingtv.setText("Good Night " + name);
        } else {
            greetingtv.setText("Good Morning " + name);
        }
    }

    private void loadGroups() {
        groupList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupList.clear();
                groupList.size();
                for(DataSnapshot ds : snapshot.getChildren()){
                    if(ds.child("Participants").child(firebaseAuth.getUid()).exists()){
                        ModelGroup modelGroup = ds.getValue(ModelGroup.class);
                        groupList.add(modelGroup);
                    }
                }
                adapterGroup = new AdapterGroup(home.this,groupList);
                recyclerView.setAdapter(adapterGroup);
                System.out.println(adapterGroup.getItemCount());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


}