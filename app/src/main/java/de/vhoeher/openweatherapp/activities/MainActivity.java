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
import de.vhoeher.openweatherapp.fragments.AboutFragment;
import de.vhoeher.openweatherapp.fragments.HistoryFragment;
import de.vhoeher.openweatherapp.util.LocaleHelper;
import de.vhoeher.openweatherapp.fragments.HomeFragment;

/**
 * This is the MainActivity. It is the central part of the application.
 * It contains a NavigationDrawer with a menu.
 * Depending on the user action it loads different Fragments in the content area.
 *
 * @author Victor Hoeher
 * @version 1.0
 */
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
        if (mSharedPreferences.getString(getString(R.string.pref_source_key), null) == null) {
            mSharedPreferences.edit().putString(getString(R.string.pref_source_key), getString(R.string.pref_source_openweathermap_value)).commit();
        }
        mInitialLocale = LocaleHelper.getPersistedLocale(this);

        //Set Toolbar
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        //Find Drawer
        mDrawerLayout = findViewById(R.id.drawer_layout);

        //Set NavigationView
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
                                //Start SettingsActivity
                                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                                startActivity(settingsIntent);
                                break;
                            case R.id.nav_home:
                                //Show HomeFragment
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.content_frame, new HomeFragment())
                                        .commit();
                                break;
                            case R.id.nav_history:
                                //Show HistoryFragment
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.content_frame, new HistoryFragment())
                                        .commit();
                                break;
                            case R.id.nav_about:
                                //Show AboutFragment
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.content_frame, new AboutFragment())
                                        .commit();
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });

        //Check if there was already a state for this activity. If not show the HomeFragment.
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
        //Check if the locale changed (language). If yes recreate the Activity
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