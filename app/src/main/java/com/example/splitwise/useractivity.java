package com.example.splitwise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class useractivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_useractivity);

//        BottomNavigationView bottomNavigationView = findViewById(R.id.btmnav_bar);
//        bottomNavigationView.setSelectedItemId(R.id.activity);
//
//        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()){
//                    case R.id.group:
//                        startActivity(new Intent(getApplicationContext(),home.class));
//                        overridePendingTransition(1,1);
//                        finish();
//                        return true;
//
//                    case R.id.friends:
//                        startActivity(new Intent(getApplicationContext(),users.class));
//                        overridePendingTransition(0,0);
//                        finish();
//                        return true;
//
//
//                    case R.id.activity:
//                        finish();
//                        return true;
//
//                    case R.id.account:
//                        startActivity(new Intent(getApplicationContext(),profile.class));
//                        overridePendingTransition(0,0);
//                        finish();
//                        return true;
//
//                }
//                return false;
//            }
//        });
    }
}