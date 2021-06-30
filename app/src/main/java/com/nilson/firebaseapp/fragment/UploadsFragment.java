package com.nilson.firebaseapp.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nilson.firebaseapp.R;
import com.nilson.firebaseapp.UpdateActivity;
import com.nilson.firebaseapp.adapter.ImagemAdapter;
import com.nilson.firebaseapp.model.Upload;
import com.nilson.firebaseapp.util.LoadingDialog;

import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadsFragment extends Fragment {

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private Button btnUpload,btnGaleria;
    private ImageView imageView;
    private Uri imageUri=null;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference("uploads")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    private ArrayList<Upload> listaUp = new ArrayList<>();

    private RecyclerView recyclerView;
    private ImagemAdapter imagemAdapter;



    public UploadsFragment(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_uploads, container, false);
        recyclerView = layout.findViewById(R.id.main_recycle);
        imagemAdapter = new ImagemAdapter(getContext(), listaUp);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        imagemAdapter.setListener(new ImagemAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                Upload upload = listaUp.get(position);
                deleteUpload(upload);

            }

            @Override
            public void onUpdateClick(int position) {
                Upload upload = listaUp.get(position);
                Intent intent = new Intent(getContext(), UpdateActivity.class);
                intent.putExtra("Upload", upload);
                startActivity(intent);


            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()
        ));


        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(imagemAdapter);



        return layout;
    }



    @Override
    public void onStart(){
        //onStart:
        /* Faz parte do clico de vida da Activity
         *  É executado quando app inicia
         *  e quando volta do background */
        super.onStart();
        getData();

    }

    public void deleteUpload(Upload upload){
        LoadingDialog dialog = new LoadingDialog(getActivity(),R.layout.custom_dialog);

        dialog.startLoadingDialog();

        //deletar no stogare
        StorageReference imagemRef = FirebaseStorage.getInstance().getReferenceFromUrl(upload.getUrl());
        imagemRef.delete().addOnSuccessListener(aVoid -> {
            //deletar imagem no database
            database.child(upload.getId()).removeValue().addOnSuccessListener(aVoid1 -> {
                Toast.makeText(getActivity(),"Arquivo deletado",Toast.LENGTH_SHORT).show();
                dialog.dismissDialog();
            });

        }).addOnFailureListener(e -> {});

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
                imagemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
