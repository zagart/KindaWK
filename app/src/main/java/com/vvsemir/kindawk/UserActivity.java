package com.vvsemir.kindawk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.PersistableBundle;
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
import com.vvsemir.kindawk.ui.IFragment;
import com.vvsemir.kindawk.ui.KindaFragment;
import com.vvsemir.kindawk.ui.NewsFragment;
import com.vvsemir.kindawk.ui.ProfileFragment;


public class UserActivity extends AppCompatActivity{
    private static final String CURRENT_FRAGMENT = "current_fragment";
    KindaFragment currentFragment;
    public BottomNavigationView bottomNavigationView;
    ProviderService providerService;
    boolean isServiceBound = false;


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
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, ProviderService.class);
        startService(intent);
        bindService(intent , boundServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isServiceBound) {
            unbindService(boundServiceConnection);
            isServiceBound = false;
        }
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

        if (savedInstanceState != null && savedInstanceState.containsKey(CURRENT_FRAGMENT)) {
            String currentFragmentTag = savedInstanceState.getString(CURRENT_FRAGMENT);
            currentFragment = (KindaFragment) getSupportFragmentManager().findFragmentByTag(currentFragmentTag);
        } else {
            currentFragment = ProfileFragment.newInstance();
        }
    }

    @Override
    protected void onDestroy() {
        //if(!isChangingConfigurations()) {
            //ProviderService.deleteTempFiles(getCacheDir());
        //}

        //Intent intent = new Intent(this, ProviderService.class);
        //stopService(intent);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

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
                else if(id == R.id.action_settings){
                    //loadFragment(ProfileFragment.newInstance());
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
                    switch (item.getItemId()) {
                        case R.id.action_profile:
                            currentFragment = ProfileFragment.newInstance();
                            break;
                        case R.id.action_newsfeed:
                            currentFragment = NewsFragment.newInstance();
                            break;
                        case R.id.action_friends:
                            currentFragment = FriendsFragment.newInstance();
                            break;
                    }

                    loadCurrentFragment();
                    return false;
                }
            });
    }

    private boolean loadCurrentFragment() {
        if (currentFragment != null && !currentFragment.isInLayout() && isServiceBound) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentsContainer, currentFragment, currentFragment.getFragmentTag())
                .addToBackStack(null)
                .commit();

            return true;
        }

        return false;
    }

    void updatebottomNavigationSelection() {
        String tag = currentFragment.getFragmentTag();
        int selectedItemId = bottomNavigationView.getSelectedItemId();

        if(tag.equals(ProfileFragment.FRAGMENT_TAG)){
            if(selectedItemId != R.id.action_profile){
                MenuItem item = bottomNavigationView.getMenu().findItem(R.id.action_profile);

                if (item != null) {
                    item.setChecked(true);
                }
            }
        } else if(tag.equals(FriendsFragment.FRAGMENT_TAG)){
            if(selectedItemId != R.id.action_friends){
                MenuItem item = bottomNavigationView.getMenu().findItem(R.id.action_friends);

                if (item != null) {
                    item.setChecked(true);
                }
            }
        } else if(tag.equals(NewsFragment.FRAGMENT_TAG)){
            if(selectedItemId != R.id.action_newsfeed){
                MenuItem item = bottomNavigationView.getMenu().findItem(R.id.action_newsfeed);

                if (item != null) {
                    item.setChecked(true);
                }
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(CURRENT_FRAGMENT)) {
            String currentFragmentTag = savedInstanceState.getString(CURRENT_FRAGMENT);
            currentFragment = (KindaFragment) getSupportFragmentManager().findFragmentByTag(currentFragmentTag);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_FRAGMENT, currentFragment.getFragmentTag());
    }

    private ServiceConnection boundServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ProviderService.ActivityBinder binder = (ProviderService.ActivityBinder) service ;
            providerService = binder.getService();
            isServiceBound = true;

            updatebottomNavigationSelection();
            loadCurrentFragment();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBound = false;
            providerService = null;

        }
    };

}

