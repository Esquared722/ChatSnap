package com.sigandroid.eric.chatsnap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewSnapsActivity extends AppCompatActivity {
    String imageFileName, imageFilePath;
    ImageView imageView;
    Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_snaps);
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageView = findViewById(R.id.imageView);
        send = findViewById(R.id.send);

        File photo = createImageFile();
        Uri photoUri = FileProvider.getUriForFile(this,"com.sigandroid.eric.chatsnap.provider",photo);
        i.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(i, 0);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send(v);
            }
        });

    }

    private File createImageFile(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd__HHmmss", Locale.getDefault()).format(new Date());
        imageFileName = "IMG_" + timeStamp;
        File image = null;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try{

            image = File.createTempFile(imageFileName, ".jpg", storageDir);
            imageFileName += ".jpg";
            imageFilePath = image.getAbsolutePath();

        } catch (IOException e) {

            e.printStackTrace();
        }

        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        File file = null;
        if(resultCode == -1){
            Glide.with(this).load(imageFilePath).into(imageView);

        } else if(resultCode == 0){
            startActivity(new Intent(NewSnapsActivity.this, SnapsActivity.class));
        }

    }

    private void send(View view){
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();


        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference ref = FirebaseStorage.getInstance().getReference().child("images").child(imageFileName);
        UploadTask task = ref.putBytes(data);

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getBaseContext(), e.getStackTrace().toString(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Intent i = new Intent(NewSnapsActivity.this, SendToActivity.class);
                i.putExtra("imgname", imageFileName);
                startActivity(i);
            }
        });



    }
}
