package com.example.vaibhav.irisplacements;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
    private ViewPager mViewPager;
    private CustomListAdapter mCompaniesListAdapter;
    private CustomListAdapter mApplicationsListAdapter;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save only applications list.
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
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setAdapter(new CustomPagerAdapter());
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        toolbar.setTitle(R.string.app_name);
        mApplicationsListAdapter = new CustomListAdapter(this);
        if (savedInstanceState != null) {
            List<Entry> ae = savedInstanceState.getParcelableArrayList("applicationsList");
            mApplicationsListAdapter.setEntries(ae);
        }
    }

    public void addToApplicationsList(Entry entry) {
        mApplicationsListAdapter.addEntry(entry);
    }

    class CustomPagerAdapter extends PagerAdapter {
        public static final int COMPANIES = 0;
        public static final int APPLICATIONS = 1;
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
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View rootView = layoutInflater.inflate(layouts[position], container, false);
            container.addView(rootView);
            final RecyclerView recyclerView;
            if (position == COMPANIES) {
                String url = getString(R.string.GET_COMPANIES);
                recyclerView = rootView.findViewById(R.id.companies_rv_list);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                JsonArrayRequest companiesRequest = new JsonArrayRequest(
                        Request.Method.GET,
                        url,
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
                                mCompaniesListAdapter =
                                        new CustomListAdapter(MainActivity.this);
                                mCompaniesListAdapter.setEntries(entries);
                                recyclerView.setAdapter(mCompaniesListAdapter);
                                mCompaniesListAdapter.notifyDataSetChanged();
                                Log.d(TAG, "Size : " + mCompaniesListAdapter.getItemCount());
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG, "Volley companiesRequest error : " + error.getMessage());
                            }
                        }
                );
                VolleyHelper.getInstance(getApplicationContext())
                        .addToRequestQueue(companiesRequest);
            } else {
                recyclerView = rootView.findViewById(R.id.applications_rv_list);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setAdapter(mApplicationsListAdapter);
            }
            return rootView;
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
