package com.master.plannerway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthCredential;

public class MainActivity extends AppCompatActivity {

    TextView memailTV;
    Button logout;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        memailTV = findViewById(R.id.emailTextView);
        logout = findViewById(R.id.logoutButton);
        memailTV.setText(intent.getStringExtra("email"));

        mAuth = FirebaseAuth.getInstance();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1017214591492-hk03002gapukpmtmovbllbu6065o4gth.apps.googleusercontent.com")
                .requestEmail()
                .build();


        logout.setOnClickListener(v -> {
            mAuth.signOut();

            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

            mGoogleSignInClient.signOut().addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(MainActivity.this,"Successfully Logout",Toast.LENGTH_SHORT).show();
                        switchActivity();
                    }
                    else
                        Toast.makeText(MainActivity.this,"Something Went Wrong",Toast.LENGTH_SHORT).show();
                }
            });
            switchActivity();
        });
    }

    void switchActivity(){

        Intent intent = new Intent(this,SigninActivity.class);
        startActivity(intent);
        finishAndRemoveTask();
    }

}