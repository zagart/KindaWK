package com.vvsemir.kindawk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vvsemir.kindawk.auth.AuthManager;
import com.vvsemir.kindawk.provider.Friend;
import com.vvsemir.kindawk.provider.observer.IEvent;
import com.vvsemir.kindawk.provider.observer.IEventObserver;
import com.vvsemir.kindawk.service.ProviderService;
import com.vvsemir.kindawk.ui.FriendsFragment;
import com.vvsemir.kindawk.ui.IFragment;
import com.vvsemir.kindawk.ui.KindaFragment;
import com.vvsemir.kindawk.ui.NewsFragment;
import com.vvsemir.kindawk.ui.PhotoBigFragment;
import com.vvsemir.kindawk.ui.ProfileFragment;
import com.vvsemir.kindawk.ui.SettingsDialogFragment;


public class UserActivity extends AppCompatActivity implements IEventObserver {
    private static final String CURRENT_FRAGMENT = "current_fragment";
    private static final String DIALOG_TAG = "dialog_tag";

    public KindaFragment currentFragment;
    public BottomNavigationView bottomNavigationView;
    MenuNavigationState menuNavigationState = MenuNavigationState.SHOW;
    private ProviderService providerService;
    boolean isServiceBound = false;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Toolbar toolbar = findViewById(R.id.toolbar);
        View actionMenuView = toolbar.getChildAt(1);
        if (id == R.id.action_options) {
            //showPopupMenu(toolbar.getContext(), findViewById(R.id.action_options));
            showPopupMenu(toolbar.getContext(), actionMenuView);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_top, menu);
        ActionBar actionbar = getSupportActionBar();
        Toolbar toolbar = findViewById(R.id.toolbar);

        if(menuNavigationState == MenuNavigationState.HIDE) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow);
            actionbar.setTitle("");
            menu.setGroupVisible(R.id.topMenuHidableItems, false);
            toolbar.getBackground().setAlpha(77);

            FrameLayout frameLayout = findViewById(R.id.fragmentsContainer);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) frameLayout.getLayoutParams();
            params.setMargins(0, 0, 0,0);
            frameLayout.requestLayout();

        } else {
            actionbar.setDisplayHomeAsUpEnabled(false);
            actionbar.setTitle(R.string.app_name);
            menu.setGroupVisible(R.id.topMenuHidableItems, true);
            toolbar.getBackground().setAlpha(255);

            FrameLayout frameLayout = findViewById(R.id.fragmentsContainer);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) frameLayout.getLayoutParams();

            TypedValue typedValue = new TypedValue();
            getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true);
            int actionBarHeight = getResources().getDimensionPixelSize(typedValue.resourceId);
            params.setMargins(0, actionBarHeight, 0,0);
            frameLayout.requestLayout();
        }

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
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        View actionMenuView = toolbar.getChildAt(1);
        if (actionMenuView != null && actionMenuView instanceof ActionMenuView) {
            ((ActionMenuView) actionMenuView).dismissPopupMenus();
        }

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    public void showPopupMenu(final Context context, View view){
        Context wrapper = new ContextThemeWrapper(context, R.style.OptionsPopupMenu);
        final PopupMenu popup = new PopupMenu(wrapper, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.user_options_popup, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.action_logout) {
                    providerService.getDbManager().cleanDb();
                    popup.dismiss();

                    AuthManager.userLogout();
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                    closeOptionsMenu();
                    finish();
                }
                else if(id == R.id.action_settings){
                    SettingsDialogFragment settingsDialogFragment = new SettingsDialogFragment();
                    settingsDialogFragment.show(getSupportFragmentManager(), DIALOG_TAG);
                    popup.dismiss();
                }

                return true;
            }
        });

        popup.show();
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

                    loadCurrentFragment(true);
                    return true;
                }
            });
    }

    public boolean loadCurrentFragment(boolean putToBackStack) {
        if (currentFragment != null && !currentFragment.isInLayout() && isServiceBound) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentsContainer, currentFragment, currentFragment.getFragmentTag());

            if(putToBackStack) {
                fragmentTransaction.addToBackStack(currentFragment.getFragmentTag());
            }

            fragmentTransaction.commit();

            return true;
        }

        return false;
    }

    public void loadProfile(Friend friend) {
        if (currentFragment != null && (currentFragment instanceof ProfileFragment) == false ) {
            currentFragment = ProfileFragment.newInstance();
        }

        ((ProfileFragment)currentFragment).setFriendProfile(friend);
        loadCurrentFragment(true);
        updatebottomNavigationSelection();
    }

    public void loadPhotoFragment(String uri) {
        if (currentFragment != null && (currentFragment instanceof PhotoBigFragment) == false ) {
            currentFragment = PhotoBigFragment.newInstance(uri);
        }

        loadCurrentFragment(true);
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
    public void onBackPressed() {
        FragmentManager fragmentManager =  getSupportFragmentManager();
        if (fragmentManager.popBackStackImmediate()) {

            if( fragmentManager.getBackStackEntryCount() == 0 ){
                finish();
                return;
            }

            FragmentManager.BackStackEntry backEntry = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1);
            if(backEntry != null){
                String tag = backEntry.getName();
                Fragment fragment = fragmentManager.findFragmentByTag(tag);

                if( fragment != null && fragment instanceof KindaFragment ){
                    currentFragment = (KindaFragment)fragment;
                    updateMenuForFragment();
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

    public void updateMenuForFragment(){
        String tag = currentFragment.getFragmentTag();

        if( tag.equals(PhotoBigFragment.FRAGMENT_TAG) ) {
            menuNavigationState = MenuNavigationState.HIDE;
            bottomNavigationView.setVisibility(View.GONE);
        } else {
            menuNavigationState = MenuNavigationState.SHOW;
            bottomNavigationView.setVisibility(View.VISIBLE);
        }

        invalidateOptionsMenu();
    }

    @Override
    public void updateOnEvent(@IEvent Integer event) {
        switch (event) {
            case IEvent.NEW_POST:
                drawNotificationBadge();

                break;
            default:
                eraseNotificationBadge();

                break;
        }
    }

    public void drawNotificationBadge() {
        eraseNotificationBadge();
        BottomNavigationItemView itemView = bottomNavigationView.findViewById(R.id.action_newsfeed);
        View badge = LayoutInflater.from(this).inflate(R.layout.view_notification_badge, itemView, false);
        itemView.addView(badge);
    }

    public void eraseNotificationBadge() {
        BottomNavigationItemView itemView = bottomNavigationView.findViewById(R.id.action_newsfeed);
        View badge = itemView.findViewById(R.id.notifyBadgeView);
        if(badge != null) {
            ((ViewGroup) badge.getParent()).removeView(badge);
        }
    }

    private ServiceConnection boundServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ProviderService.ActivityBinder binder = (ProviderService.ActivityBinder) service ;
            providerService = binder.getService();
            isServiceBound = true;

            registerAsObserver();
            updatebottomNavigationSelection();
            loadCurrentFragment(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBound = false;
            providerService = null;
            unregisterAsObserver();
        }
    };

    void registerAsObserver() {
        providerService.registerObserver(this);
    }

    void unregisterAsObserver() {
        providerService.unregisterObserver(this);
    }


    public final ProviderService getProviderService() {
        if(isServiceBound && providerService != null){
            return providerService;
        }

        return null;
    }

    enum MenuNavigationState{
        SHOW, HIDE
    }
}

