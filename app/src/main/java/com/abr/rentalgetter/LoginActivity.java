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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private EditText phoneNumber;
    private EditText myCode;
    private Button sendCodeButton, verifyButton, registrationButton;
    private ProgressBar progressBar;
    private String verificationId;
    private FirebaseAuth mAuth;
    private boolean isUserRegistered;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                myCode.setText(code);
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
            Toast.makeText(LoginActivity.this, "Verification faild! " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            Intent intent = new Intent(LoginActivity.this, FirstActivity.class);
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

    @Override
    protected void onStart() {
        super.onStart();
        //Check on start in user is already logged in, then directly open first activity
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, FirstActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        phoneNumber = findViewById(R.id.loginPhone);
        sendCodeButton = findViewById(R.id.sendCodeButton);
        verifyButton = findViewById(R.id.verifyButton);
        myCode = findViewById(R.id.codeEditText);
        progressBar = findViewById(R.id.progressBar);
        registrationButton = findViewById(R.id.registrationButton);

        sendCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phone = phoneNumber.getText().toString();
                if (phone.isEmpty() || phone.length() < 13) {
                    phoneNumber.setError("Enter phone number");
                    phoneNumber.requestFocus();
                    return;
                }
                DatabaseReference databaseReference;

                databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                databaseReference.orderByChild("phone").equalTo(phone).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        isUserRegistered = false;

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            String dbphone = ds.child("PhoneNumber").getValue().toString();

                            if (phone.equals(dbphone)) {

                                isUserRegistered = true;
                                break;

                            }

                        }
                        if (isUserRegistered) {

                            Toast.makeText(getApplicationContext(), "User is already registered, Try ONE-TAP LOGIN", Toast.LENGTH_LONG).show();

                            sendVerificationCode(phone);
                            progressBar.setVisibility(View.VISIBLE);
                            Toast.makeText(LoginActivity.this, "Please wait... ", Toast.LENGTH_SHORT).show();

                        } else {
                            //new activity for registration
//                            Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            intent.putExtra(EXTRA_MESSAGE, message);
//                            intent.putExtra("number",PhoneNumber);
//                            intent.putExtra("countryCodeMobNumber",fullNumber);
//                            intent.putExtra("verificationIdSent",phoneVerificationId);
//                            intent.putExtra("reSendToken",resendingToken);
//                            startActivity(intent);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(LoginActivity.this, "DB ERROR", Toast.LENGTH_SHORT).show();

                    }
                });


            }
        });

        View.OnClickListener myListener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String code = myCode.getText().toString();
                if (code.isEmpty()) {
                    myCode.setError("Enter OTP");
                    myCode.requestFocus();
                    return;
                }
                verifyCode(code);
            }
        };
        verifyButton.setOnClickListener(myListener2);


        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}
