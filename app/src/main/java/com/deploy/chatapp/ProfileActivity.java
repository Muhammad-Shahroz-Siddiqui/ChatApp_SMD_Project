package com.deploy.chatapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {


    EditText mviewusername;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    TextView mmovetoupdateprofile;

    FirebaseFirestore firebaseFirestore;

    ImageView mviewuserimageinimageview;

    StorageReference storageReference;
    androidx.appcompat.widget.Toolbar mtoolbarofviewprofile;
    ImageButton mbackbuttonofviewprofile;
    private AdView mAdView;
    FirebaseStorage firebaseStorage;
    private String ImageURIacessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        MobileAds.initialize(this);
        mviewuserimageinimageview = findViewById(R.id.viewuserimageinimageview);
        mviewusername = findViewById(R.id.viewusername);
        mmovetoupdateprofile = findViewById(R.id.movetoupdateprofile);
        firebaseFirestore = FirebaseFirestore.getInstance();
        mtoolbarofviewprofile = findViewById(R.id.toolbarofviewprofile);
        mbackbuttonofviewprofile = findViewById(R.id.backbuttonofviewprofile);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        mAdView = findViewById(R.id.adView);


        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);



        setSupportActionBar(mtoolbarofviewprofile);

        mbackbuttonofviewprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        storageReference = firebaseStorage.getReference();
        storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageURIacessToken = uri.toString();
                Picasso.get().load(uri).into(mviewuserimageinimageview);

            }
        });

        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userprofile muserprofile = snapshot.getValue(userprofile.class);
                mviewusername.setText(muserprofile.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getApplicationContext(), "Failed To Fetch", Toast.LENGTH_SHORT).show();
            }
        });


        mmovetoupdateprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, UpdateProfile.class);
                intent.putExtra("nameofuser", mviewusername.getText().toString());
                startActivity(intent);
            }
        });


    }


    @Override
    protected void onStop() {
        super.onStop();
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status", "Offline").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Now User is Offline", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status", "Online").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Now User is Online", Toast.LENGTH_SHORT).show();
            }
        });

    }
}