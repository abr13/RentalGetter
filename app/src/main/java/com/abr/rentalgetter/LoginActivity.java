package com.abr.rentalgetter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    EditText phoneNumber;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        phoneNumber = findViewById(R.id.loginPhone);
        loginButton = findViewById(R.id.loginButton);

        View.OnClickListener myListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //aaaaaa
                String n = phoneNumber.getText().toString();
                Toast.makeText(LoginActivity.this, "Login Button Pressed " + n, Toast.LENGTH_SHORT).show();
            }
        };
        loginButton.setOnClickListener(myListener);


    }
}
