package com.nilson.firebaseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private Button btnLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLogout = findViewById(R.id.main_btn_logout);
        TextView textEmail = findViewById(R.id.main_text_email);
        TextView textNome = findViewById(R.id.main_text_nome);
        TextView textStogare = findViewById(R.id.main_btn_storage);

        //deslogar usuario
        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            finish();
        });
        textNome.setText(auth.getCurrentUser().getDisplayName());
        textEmail.setText(auth.getCurrentUser().getEmail());


    }
}
