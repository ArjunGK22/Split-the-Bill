package com.example.splitwise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends AppCompatActivity {
    AnyChartView anyChartView;
    Button getReport_btn;
    DatePicker datePicker;

    FirebaseAuth firebaseAuth;
    ProgressBar load;

    String groupID;


    String[] partition = {"Total Expenditure","Total Settlement"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        anyChartView = findViewById(R.id.piechart);
        getReport_btn = findViewById(R.id.getreport_btn);
        datePicker = findViewById(R.id.date_dp);
        firebaseAuth = FirebaseAuth.getInstance();
        load = findViewById(R.id.pgbar);

        Intent intent = getIntent();
        groupID = intent.getStringExtra("group_id");

        ImageButton back = findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



        getReport_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int month = datePicker.getMonth() + 1;
                int year = datePicker.getYear();

                String date_txt ="0" +month+ "-" + year;
                setpiechart(date_txt);


            }
        });

    }

    private void setpiechart(String date) {
        load.setVisibility(View.VISIBLE);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupID).child("Report").child(date)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists()){
                            int total_expense = Integer.parseInt(""+snapshot.child("Monthly Expense").getValue());
                            int total_settlement = Integer.parseInt(""+snapshot.child("Total Settlement").getValue());

                            int[] account = {total_expense,total_settlement};

                            Pie pie = AnyChart.pie();
                            List<DataEntry> dataEntryList = new ArrayList<>();

                            for(int i=0;i<partition.length;i++){
                                dataEntryList.add(new ValueDataEntry(partition[i],account[i]));

                            }
                            pie.data(dataEntryList);
                            pie.title("Group Report");
                            anyChartView.setChart(pie);
                            anyChartView.setVisibility(View.VISIBLE);
                            load.setVisibility(View.INVISIBLE);
                        }
                        else{
                            anyChartView.setVisibility(View.INVISIBLE);
                            Toast.makeText(ReportActivity.this,"No Results Found",Toast.LENGTH_LONG).show();
                            load.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}