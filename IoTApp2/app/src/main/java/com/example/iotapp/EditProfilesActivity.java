package com.example.iotapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import io.grpc.Context;

public class EditProfilesActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    RelativeLayout btn_edit_back;
    EditText profileFullName, profilePhone;
    ImageView profileImageView;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    Button saveBtn, cancelBtn;
    FirebaseUser user;
    ImageButton imageBtn;
    StorageReference storageReference;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profiles);

        mtoolbar = (Toolbar) findViewById(R.id.i_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setIcon(getDrawable(R.drawable.ic_action_toolbar_back));


        Intent data = getIntent();
        String fullName = data.getStringExtra("fullName");
        String phone = data.getStringExtra("phone");
        String email1 = data.getStringExtra("email");

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();


        StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImageView);
            }
        });

        profileFullName = findViewById(R.id.edittext_name);
        profilePhone = findViewById(R.id.edittext_phone);
        profileImageView = findViewById(R.id.profile_image_view);
        saveBtn = findViewById(R.id.bt_ed_save);
        cancelBtn = findViewById(R.id.bt_ed_cancel);
        imageBtn = findViewById(R.id.imageButton);

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(profileFullName.getText().toString().isEmpty() || profilePhone.getText().toString().isEmpty())
                {
                    Toast.makeText(EditProfilesActivity.this, "One of Many fields are empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String email = email1;
                user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference docRef = fStore.collection("users").document(user.getUid());
                        Map<String, Object> edited = new HashMap<>();
                        edited.put("fName", profileFullName.getText().toString());
                        edited.put("phone", profilePhone.getText().toString());
                        docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditProfilesActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), UserActivity.class));
                                finish();
                            }
                        });
                        Toast.makeText(EditProfilesActivity.this, "Save Changed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfilesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditProfilesActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), UserActivity.class));
                finish();
            }
        });

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        profileFullName.setText(fullName);
        profilePhone.setText(phone);

        

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                //profileImageView.setImageURI(imageUri);

                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        // upload image to Firebase storage
        StorageReference fileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImageView);
                    }
                });
                Toast.makeText(EditProfilesActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfilesActivity.this, "Failed to Upload Image", Toast.LENGTH_SHORT).show();
            }
        });
    }
}