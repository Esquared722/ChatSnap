package com.sigandroid.eric.chatsnap;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    EditText email;
    EditText PassWordBox;
    Button LogIn;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        email =findViewById(R.id.UsernameBox);

        PassWordBox=findViewById(R.id.PasswordBox);

        LogIn=findViewById(R.id.logIn);

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null){
            nextActivity();

        }

        LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = email.getText().toString();
                String passwordText = PassWordBox.getText().toString();
                if(emailText.isEmpty() || passwordText.isEmpty())
                    makeText("Fill out all fields!");
                else
                    login(emailText, passwordText);


            }
        });
    }

    public void login(final String email, final String password){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                    nextActivity();
                else
                    signUp(email, password);
            }
        });
    }

    public void signUp(final String email, final String password){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    addToDatabase(email, task);
                    nextActivity();

                } else {
                    makeText("Error");
                }
            }
        });
    }

    public void addToDatabase(String email, Task<AuthResult> task){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").child(auth.getUid()).child("email").setValue(email);
    }

    public void nextActivity(){
        startActivity(new Intent(LoginActivity.this, SnapsActivity.class));
    }

    public void makeText(String message){
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();

    }
}
