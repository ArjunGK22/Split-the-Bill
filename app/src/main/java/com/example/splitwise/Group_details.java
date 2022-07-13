package com.example.splitwise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Group_details extends AppCompatActivity {

    String groupId;
    private TextView groupname_tv,out_amt_tv,participants_tv;
    private Button add_expense,settleUp,report_btn;
    ImageButton addParticipants_btn;

    ProgressBar progressBar;
    RecyclerView recyclerView;
    private AdapterExpense adapterExpense;
    private ArrayList<ModelExpense> modelExpenses;

    String date_str = new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date());

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        Intent intent = getIntent();
        groupId = intent.getStringExtra("group_id");

        groupname_tv = findViewById(R.id.groupName_tv);
        out_amt_tv = findViewById(R.id.text_outstanding);
        add_expense = findViewById(R.id.exp_btn);
        settleUp = findViewById(R.id.settleup_btn);
        addParticipants_btn = findViewById(R.id.add_participant_ib);
        report_btn = findViewById(R.id.report_btn);
        participants_tv = findViewById(R.id.participants_tv);
        ImageButton arrow_back = findViewById(R.id.arrow_back);


        progressBar = findViewById(R.id.progressBar2);

        recyclerView = findViewById(R.id.expense_rv);


        firebaseAuth = FirebaseAuth.getInstance();

        report_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent report_intent = new Intent(Group_details.this,ReportActivity.class);
                report_intent.putExtra("group_id",groupId);
                startActivity(report_intent);
            }
        });

        arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        addParticipants_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(Group_details.this,AddParticipants.class);
                intent1.putExtra("groupId",groupId);
                startActivity(intent1);
            }
        });

        add_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                openExpenseDialog();
            }
        });

        settleUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(groupId);
                Intent intent1 = new Intent(Group_details.this,SettleUp.class);
                intent1.putExtra("group",groupId);
                startActivity(intent1);
            }
        });


        loadGroupDetails();
        loadExpenses();

    }

    private void loadExpenses() {
        modelExpenses = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Expenses")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        modelExpenses.clear();
                        modelExpenses.size();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelExpense me = ds.getValue(ModelExpense.class);
                            modelExpenses.add(me);
                        }

                        adapterExpense = new AdapterExpense(Group_details.this,modelExpenses,groupId);
                        recyclerView.setAdapter(adapterExpense);
                    }



                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    AlertDialog alertDialog;
    EditText descriptionEt,expenseEt;
    Button expenseBtn;
    ImageButton backbtn;
    private void openExpenseDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.add_expense_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        descriptionEt = view.findViewById(R.id.description_et);
        expenseEt = view.findViewById(R.id.amount_et);
        expenseBtn = view.findViewById(R.id.add_btn);
        backbtn = view.findViewById(R.id.back_btn);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        expenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addExpense();
            }
        });


        alertDialog = builder.create();
        alertDialog.show();

    }
    String description, expense,name,expense_id;
    private void addExpense() {

        progressBar.setVisibility(View.VISIBLE);
        description = descriptionEt.getText().toString().trim();
        expense = expenseEt.getText().toString().trim();
        expense_id = ""+System.currentTimeMillis();

        if(description.equals("") || expense.equals("")){
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(Group_details.this,"Kindly fill all the fields!!",Toast.LENGTH_SHORT).show();
            return;
        }


        DatabaseReference reference_user = FirebaseDatabase.getInstance().getReference("Users");
        reference_user.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        name = ""+snapshot.child("Name").getValue();

                        HashMap<String,Object> hashMap = new HashMap<>();

                        hashMap.put("Description",""+description);
                        hashMap.put("Amount",""+expense);
                        hashMap.put("By",""+name);
                        hashMap.put("Uid",""+firebaseAuth.getUid());
                        hashMap.put("expense_id",""+expense_id);
                        hashMap.put("Time",""+expense_id);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
                        reference.child(groupId).child("Expenses").child(expense_id)
                                .setValue(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        updatePaidAmount(expense);


                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void updatePaidAmount(String myExpense) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");

        int my_expense = Integer.parseInt(myExpense);

        reference.child(groupId).child("Participants").child(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        alertDialog.dismiss();
                        int avail_expense = Integer.parseInt(""+snapshot.child("Amount_paid").getValue());
                        int update_expense = avail_expense + my_expense;

                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("Amount_paid",""+update_expense);
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                        reference.child(groupId).child("Participants").child(firebaseAuth.getUid())
                                .updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        updateAmount(expense);
                                    }
                                });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




    }

    String totalAmt;
    private void updateAmount(String expense_param) {
        HashMap<String,Object> hashMap = new HashMap<>();
        int total_amt = Integer.parseInt(totalAmt);
        int expense_amt = Integer.parseInt(expense_param);
        int total_expense = total_amt + expense_amt;
        hashMap.put("Total Amount",""+total_expense);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId)
                .updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressBar.setVisibility(View.INVISIBLE);
                        updateExpense(total_expense);
                        Toast.makeText(Group_details.this,"Expense Added to the group",Toast.LENGTH_SHORT).show();


                    }
                });
    }

    private void updateExpense(int ttl_expense) {

        HashMap<String,Object> hmap = new HashMap<>();
        hmap.put("Monthly Expense",""+ttl_expense);
        hmap.put("Total Settlement","0");



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Report").child(date_str)
                .updateChildren(hmap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });

    }

    private void loadGroupDetails() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String groupName = ""+snapshot.child("groupName").getValue();
                        totalAmt = ""+snapshot.child("Total Amount").getValue();
                        String tpart = ""+snapshot.child("Total Participants").getValue();

                        groupname_tv.setText(groupName);
                        out_amt_tv.setText("Total Out Standing Amount is ₹" +totalAmt);
                        participants_tv.setText("Total Participants in the group: "+tpart);

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}