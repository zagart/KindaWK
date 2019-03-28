package com.vvsemir.kindawk;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import com.vvsemir.kindawk.auth.AuthManager;
import com.vvsemir.kindawk.ui.TopPagerAdapter;

public class UserActivity extends AppCompatActivity{

    TopPagerAdapter topPagerAdapter;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        topPagerAdapter = new TopPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.userViewPager);
        viewPager.setAdapter(topPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}

