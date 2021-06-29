package com.nilson.firebaseapp.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nilson.firebaseapp.R;
import com.nilson.firebaseapp.model.Upload;
import com.nilson.firebaseapp.util.LoadingDialog;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadsFragment extends Fragment {

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private Button btnUpload,btnGaleria;
    private ImageView imageView;
    private Uri imageUri=null;
    private EditText editNome;
    // referencia p/ um nó RealtimeDB
    private DatabaseReference database = FirebaseDatabase.getInstance()
            .getReference("uploads");
    private Upload upload;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_uploads, container, false);

        btnUpload = layout.findViewById(R.id.update_btn_upload);
        imageView = layout.findViewById(R.id.update_image_cel);
        btnGaleria = layout.findViewById(R.id.update_btn_galeria);
        editNome = layout.findViewById(R.id.update_edit_nome);

        //recuperar o upload selecionado
        upload = (Upload) getIntent().getSerializableExtra("Upload");
        editNome.setText(upload.getNomeImagem());

        Glide.with(this).load(upload.getUrl()).into(imageView);

        
        btnGaleria.setOnClickListener( v -> {
            Intent intent = new Intent();
            //intent implicita -> pegar um arquivo do celular
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            // inicia uma Activity, e espera o retorno(foto)
            startActivityForResult(intent,112);
        });
        btnUpload.setOnClickListener( v -> {
            if(editNome.getText().toString().isEmpty()){
                Toast.makeText(getActivity(),"Sem Nome",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            //caso imagem nao tenho sido atualizada
            if(imageUri==null){
                //atualizar o nome da imagem
                String nome = editNome.getText().toString();
                upload.setNomeImagem(nome);
                database.child(upload.getId()).setValue(upload).addOnSuccessListener(aVoid -> {

                    NavController navController = Navigation.findNavController(getActivity(),R.id.nav_host_fragment);

                    //voltar para o fagmento incial
                    navController.navigateUp();
                });
                return;
            }

            AtualizarImagem();
        });

        return layout;

    }
    private void AtualizarImagem() {
        //deletar a imagem antiga
        storage.getReferenceFromUrl(upload.getUrl()).delete();
        //fazer upload da imagem atualizada
        uploadImagemUri();
        //listener para recuperar url da imagem no storage

        //atualizar o database

    }
    private void uploadImagemUri() {

        LoadingDialog dialog = new LoadingDialog(getActivity(),R.layout.custom_dialog);
        dialog.startLoadingDialog();

        String tipo = getFileExtension(imageUri);
        //referencia do arquivo no firebase
        Date d = new Date();
        String nome = editNome.getText().toString();

        // criando referencia da imagem no Storage
        StorageReference imagemRef = storage.getReference()
                .child("imagens/"+nome+
                        "-"+d.getTime()+"."+tipo);

        imagemRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(getActivity(),"Upload feito com sucesso",
                            Toast.LENGTH_SHORT).show();

                    /* inserir dados da imagem no RealtimeDatabase */

                    //pegar a URL da imagem
                    taskSnapshot.getStorage().getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                // atualizar no database

                                //atualizar o ojeto upload
                                upload.setUrl(uri.toString());
                                upload.setNomeImagem(editNome.getText().toString());

                                database.child(upload.getId()).setValue(upload).addOnSuccessListener(aVoid -> {
                                    dialog.dismissDialog();
                                    NavController navController = Navigation.findNavController(getActivity(),R.id.nav_host_fragment);

                                    //voltar para o fagmento incial
                                    navController.navigateUp();
                                });


                            });

                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });

    }
    private String getFileExtension(Uri imageUri) {
        ContentResolver cr = getActivity().getContentResolver();
        return MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(cr.getType(imageUri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("RESULT", "requestCode: "+ requestCode +
                ",resultCode: "+ resultCode);
        if(requestCode==112 && resultCode== Activity.RESULT_OK){
            //caso o usuario selecionou uma imagem da galeria

            //endereco da imagem selecionada
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }
}
