package com.vvsemir.kindawk;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.vvsemir.kindawk.auth.AuthManager;
import com.vvsemir.kindawk.service.ProviderIntentService;
import com.vvsemir.kindawk.ui.TopPagerAdapter;

import java.io.File;

public class UserActivity extends AppCompatActivity{

    TopPagerAdapter topPagerAdapter;
    ViewPager viewPager;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_menu) {
            /*
            AuthManager.userLogout();
            Intent intent=new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();*/
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_top, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //ActionBar actionbar = getSupportActionBar();
        //actionbar.setDisplayHomeAsUpEnabled(true);
        //actionbar.setHomeAsUpIndicator(R.drawable.ic_exit);

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

    @Override
    protected void onDestroy() {
        if(!isChangingConfigurations()) {
            ProviderIntentService.deleteTempFiles(getCacheDir());
        }
        Intent intent = new Intent(this, ProviderIntentService.class);
        stopService(intent);
        super.onDestroy();
    }
}

