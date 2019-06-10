package com.abr.rentalgetter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private EditText phoneNumber;
    private EditText myCode;
    private Button loginButton;
    private Button signupButton;
    private ProgressBar progressBar;
    private String verificationId;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                //progressBar Here
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        sigInWithCredential(credential);
    }

    private void sigInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {
                            Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void sendVerificationCode(String number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacksPhoneAuthActivity.java

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        phoneNumber = findViewById(R.id.loginPhone);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);
        myCode = findViewById(R.id.codeEditText);
        progressBar = findViewById(R.id.progressBar);


        String phone = phoneNumber.getText().toString();
        //sendVerificationCode(phone);

        View.OnClickListener myListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if (code.isEmpty() || code.length() < 6) {
                    myCode.setError("Error code...");
                    myCode.requestFocus();
                    return;
                }*/
                //progressbar Here
                progressBar.setVisibility(View.VISIBLE);
                String phone = phoneNumber.getText().toString();
                sendVerificationCode(phone);

                Toast.makeText(LoginActivity.this, "Please wait... ", Toast.LENGTH_SHORT).show();
            }
        };
        loginButton.setOnClickListener(myListener);

        View.OnClickListener myListener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = myCode.getText().toString();
                verifyCode(code);
            }
        };
        signupButton.setOnClickListener(myListener2);


    }
}
