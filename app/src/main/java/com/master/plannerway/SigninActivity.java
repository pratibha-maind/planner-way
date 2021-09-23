package com.master.plannerway;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class SigninActivity  extends AppCompatActivity {
    EditText memailET,mpasswordET;
    TextView mforgotPasswordTV;

    Button msignupButton,msigninButton;
    SignInButton mgoogleSigninButton;
    private FirebaseAuth mAuth;
    ProgressBar mprogressBar;
    private String memail,mpassword;
    GoogleSignInClient mgoogleSigninClient;
    GoogleSignInOptions gso;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 199;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);


        mforgotPasswordTV = findViewById(R.id.forgotpasswordTextView);
        msignupButton = findViewById(R.id.createAccountButton);
        msigninButton = findViewById(R.id.signinButton);
        memailET = findViewById(R.id.emailEditText);
        mpasswordET = findViewById(R.id.passwordEditText);
        mgoogleSigninButton = findViewById(R.id.googlesigninButton);
        mprogressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1017214591492-hk03002gapukpmtmovbllbu6065o4gth.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mgoogleSigninClient = GoogleSignIn.getClient(this,gso);
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentuser = mAuth.getCurrentUser();

        if(currentuser!=null){
            switchActivity(currentuser);
            finishAffinity();
        }

        mpasswordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length()<8 || s.toString().contains(" ")){
                    mpasswordET.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_error,0);
                }
                else
                {
                    mpasswordET.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().contains(" ")){
                    Toast.makeText(SigninActivity.this,"Password Shouldn't Contain any spaces",Toast.LENGTH_LONG).show();
                }
            }
        });

        memailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                memailET.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        msignupButton.setOnClickListener(v-> signup());

        msigninButton.setOnClickListener(v->signin());

        mforgotPasswordTV.setOnClickListener(v-> forgotPassword());

        mgoogleSigninButton.setOnClickListener(v->googlesignIn());
    }

    void signup(){
        memail = memailET.getText().toString();
        mpassword = mpasswordET.getText().toString();

        memailET.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0);
        mpasswordET.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0);

        if(memail.isEmpty() ) {
            Toast.makeText(SigninActivity.this, "Enter Email to Create Account", Toast.LENGTH_SHORT).show();
            memailET.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_error, 0);
        }
        else if(mpassword.length()<8)
            mpasswordET.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_error,0);
        else{
            mprogressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(memail,mpassword).addOnCompleteListener(SigninActivity.this, task -> {

                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    if(user!=null) {
                        switchActivity(user);
                    }
                    else {
                        Toast.makeText(SigninActivity.this,"Something went Wrong",Toast.LENGTH_SHORT).show();
                        recreate();
                    }

                }
                else{
                    mprogressBar.setVisibility(View.GONE);
                    Toast.makeText(SigninActivity.this,"Please check your Email",Toast.LENGTH_SHORT).show();

                    memailET.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_error,0);
                    try {
                        throw Objects.requireNonNull(task.getException());

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("SignupException","Exception in creating Account");
                    }

                }

            });
        }
    }

    void signin(){
        memail = memailET.getText().toString();
        mpassword = mpasswordET.getText().toString();

        memailET.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0);
        mpasswordET.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0);

        if(memail.isEmpty()){
            Toast.makeText(SigninActivity.this,"Enter Email to Login or Use Google Sign in",Toast.LENGTH_SHORT).show();
            memailET.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_error,0);
        }
        else if(mpassword.length()<8){
            mpasswordET.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_error,0);
        }
        else{
            mprogressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(memail,mpassword).addOnCompleteListener(SigninActivity.this, task -> {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    if(user!=null) {
                        switchActivity(user);
                    }
                    else{
                        mprogressBar.setVisibility(View.GONE);
                        Toast.makeText(SigninActivity.this,"Something went Wrong",Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    mprogressBar.setVisibility(View.GONE);
                    mpasswordET.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_error,0);
                    memailET.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_error,0);
                    Toast.makeText(SigninActivity.this,"Check Entered Credentials",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    void forgotPassword(){
        memail =  memailET.getText().toString();
        if(memail.isEmpty()){
            Toast.makeText(SigninActivity.this,"Enter Email to Reset Password",Toast.LENGTH_LONG).show();
            memailET.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_error,0);
        }
        else{
            mAuth.sendPasswordResetEmail(memail).addOnCompleteListener(this,task -> {
                if(task.isSuccessful()){
                    Toast.makeText(SigninActivity.this,"Password Reset link sent to "+memail,Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(SigninActivity.this,"Check Your Email",Toast.LENGTH_SHORT).show();
                    memailET.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_error,0);
                }
            });
        }


    }
    void switchActivity(FirebaseUser user){

        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("email",user.getEmail());
        startActivity(intent);
        finishAffinity();
    }

    private void googlesignIn() {

        Intent signInIntent = mgoogleSigninClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        mprogressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.d(TAG,"Signinfailed",task.getException());
                mprogressBar.setVisibility(View.GONE);
                Toast.makeText(SigninActivity.this,"Google Sign in Failed",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    mprogressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user!=null)
                            switchActivity(user);
                        else{
                            Log.d(TAG,"Signinfailed",task.getException());
                            Toast.makeText(SigninActivity.this,"Google Sign in Failed",Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Log.d(TAG,"Signinfailed",task.getException());
                        Toast.makeText(SigninActivity.this,"Google Sign in Failed",Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
