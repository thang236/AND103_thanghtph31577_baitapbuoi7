package com.duyle.lap1.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.duyle.lap1.R;
import com.duyle.lap1.ui.bt7.ListXeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChoiceLoginActivity extends AppCompatActivity {
    private Button btnemail,btnOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_login);

        btnemail = findViewById(R.id.btnemail);
        btnOtp = findViewById(R.id.btnOtp);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(ChoiceLoginActivity.this, ListXeActivity.class);
            startActivity(intent);
            finish();
        }

        btnemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(ChoiceLoginActivity.this, LoginActivity.class);
                startActivity(in);
            }
        });
        btnOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(ChoiceLoginActivity.this, LoginOtpActivity.class);
                startActivity(in);
            }
        });
    }
}