package com.sigandroid.eric.chatsnap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ViewSnapActivity extends AppCompatActivity {

    ImageView imageView;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_snap);
        imageView = findViewById(R.id.imageView);
        backButton = findViewById(R.id.backButton);
        Glide.with(this).load(getIntent().getStringExtra("url")).into(imageView);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = FirebaseAuth.getInstance().getUid();
                String snapName = getIntent().getStringExtra("snapName");
                FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("snaps").child(snapName).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getBaseContext(), "Snap Deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                startActivity(new Intent(ViewSnapActivity.this, SnapsActivity.class));
            }
        });




    }
}
