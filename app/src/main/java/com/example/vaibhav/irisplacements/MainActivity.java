package com.example.vaibhav.irisplacements;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final int COMPANIES = 0;
    public static final int APPLICATIONS = 1;
    private ViewPager mViewPager;
    private CustomListAdapter mCompaniesListAdapter;
    private CustomListAdapter mApplicationsListAdapter;
    private ProgressBar progressBar;
    private boolean loadFromServer=true;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(
                "companiesList",
                (ArrayList<Entry>) mCompaniesListAdapter.getEntries()
        );
        outState.putParcelableArrayList(
                "applicationsList",
                (ArrayList<Entry>) mApplicationsListAdapter.getEntries()
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TabLayout tabLayout = findViewById(R.id.main_tl_tabs);
        mViewPager = findViewById(R.id.main_vp_list_container);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setAdapter(new CustomPagerAdapter());
        toolbar.setTitle(R.string.app_name);
        mApplicationsListAdapter = new CustomListAdapter(this,APPLICATIONS);
        mCompaniesListAdapter = new CustomListAdapter(this,COMPANIES);
        if (savedInstanceState != null) {
            List<Entry> a1 = savedInstanceState.getParcelableArrayList("companiesList");
            mCompaniesListAdapter.setEntries(a1);
            List<Entry> a2 = savedInstanceState.getParcelableArrayList("applicationsList");
            mApplicationsListAdapter.setEntries(a2);
            loadFromServer=false;
        }
    }

    public void addToApplicationsList(Entry entry) {
        mApplicationsListAdapter.addEntry(entry);
    }

    public void findEntryInCompanies(Entry entry) {
        mCompaniesListAdapter.find(entry).setSelected(false);
    }

    public void snackbarMessage(String message) {
        Snackbar.make(mViewPager,message,Snackbar.LENGTH_SHORT).show();
    }
    class CustomPagerAdapter extends PagerAdapter {
        private String titles[] = {"Companies", "My Applications"};
        private int layouts[] = {R.layout.layout_companies, R.layout.layout_applications};

        CustomPagerAdapter() {
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull final ViewGroup container, int position) {
            LayoutInflater layoutInflater = getLayoutInflater();
            final View rootView = layoutInflater.inflate(layouts[position], container, false);
            container.addView(rootView);
            final RecyclerView recyclerView;
            if (position == COMPANIES) {
                progressBar = findViewById(R.id.companies_pb_progress);
                progressBar.setVisibility(View.VISIBLE);
                recyclerView = rootView.findViewById(R.id.companies_rv_list);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                if(loadFromServer)
                    makeRequest(rootView, recyclerView);
                else {
                    recyclerView.setAdapter(mCompaniesListAdapter);
                    progressBar.setVisibility(View.GONE);
                }
            } else {
                recyclerView = rootView.findViewById(R.id.applications_rv_list);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setAdapter(mApplicationsListAdapter);
            }
            return rootView;
        }

        /**
         * Makes request and populates recyclerView contained in rootView
         * with json obtained from server.
         */
        void makeRequest(final View rootView, final RecyclerView recyclerView) {
            progressBar.setVisibility(View.VISIBLE);
            final JsonArrayRequest companiesRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    getString(R.string.GET_COMPANIES),
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d(TAG, "Volley success : " + response.toString());
                            Type listType = new TypeToken<ArrayList<Entry>>() {
                            }.getType();
                            ArrayList<Entry> entries =
                                    GsonHelper.getInstance()
                                            .getGson().fromJson(response.toString(), listType);
                            Log.d(TAG, "List after GSON : " + entries.toString());
                            mCompaniesListAdapter.setEntries(entries);
                            recyclerView.setAdapter(mCompaniesListAdapter);
                            mCompaniesListAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            Log.d(TAG, "Size : " + mCompaniesListAdapter.getItemCount());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(final VolleyError error) {
                            Log.e(TAG, "Volley companiesRequest error : " + error.getMessage());
                            progressBar.setVisibility(View.GONE);
                            Snackbar.make(rootView, "Connection Error", Snackbar.LENGTH_INDEFINITE)
                                    .setAction("RETRY", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Log.d(TAG,"Make request (Connection retry) attempted");
                                            // Making one more request.
                                            makeRequest(rootView, recyclerView);
                                        }
                                    })
                                    .show();
                        }
                    }
            );
            VolleyHelper.getInstance(getApplicationContext())
                    .addToRequestQueue(companiesRequest);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
