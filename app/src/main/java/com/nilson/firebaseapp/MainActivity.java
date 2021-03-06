package com.nilson.firebaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nilson.firebaseapp.adapter.ImagemAdapter;
import com.nilson.firebaseapp.model.Upload;
import com.nilson.firebaseapp.util.LoadingDialog;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private Button btnLogout, btnArmazenar;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference("uploads");
    private ArrayList<Upload> listaUp = new ArrayList<>();

    private RecyclerView recyclerView;
    private ImagemAdapter imagemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnArmazenar = findViewById(R.id.main_btn_storage);
        btnLogout = findViewById(R.id.main_btn_logout);
        TextView textEmail = findViewById(R.id.main_text_email);
        TextView textNome = findViewById(R.id.main_text_nome);
        TextView textStogare = findViewById(R.id.main_btn_storage);

        //recycle
        recyclerView = findViewById(R.id.main_recycle);
        imagemAdapter = new ImagemAdapter(getApplication(),listaUp);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        imagemAdapter.setListener(new ImagemAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                Upload upload = listaUp.get(position);
                deleteUpload(upload);

            }

            @Override
            public void onUpdateClick(int position) {
                Upload upload = listaUp.get(position);
                Intent intent = new Intent(getApplicationContext(),UpdateActivity.class);
                intent.putExtra("Upload",upload);
                startActivity(intent);


            }
        });


        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(imagemAdapter);





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
        *  ?? executado quando app inicia
        *  e quando volta do background */
        super.onStart();
        getData();

    }

    public void deleteUpload(Upload upload){
        LoadingDialog dialog = new LoadingDialog(this,R.layout.custom_dialog);

        dialog.startLoadingDialog();

        //deletar no stogare
        StorageReference imagemRef = FirebaseStorage.getInstance().getReferenceFromUrl(upload.getUrl());
        imagemRef.delete().addOnSuccessListener(aVoid -> {
            //deletar imagem no database
            database.child(upload.getId()).removeValue().addOnSuccessListener(aVoid1 -> {
                Toast.makeText(getApplicationContext(),"Arquivo deletado",Toast.LENGTH_SHORT).show();
                dialog.dismissDialog();
            });

        }).addOnFailureListener(e -> {});

    }

    private void getData() {
        //listener para o n?? uploads
        // - caso ocorra alguma alteracao -> retorna todos os dados!!
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot no_filho : snapshot.getChildren()) {
                    Upload upload = no_filho.getValue(Upload.class);
                    listaUp.add(upload);
                    Log.i("Database","id: " + upload.getId() + ", nome: " + upload.getNomeImagem()+ ", url" + upload.getUrl());
                }
                imagemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
