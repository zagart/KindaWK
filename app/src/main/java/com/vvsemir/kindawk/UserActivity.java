package com.vvsemir.kindawk;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.vvsemir.kindawk.service.ProviderService;
import com.vvsemir.kindawk.ui.FriendsFragment;
import com.vvsemir.kindawk.ui.MessagesFragment;
import com.vvsemir.kindawk.ui.NewsFragment;
import com.vvsemir.kindawk.ui.ProfileFragment;


public class UserActivity extends AppCompatActivity{
    BottomNavigationView bottomNavigationView;

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        /*if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (NoSuchMethodException e) {
                    Log.e("tag", "onMenuOpened", e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }*/
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_menu) {
            showPopupMenu(this, findViewById(R.id.action_menu));

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

        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        setBottomNavigationListener();
        bottomNavigationView.setSelectedItemId(0);

        Intent intent = new Intent(this, ProviderService.class);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        if(!isChangingConfigurations()) {
            ProviderService.deleteTempFiles(getCacheDir());
        }

        Intent intent = new Intent(this, ProviderService.class);
        stopService(intent);
        super.onDestroy();
    }

    public void showPopupMenu(Context context, View view){
        Context wrapper = new ContextThemeWrapper(context, R.style.OptionsPopupMenu);
        PopupMenu popup = new PopupMenu(wrapper, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.user_options_popup, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.action_logout) {
                    /*
                    AuthManager.userLogout();
                    Intent intent=new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish();*/
                }
                else if(id == R.id.action_profile){
                    loadFragment(ProfileFragment.newInstance());
                }
                return false;
            }
        });
    }

    private void setBottomNavigationListener(){
        bottomNavigationView.setOnNavigationItemSelectedListener(
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragment = null;
                    switch (item.getItemId()) {
                        case R.id.action_messages:
                            fragment = MessagesFragment.newInstance();
                            break;
                        case R.id.action_newsfeed:
                            fragment = NewsFragment.newInstance();
                            break;
                        case R.id.action_friends:
                            fragment = FriendsFragment.newInstance();;
                            break;
                    }

                    return loadFragment(fragment);
                }
            });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentsContainer, fragment)
                .addToBackStack(null)
                .commit();

            return true;
        }

        return false;
    }
}

