package com.example.vaibhav.irisplacements;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.ViewHolder> {
    private static final String TAG = "CustomListAdapter";
    private Context mContext;
    private List<Entry> entries = new ArrayList<>();

    CustomListAdapter(Context context) {
        this.mContext = context;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView company_name, recruitment_date, deadline;
        Button apply;

        ViewHolder(View itemView) {
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Log.d(TAG, entries.get(position).getUrl());
        holder.company_name.setText(entries.get(position).getCompany().getName());
        holder.recruitment_date.setText(entries.get(position).getRecruitment_date());
        holder.deadline.setText(entries.get(position).getDeadline());
        holder.apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make a POST request to obtain commit:true as a json response.
                JSONObject json = null;
                try {
                    json = new JSONObject();
                    json.put("commit", "true");
                } catch (JSONException error) {
                    Log.e(TAG, "commit JSON creation error : " + error.getMessage());
                }
                String url = mContext.getString(R.string.POST_APPLICATION);
                JsonObjectRequest commitRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        json,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, "Volley commitRequest success : " + response.toString());
                                MainActivity instance = (MainActivity) mContext;
                                instance.addToApplicationsList(
                                        entries.get(holder.getAdapterPosition())
                                );
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Volley commitRequest failure : " + error.getMessage());
                            }
                        }
                );
                VolleyHelper.getInstance(mContext).addToRequestQueue(commitRequest);
            }
        });
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void addEntry(Entry entry) {
        entries.add(entry);
        notifyItemInserted(entries.size() - 1);
    }
}
