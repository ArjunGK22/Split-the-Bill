package com.example.splitwise;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterGroupActivity extends RecyclerView.Adapter<AdapterGroupActivity.myholder> {


    private Context context;
    private ArrayList<ModelActivity> modelActivityArrayList;

    public AdapterGroupActivity(Context context, ArrayList<ModelActivity> modelActivityArrayList) {
        this.context = context;
        this.modelActivityArrayList = modelActivityArrayList;
    }

    @NonNull
    @Override
    public myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_activity_row,parent,false);


        return new myholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myholder holder, int position) {

        ModelActivity modelActivity = modelActivityArrayList.get(position);
        String desp = modelActivity.getDescription();
        String date = modelActivity.getDate();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(date));
        String dateforamat = DateFormat.format("dd/MM/yyyy",calendar).toString();

        holder.desctv.setText(desp);
        holder.timetv.setText(dateforamat);

    }

    @Override
    public int getItemCount() {
        return modelActivityArrayList.size();
    }

    class myholder extends RecyclerView.ViewHolder {

        private TextView desctv, timetv;
        public myholder(@NonNull View itemView) {
            super(itemView);

            desctv = itemView.findViewById(R.id.desc_tv);
            timetv = itemView.findViewById(R.id.date_tv);

        }
    }

}
