package de.vhoeher.openweatherapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import de.vhoeher.openweatherapp.R;
import de.vhoeher.openweatherapp.util.LocaleHelper;
import de.vhoeher.openweatherapp.fragments.HomeFragment;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout = null;
    private SharedPreferences mSharedPreferences = null;
    private String mInitialLocale = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name);

        //Get preferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(mSharedPreferences.getString(getString(R.string.pref_source_key), null) == null){
            mSharedPreferences.edit().putString(getString(R.string.pref_source_key),getString(R.string.pref_source_openweathermap_value)).commit();
        }
        mInitialLocale = LocaleHelper.getPersistedLocale(this);

        //Set Toolbar
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                        //Close the drawer
                        mDrawerLayout.closeDrawers();

                        //Update UI
                        switch (menuItem.getItemId()) {
                            case R.id.nav_settings:
                                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                                startActivity(settingsIntent);
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new HomeFragment())
                    .commit();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mInitialLocale != null && !mInitialLocale.equals(LocaleHelper.getPersistedLocale(this))) {
            recreate();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}