package com.example.vaibhav.irisplacements;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TabLayout tabLayout = findViewById(R.id.main_tl_tabs);
        mViewPager = findViewById(R.id.main_vp_list_container);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setAdapter(new CustomPagerAdapter());
    }
    class CustomPagerAdapter extends PagerAdapter{
        public static final int COMPANIES = 0;
        public static final int APPLICATIONS = 1;
        private String titles[] = {"Companies","Applications"};
        private int layouts[] = {R.layout.layout_companies,R.layout.layout_applications};

        CustomPagerAdapter(){
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View rootView = layoutInflater.inflate(layouts[position],container,false);
            container.addView(rootView);
            RecyclerView recyclerView;
            if(position==COMPANIES){
                recyclerView = findViewById(R.id.companies_rv_list);
            } else {
                recyclerView = findViewById(R.id.applications_rv_list);
            }
            recyclerView.setAdapter(new CustomListAdapter(getApplicationContext()));
            return container;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            super.destroyItem(container, position, object);
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
