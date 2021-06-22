package com.nilson.firebaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nilson.firebaseapp.model.Upload;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private Button btnLogout, btnArmazenar;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference("uploads");
    private ArrayList<Upload> listaUp = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnArmazenar = findViewById(R.id.main_btn_storage);
        btnLogout = findViewById(R.id.main_btn_logout);
        TextView textEmail = findViewById(R.id.main_text_email);
        TextView textNome = findViewById(R.id.main_text_nome);
        TextView textStogare = findViewById(R.id.main_btn_storage);




        btnArmazenar.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),StorageActivity.class);
            startActivity(intent);

        });
        //deslogar usuario
        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            finish();
        });
        textNome.setText(auth.getCurrentUser().getDisplayName());
        textEmail.setText(auth.getCurrentUser().getEmail());


    }

    @Override
    protected void onStart() {
        //onStart:
        /* Faz parte do clico de vida da Activity
        *  É executado quando app inicia
        *  e quando volta do background */
        super.onStart();
        getData();

    }

    private void getData() {
        //listener para o nó uploads
        // - caso ocorra alguma alteracao -> retorna todos os dados!!
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot no_filho : snapshot.getChildren()) {
                    Upload upload = no_filho.getValue(Upload.class);
                    listaUp.add(upload);
                    Log.i("Database","id: " + upload.getId() + ", nome: " + upload.getNomeImagem()+ ", url" + upload.getUrl());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
