package com.example.iotapp;

import com.example.iotapp.DataHolder;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Set;

public class SensorActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Toolbar mtoolbar;
    private BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Intent btEnablingInten;
    int requesCodeForEnable;
    ListView list_device;
    Button mbtn_searchdv;
    ArrayList<String> stringArrayList = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;
    TextView temp, hum;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        temp = findViewById(R.id.tv_temp);
        hum = findViewById(R.id.tv_hum);


        mtoolbar = (Toolbar) findViewById(R.id.i_toolbar);
        setSupportActionBar(mtoolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout_sensor);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, mtoolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        if(!myBluetoothAdapter.isEnabled()){
            openBluetoothDialog(Gravity.CENTER);

            IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(myReceiver, intentFilter);

            arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, stringArrayList);
            list_device.setAdapter(arrayAdapter);
        }

        Thread2 t = new Thread2();
        t.start();

    }

    Handler handler1 = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            temp.setText(String.valueOf(msg.arg1));
            return false;
        }
    });

    Handler handler2 = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            hum.setText(String.valueOf(msg.arg1));
            return false;
        }
    });

    private class Thread2 extends Thread
    {
        public void run()
        {
            while(true)
            {
                Message message1 = Message.obtain();
                message1.arg1= (int) (Math.random() * 11) + 30;
                handler1.sendMessage(message1);

                DataHolder.getInstance().setData(message1.arg1);

                Message message2 = Message.obtain();
                message2.arg1=(int) (Math.random() * 41) + 60;
                handler2.sendMessage(message2);

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }









    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requesCodeForEnable) {
            if (requestCode == RESULT_OK) {
                // Toast
            }
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
                Intent intent_home = new Intent(SensorActivity.this, MainActivity.class);
                startActivity(intent_home);
                finish();

                break;

            case R.id.nav_user:
                Intent intent_user = new Intent(SensorActivity.this, UserActivity.class);
                startActivity(intent_user);
                finish();

                break;

            case R.id.nav_sensor:


                break;

            case R.id.nav_setting:
                Intent intent_settings = new Intent(SensorActivity.this, SettingsActivity.class);
                startActivity(intent_settings);
                finish();

                break;

            case R.id.nav_dashboard:
                Intent intent_dashboard = new Intent(SensorActivity.this, CombinedChart.class);
                startActivity(intent_dashboard);
                finish();
                break;

        }


        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openBluetoothDialog(int gravity){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_bluetooth_enable);

        Window window = dialog.getWindow();
        if(window == null){
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = gravity;
        window.setAttributes(windowAttribute);

        if(Gravity.CENTER == gravity){
            dialog.setCancelable(true);
        } else {
            dialog.setCancelable(false);
        }

        Button mbtnEnable = dialog.findViewById(R.id.btn_enable_bluetooth);
        Button mbtnCancel = dialog.findViewById(R.id.btn_cancel_bluetooth);
        list_device =  dialog.findViewById(R.id.list_dv);
        mbtn_searchdv =  dialog.findViewById(R.id.btn_searchdv);
        btEnablingInten = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        requesCodeForEnable = 1;


        mbtnEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myBluetoothAdapter == null)
                {
                    Toast.makeText(getApplicationContext(), "Bluetooth does not support on this Device",Toast.LENGTH_LONG).show();
                } else
                    {
                        if(!myBluetoothAdapter.isEnabled()){
                            startActivityForResult(btEnablingInten,requesCodeForEnable);
                        }
                    }

            }
        });

        mbtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_home2 = new Intent(SensorActivity.this, MainActivity.class);
                startActivity(intent_home2);
                finish();
            }
        });

        mbtn_searchdv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!myBluetoothAdapter.isEnabled())
                {
                    Toast.makeText(SensorActivity.this, "Please enable your Bluetooth!", Toast.LENGTH_SHORT).show();
                } else
                {
                    myBluetoothAdapter.startDiscovery();
                }
            }
        });



        dialog.show();

    }

    BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                stringArrayList.add(device.getName());
                arrayAdapter.notifyDataSetChanged();
            }
        }
    };








}