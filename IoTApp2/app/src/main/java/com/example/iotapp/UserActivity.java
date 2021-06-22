package com.example.iotapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class UserActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Toolbar mtoolbar;
    private TextView mtv_edit, mtv_change, mtv_email, mtv_phone, mtv_firstname, mtv_lastname, mtv_fullName;
    FirebaseAuth mFirebaseAuth;
    FirebaseFirestore fStore;
    String userId;
    private TextView mbtn_editp;
    ImageView profileImage;
    StorageReference storageReference;
    FirebaseUser user;
    DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mFirebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user = mFirebaseAuth.getCurrentUser();

        //Hien thi profile image
        StorageReference profileRef = storageReference.child("users/"+mFirebaseAuth.getCurrentUser().getUid()+"profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });


        userId = mFirebaseAuth.getCurrentUser().getUid();

        documentReference = fStore.collection("users").document(userId);

        mtoolbar = (Toolbar) findViewById(R.id.i_toolbar);
        setSupportActionBar(mtoolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout_user);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, mtoolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        mtv_edit = findViewById(R.id.tv_editpro);
        mtv_change = findViewById(R.id.tv_changepass);
        mtv_email = findViewById(R.id.tv_email);
        mtv_phone = findViewById(R.id.tv_phone);
        mtv_firstname = findViewById(R.id.tv_firstname);
        mtv_lastname = findViewById(R.id.tv_lastname);
        mtv_fullName = findViewById(R.id.tv_fullName);
        mbtn_editp = findViewById(R.id.tv_editpro);
        profileImage = findViewById(R.id.profile_image);
        // Hien thi thong tin tai khoan user tu Firebase
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                mtv_phone.setText(documentSnapshot.getString("phone"));
                mtv_email.setText(documentSnapshot.getString("email"));
                String fullName = documentSnapshot.getString("fName");
                mtv_fullName.setText(fullName);

                String lastName = "";
                String firstName= "";
                if(fullName.split("\\w+").length>1){

                    lastName = fullName.substring(fullName.lastIndexOf(" ")+1);
                    firstName = fullName.substring(0, fullName.lastIndexOf(' '));
                }
                else{
                    firstName = fullName;
                }

                mtv_firstname.setText(firstName);
                mtv_lastname.setText(lastName);
            }
        });


        // Nut Edit Profile
        mtv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_edit = new Intent(UserActivity.this, EditProfilesActivity.class);
                intent_edit.putExtra("fullName", mtv_fullName.getText().toString());
                intent_edit.putExtra("phone", mtv_phone.getText().toString());
                intent_edit.putExtra("email", mtv_email.getText().toString());
                startActivity(intent_edit);
            }
        });

        mtv_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChangePassworDialog(Gravity.CENTER);
            }
        });

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
                Intent intent_home = new Intent(UserActivity.this, MainActivity.class);
                startActivity(intent_home);
                finish();

                break;

            case R.id.nav_user:


                break;

            case R.id.nav_sensor:
                Intent intent_sensor = new Intent(UserActivity.this, SensorActivity.class);
                startActivity(intent_sensor);
                finish();

                break;

            case R.id.nav_setting:
                Intent intent_settings = new Intent(UserActivity.this, SettingsActivity.class);
                startActivity(intent_settings);
                finish();

                break;
            case R.id.nav_logout:
                mFirebaseAuth.signOut();
                startActivity(new Intent(this, Login.class));
                finish();
                break;

            case R.id.nav_dashboard:
                Intent intent_dashboard = new Intent(UserActivity.this, CombinedChart.class);
                startActivity(intent_dashboard);
                finish();

                break;

        }



        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    // Change Password Dialog
    private void openChangePassworDialog(int gravity){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_change_password);

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

        TextView mbtnSavePassword = dialog.findViewById(R.id.btn_save_change_password);
        TextView mbtnCancelChangePassword = dialog.findViewById(R.id.btn_cancel_change_password);
        EditText editOldPass = dialog.findViewById(R.id.current_password);
        EditText editNewPass = dialog.findViewById(R.id.new_password);


        
        mbtnSavePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String OldPassworld = editOldPass.getText().toString();
                String NewPassworld = editNewPass.getText().toString();

                if(!OldPassworld.isEmpty() && !NewPassworld.isEmpty()){

                    AuthCredential credential = EmailAuthProvider.getCredential(mtv_email.getText().toString(), OldPassworld);
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            user.updatePassword(NewPassworld).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(UserActivity.this, "Password Reset Successfully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UserActivity.this, "Password Reset Failded", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UserActivity.this, "Wrong Password, Please try Again!", Toast.LENGTH_SHORT ).show();
                        }
                    });
                } else {
                    Toast.makeText(UserActivity.this, "One of Many fields are empty", Toast.LENGTH_SHORT).show();
                }


            }
        });

        mbtnCancelChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setCancelable(true);
            }
        });


        dialog.show();

    }
}