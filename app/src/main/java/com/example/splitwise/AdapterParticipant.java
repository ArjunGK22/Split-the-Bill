package com.example.splitwise;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterParticipant extends RecyclerView.Adapter<AdapterParticipant.myHolder> {

    Context context;
    ArrayList<ModelUser> modelUsers;
    String groupID, userID;

    public AdapterParticipant(Context context, ArrayList<ModelUser> modelUsers, String groupID, String userID) {
        this.context = context;
        this.modelUsers = modelUsers;
        this.groupID = groupID;
        this.userID = userID;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.addparticipant_row,parent,false);

        return new myHolder(view);
    }
    String userID_for_adding;
    @Override
    public void onBindViewHolder(@NonNull myHolder holder, int position) {

        //Getting the Data
        ModelUser mUser = modelUsers.get(position);
        String userName = mUser.getName();
        String userEmail = mUser.getEmail();
        String userId = mUser.getUid();

        //Setting the data
        holder.pName.setText(userName);
        holder.pEmail.setText(userEmail);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getMenu(mUser);
            }
        });

        checkUser(mUser,holder);





    }

    private void checkUser(ModelUser mUser, myHolder holder) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupID).child("Participants")
                .child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            holder.pStatus.setTextColor(Color.RED);
                            holder.pStatus.setText("Already a participant");

                        }

                        else{
                            holder.pStatus.setTextColor(context.getResources().getColor(R.color.lt_green));
                            holder.pStatus.setText("Click to add as participant");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getMenu(ModelUser modelUser) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("ADD PARTICIPANT")
                .setMessage("Are you sure you want to add this user?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        DatabaseReference reference_toCheck = FirebaseDatabase.getInstance().getReference("Groups");
                        reference_toCheck.child(groupID).child("Participants")
                                        .child(modelUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                            builder1.setTitle("Already An Participant")
                                                    .setMessage("Do you want to remove this user?")
                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            removeParticipant(modelUser);
                                                        }
                                                    })
                                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            dialogInterface.dismiss();

                                                        }
                                                    }).show();

                                        }
                                        else{
                                            addParticipant(modelUser);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(context,""+error.getMessage(),Toast.LENGTH_SHORT).show();


                                    }
                                });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    private void removeParticipant(ModelUser modelUser) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupID).child("Participants").child(modelUser.getUid()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context,"User Removed Successfully",Toast.LENGTH_SHORT).show();
                        decrementParticipant();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void decrementParticipant() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int participants = Integer.parseInt(""+snapshot.child("Total Participants").getValue());
                        if(participants>1)
                            participants--;
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("Total Participants",""+Integer.toString(participants));

                        reference.child(groupID)
                                .updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context,"Participant Removed Successfully",Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void addParticipant(ModelUser muser1) {
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("uid",""+muser1.getUid());
        hashMap.put("Role","admin");
        hashMap.put("Amount_paid","0");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupID).child("Participants").child(muser1.getUid())
                .setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        incrementParticipants();


                    }
                });
    }

    private void incrementParticipants() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int participants = Integer.parseInt(""+snapshot.child("Total Participants").getValue());
                        participants++;
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("Total Participants",""+Integer.toString(participants));

                        reference.child(groupID)
                                .updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context,"Participant Added Successfully",Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    @Override
    public int getItemCount() {
        return modelUsers.size();
    }

    class myHolder extends RecyclerView.ViewHolder{

        TextView pName, pEmail,pStatus;
        public myHolder(@NonNull View itemView) {
            super(itemView);

            pName = itemView.findViewById(R.id.pname_tv);
            pEmail = itemView.findViewById(R.id.pemail_tv);
            pStatus = itemView.findViewById(R.id.pstatus_tv);
        }
    }

}
