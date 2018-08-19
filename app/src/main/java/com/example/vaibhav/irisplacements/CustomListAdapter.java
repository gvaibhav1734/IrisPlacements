package com.example.vaibhav.irisplacements;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
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
    private int type; // To determine which list(companies or applications)

    CustomListAdapter(Context context, int type) {
        this.mContext = context;
        this.type = type;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView company_name, companyType, recruitmentType;
        Button apply;
        ProgressBar progressBar;

        ViewHolder(View itemView) {
            super(itemView);
            company_name = itemView.findViewById(R.id.list_item_tv_company_name);
            companyType = itemView.findViewById(R.id.list_item_tv_company_type);
            recruitmentType = itemView.findViewById(R.id.list_item_tv_recruitment_type);
            apply = itemView.findViewById(R.id.list_item_btn_apply);
            progressBar = itemView.findViewById(R.id.list_item_pb_progress);
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
        // By default text VISIBLE and progress bar INVISIBLE
        holder.progressBar.setVisibility(View.GONE);
        holder.apply.setVisibility(View.VISIBLE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EntryDialog entryDialog =
                        EntryDialog.getInstance(entries.get(holder.getAdapterPosition()));
                entryDialog.show(((MainActivity)mContext).getFragmentManager(),"entry");
                entryDialog.setCancelable(true);
            }
        });
        holder.company_name.setText(entries.get(position).getCompany().getName());
        holder.recruitmentType.setText(entries.get(position).getRecruitment_type());
        holder.companyType.setText(entries.get(position).getCompany().getCompany_type());
        if (type == MainActivity.APPLICATIONS) {
            holder.apply.setText(R.string.cancel);
            holder.apply.setTextColor(mContext.getResources().getColor(R.color.red));
        } else {
            holder.apply.setText(R.string.apply);
        }
        holder.apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.apply.setVisibility(View.INVISIBLE);
                if (entries.get(holder.getAdapterPosition()).isSelected()) {
                    if (type == MainActivity.COMPANIES) {
                        // Display message "Already applied"
                        MainActivity instance = (MainActivity) mContext;
                        ((MainActivity) mContext).snackbarMessage("Already Applied.");
                        holder.progressBar.setVisibility(View.GONE);
                        holder.apply.setVisibility(View.VISIBLE);
                    } else if (type == MainActivity.APPLICATIONS) {
                        // Set entry.selected false in companies list
                        MainActivity instance = (MainActivity) mContext;
                        instance.findEntryInCompanies(entries.get(holder.getAdapterPosition()));
                        // Remove the entry from applications list.
                        entries.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                    }
                } else {
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
                                    Log.d(TAG, "For Entry : "
                                            + entries.get(holder.getAdapterPosition())
                                            .getCompany()
                                            .getName());
                                    holder.progressBar.setVisibility(View.GONE);
                                    holder.apply.setVisibility(View.VISIBLE);
                                    try {
                                        if (response.getBoolean("success")) {
                                            MainActivity instance = (MainActivity) mContext;
                                            instance.addToApplicationsList(
                                                    entries.get(holder.getAdapterPosition())
                                            );
                                            entries.get(holder.getAdapterPosition()).setSelected(true);
                                            instance.snackbarMessage("Applied.");
                                        }
                                    } catch (JSONException error) {
                                        Log.e(TAG, "Volley commitRequest response json error : "
                                                + error.getMessage());
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e(TAG, "Volley commitRequest failure : " + error.getMessage());
                                    holder.progressBar.setVisibility(View.GONE);
                                    holder.apply.setVisibility(View.VISIBLE);
                                    ((MainActivity) mContext).snackbarMessage("Connection Lost.");

                                }
                            }
                    );
                    VolleyHelper.getInstance(mContext).addToRequestQueue(commitRequest);
                }
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

    public Entry find(Entry entry) {
        for (Entry entry1 : entries) {
            if (entry1.getId() == entry.getId())
                return entry1;
        }
        return null;
    }
}
