package com.example.iotapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Toolbar mtoolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mtoolbar = (Toolbar) findViewById(R.id.i_toolbar);
        setSupportActionBar(mtoolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout_settings);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, mtoolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.home:
                Intent intent_home = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent_home);
                finish();

                break;

            case R.id.nav_user:
                Intent intent_user = new Intent(SettingsActivity.this, UserActivity.class);
                startActivity(intent_user);
                finish();

                break;

            case R.id.nav_sensor:
                Intent intent_sensor = new Intent(SettingsActivity.this, SensorActivity.class);
                startActivity(intent_sensor);
                finish();

                break;

            case R.id.nav_setting:

                break;

            case R.id.nav_dashboard:
                Intent intent_dashboard = new Intent(SettingsActivity.this, CombinedChart.class);
                startActivity(intent_dashboard);
                finish();
                break;

        }


        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}