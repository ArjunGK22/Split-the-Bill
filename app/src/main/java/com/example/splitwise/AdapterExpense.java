package com.example.splitwise;

import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AdapterExpense extends RecyclerView.Adapter<AdapterExpense.myHolder> {

    private Context context;
    private ArrayList<ModelExpense> modelExpenses;
    private String groupId;
    String date_str = new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date());


    public AdapterExpense(Context context, ArrayList<ModelExpense> modelExpenses, String groupId) {
        this.context = context;
        this.modelExpenses = modelExpenses;
        this.groupId = groupId;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.expense_row,parent,false);

        return new AdapterExpense.myHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myHolder holder, int position) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        ModelExpense modelE = modelExpenses.get(position);
        String paidBy = modelE.getBy();
        String time = modelE.getTime();
        String item = modelE.getDescription();
        String paidAmt = modelE.getAmount();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(time));
        String dateforamat = DateFormat.format("dd/MM/yyyy",calendar).toString();

        holder.paid_tv.setText("Paid By "+paidBy);
        holder.date_tv.setText(dateforamat);
        holder.item_tv.setText(item);
        holder.paidamt_tv.setText("₹"+paidAmt);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
                reference.child(groupId).child("Expenses").child(modelE.getExpense_id())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(firebaseAuth.getUid().equals(modelE.getUid())){
                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                                    alertDialog.setTitle("Delete")
                                            .setMessage("Are you sure you want to delete?")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    deleteExpense(modelE);
                                                }
                                            })
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            }).show();

                                }
                                else{
                                    Toast.makeText(context,"You cannot delete other expenses",Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });


    }

    private void deleteExpense(ModelExpense modelE) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Expenses").child(modelE.getExpense_id()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        String amt = "" + modelE.getAmount();

                        int av_amt = Integer.parseInt(amt);
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String fire_amt = ""+snapshot.child(groupId).child("Total Amount").getValue();
                                int f_amt = Integer.parseInt(fire_amt);
                                int u_amt = f_amt - av_amt;
                                HashMap<String,Object> hashMap = new HashMap<>();
                                hashMap.put("Total Amount",""+u_amt);

                                reference.child(groupId).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                reference.child(groupId).child("Participants").child(firebaseAuth.getUid())
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                int user_pamt = Integer.parseInt(""+snapshot.child("Amount_paid").getValue());
                                                                int minus_paid = user_pamt - av_amt;
                                                                HashMap<String,Object> hashMap1 = new HashMap<>();
                                                                hashMap1.put("Amount_paid",""+minus_paid);
                                                                reference.child(groupId).child("Participants").child(firebaseAuth.getUid())
                                                                        .updateChildren(hashMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {
                                                                                HashMap<String,Object> hashMap2 = new HashMap<>();
                                                                                hashMap2.put("Monthly Expense",""+u_amt);
                                                                                reference.child(groupId).child("Report").child(date_str)
                                                                                        .updateChildren(hashMap2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void unused) {

                                                                                            }
                                                                                        });

                                                                            }
                                                                        });
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });

                                            }
                                        });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                });


    }


    @Override
    public int getItemCount() {
        return modelExpenses.size();
    }

    class myHolder extends RecyclerView.ViewHolder {

        private TextView date_tv,item_tv,paid_tv,paidamt_tv;


        public myHolder(@NonNull View itemView) {
            super(itemView);
            date_tv = itemView.findViewById(R.id.date_tv);
            item_tv = itemView.findViewById(R.id.item_tv);
            paid_tv = itemView.findViewById(R.id.paidBy_tv);
            paidamt_tv = itemView.findViewById(R.id.paidamt_tv);

        }
    }
}
