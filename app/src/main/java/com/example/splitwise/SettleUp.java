package com.example.splitwise;

import static java.lang.Math.abs;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class SettleUp extends AppCompatActivity {
    String groupID;
    Button pay_btn;

    FirebaseAuth firebaseAuth;
    String date_str = new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date());

    int temp_balance_amt;

    String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
    int GOOGLE_PAY_REQUEST_CODE = 123;






    TextView total_tv,participants_tv,selfPaid_tv,balance_tv;

    ActivityResultLauncher<Intent> startActive;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settle_up);

        firebaseAuth = FirebaseAuth.getInstance();
        Intent i1 = getIntent();
        groupID = i1.getStringExtra("group");

        System.out.println(groupID);

        pay_btn = findViewById(R.id.pay_btn);
        total_tv = findViewById(R.id.total_amount);
        participants_tv = findViewById(R.id.participants_tv);
        selfPaid_tv = findViewById(R.id.selfpay_tv);
        balance_tv = findViewById(R.id.balance_tv);

        ImageButton close = findViewById(R.id.close_btn);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        pay_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                google_pay();
            }
        });

        loadDetails();

    }

    private void google_pay() {

        if(balanceAmt<=0){
            Toast.makeText(this,"You do not owe anything! Happy Splitting",Toast.LENGTH_SHORT).show();

        }
        else{

            Uri uri =
                    new Uri.Builder()
                            .scheme("upi")
                            .authority("pay")
                            .appendQueryParameter("pa", "arjungkanikeri@okicici")
                            .appendQueryParameter("pn", "Arjun G K")
                            .appendQueryParameter("mc", "")
                            .appendQueryParameter("tr", "202105051735")
                            .appendQueryParameter("tn", "Split Wise")
                            .appendQueryParameter("am", ""+balanceAmt)
                            .appendQueryParameter("cu", "INR")
                            .appendQueryParameter("url", "your-transaction-url")
                            .build();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            Intent choice = Intent.createChooser(intent,"Pay With");
            startActivityForResult(choice, GOOGLE_PAY_REQUEST_CODE);

            settleup();

        }


    }

    private void settleup() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");


        reference.child(groupID).child("Participants").child(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int getUserAmt = Integer.parseInt(""+snapshot.child("Amount_paid").getValue());
                        int temp = getUserAmt + balanceAmt;
                        System.out.println("Balnce AMt" +balanceAmt);
                        temp_balance_amt = balanceAmt;

                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("Amount_paid",""+temp);
                        reference.child(groupID).child("Participants").child(firebaseAuth.getUid())
                                .updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        updateReport();
                                    }
                                });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void updateReport() {
        HashMap<String,Object> str = new HashMap<>();
        System.out.println("Expense Amount"+temp_balance_amt);
        str.put("Total Settlement",""+temp_balance_amt);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupID).child("Report").child(date_str)
                .updateChildren(str)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {


                    }
                });
    }

    String total_amt,total_participants,paid_amt;
    private void loadDetails() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        total_amt = ""+snapshot.child("Total Amount").getValue();
                        total_participants = ""+snapshot.child("Total Participants").getValue();
                        total_tv.setText("₹"+total_amt);
                        participants_tv.setText(""+total_participants);

                        reference.child(groupID).child("Participants").child(firebaseAuth.getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        paid_amt = ""+snapshot.child("Amount_paid").getValue();
                                        selfPaid_tv.setText("₹"+paid_amt);
                                        calculateMoney();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    int totalAmount,totalParticipants,amountPaidByUser,divideAmount,balanceAmt;
    private void calculateMoney() {
         totalAmount = Integer.parseInt(total_amt);
         totalParticipants = Integer.parseInt(total_participants);
         amountPaidByUser = Integer.parseInt(paid_amt);

         divideAmount = totalAmount / totalParticipants;
         balanceAmt = divideAmount - amountPaidByUser;

            balance_tv.setText("₹"+balanceAmt);







    }
}