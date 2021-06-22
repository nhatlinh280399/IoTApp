package com.example.iotapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;


import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

   private DrawerLayout drawerLayout;

   private Toolbar mtoolbar;
   private RelativeLayout mrt_user, mrt_sensor, mrt_settings, mrt_account_settings;

   private FirebaseAuth mFirebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAuth = FirebaseAuth.getInstance();

        mrt_user = findViewById(R.id.rt_user);
        mrt_sensor = findViewById(R.id.rt_sensor);
        mrt_settings = findViewById(R.id.rt_settings);
        mrt_account_settings = findViewById(R.id.rt_account);


        mtoolbar = (Toolbar) findViewById(R.id.i_toolbar);
        setSupportActionBar(mtoolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);


        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, mtoolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();



        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.home);

        mrt_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_user = new Intent(MainActivity.this, UserActivity.class);
                startActivity(intent_user);
                finish();
            }
        });

        mrt_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_settings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent_settings);
                finish();
            }
        });

        mrt_sensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_sensor = new Intent(MainActivity.this, SensorActivity.class);
                startActivity(intent_sensor);
                finish();
            }
        });

        mrt_account_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_account = new Intent(MainActivity.this, EditProfilesActivity.class);
                startActivity(intent_account);
                finish();
            }
        });


    }

    @Override
    protected void onStart(){
        super.onStart();

        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if(mFirebaseUser != null){
            //có tk đăng nhập
        } else {
            startActivity(new Intent(this, Login.class));
            finish();
        }
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
                break;

            case R.id.nav_user:
                Intent intent_user = new Intent(MainActivity.this, UserActivity.class);
                startActivity(intent_user);
                finish();

                break;

            case R.id.nav_sensor:
                Intent intent_sensor = new Intent(MainActivity.this, SensorActivity.class);
                startActivity(intent_sensor);
                finish();

                break;

            case R.id.nav_setting:
                Intent intent_settings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent_settings);
                finish();

                break;

            case R.id.nav_facebook:
                String video_path = "https://www.youtube.com/watch?v=dQw4w9WgXcQ&ab_channel=RickAstleyVEVO";
                Uri uri = Uri.parse(video_path);

                uri = Uri.parse("vnd.youtube:" + uri.getQueryParameter("v"));
                Intent intent_yt = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent_yt);
                break;

            case R.id.nav_logout:
                mFirebaseAuth.signOut();
                startActivity(new Intent(this, Login.class));
                finish();
                break;

            case R.id.nav_dashboard:
                Intent intent_dashboard = new Intent(MainActivity.this, CombinedChart.class);
                startActivity(intent_dashboard);
                finish();
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}