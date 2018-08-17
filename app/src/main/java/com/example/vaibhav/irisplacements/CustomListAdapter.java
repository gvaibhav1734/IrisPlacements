package com.example.vaibhav.irisplacements;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.ViewHolder>{
    private Context mContext;
    private List<Entry> entries;
    CustomListAdapter(Context context){
        this.mContext = context;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView company_name,recruitment_date,deadline;
        Button apply;
        ViewHolder(View itemView){
            super(itemView);
            company_name = itemView.findViewById(R.id.list_item_tv_company_name);
            recruitment_date = itemView.findViewById(R.id.list_item_tv_recruitment_date);
            deadline = itemView.findViewById(R.id.list_item_tv_deadline);
            apply = itemView.findViewById(R.id.list_item_btn_apply);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext)
                .inflate(R.layout.layout_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.company_name.setText(entries.get(position).getCompany().getName());
        holder.recruitment_date.setText(entries.get(position).getRecruitment_date());
        holder.deadline.setText(entries.get(position).getDeadline());
        holder.apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:Volley request.
            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
