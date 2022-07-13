package com.example.splitwise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddParticipants extends AppCompatActivity {

    String groupID;

    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;

    private ArrayList<ModelUser> participantList;
    private AdapterParticipant adapterAddParticipant;

    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_participants);

        recyclerView = findViewById(R.id.users_rv);
        firebaseAuth = FirebaseAuth.getInstance();


        back = findViewById(R.id.backbtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        Intent intent = getIntent();
        groupID =  intent.getStringExtra("groupId");
        getAllUsers();
    }

    private void getAllUsers() {
        participantList = new ArrayList<>();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                participantList.size();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    if(!modelUser.getUid().equals(firebaseUser.getUid()))
                        participantList.add(modelUser);


                }

                adapterAddParticipant = new AdapterParticipant(AddParticipants.this,participantList,""+groupID,"");
                recyclerView.setAdapter(adapterAddParticipant);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}