package com.example.splitwise;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterGroup extends RecyclerView.Adapter<AdapterGroup.HolderGroup> {

    private Context context;
    private ArrayList<ModelGroup> groupsList;

    public AdapterGroup(Context context, ArrayList<ModelGroup> groupsList) {
        this.context = context;
        this.groupsList = groupsList;
    }

    @NonNull
    @Override
    public HolderGroup onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_card,parent,false);

        return new HolderGroup(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroup holder, int position) {

        ModelGroup modelGroup = groupsList.get(position);
        String groupId = modelGroup.getGroupID();
        String gname = modelGroup.getGroupName();

        holder.groupName_tv.setText(gname);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,Group_details.class);
                intent.putExtra("group_id",groupId);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return groupsList.size();
    }

    class HolderGroup extends RecyclerView.ViewHolder{

        private TextView groupName_tv;

        public HolderGroup(@NonNull View itemView) {
            super(itemView);

            groupName_tv = itemView.findViewById(R.id.group_tv);



        }
    }

}
