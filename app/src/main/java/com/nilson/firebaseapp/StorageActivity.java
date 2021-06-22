package com.nilson.firebaseapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nilson.firebaseapp.model.Upload;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class StorageActivity extends AppCompatActivity {
    // Referencia para o FirebaseStorage
    private FirebaseStorage armazenar = FirebaseStorage.getInstance();
    private ImageView storage_imagem;
    private Button btnUpload, btnGaleria;
    private Uri imagemUri = null;
    private EditText storage_edit_nome;

    //referencia para um no Realtimedatabase
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference("uploads");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);
        btnUpload = findViewById(R.id.storage_btn_upload);
        btnGaleria = findViewById(R.id.storage_btn_galeria);
        storage_imagem = findViewById(R.id.storage_image_cel);
        storage_edit_nome = findViewById(R.id.storage_edit_nome);







        btnGaleria.setOnClickListener(v -> {
            Intent intent = new Intent();
            //imagem diferenciada ->
            intent.setAction(intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent,111);

        });

        btnUpload.setOnClickListener(v -> {
            if(storage_edit_nome.getText().toString().isEmpty()){
                Toast.makeText(this,"Preencha corretamente",Toast.LENGTH_SHORT).show();
                return;
            }
            if(imagemUri!=null){
                uploadImagemUri();
            }else {
                uploadImagemByte();
            }
        });
    }

    private void uploadImagemUri() {
        String tipo = getFileExtension(imagemUri);
        //referencia do arquivo no Firebase
        Date d = new Date();
        String nome = storage_edit_nome.getText().toString();

        //criando referencia da imagem no Storage
        StorageReference imageRef = armazenar.getReference().child("imagem/"+nome+"-"+ d.getTime()+"."+tipo);

        imageRef.putFile(imagemUri).addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(this,"Upload Foi suave",Toast.LENGTH_SHORT).show();



            /*inserir dados da imagem no Realtimedatabase*/





            //pegar url da imagem
            taskSnapshot.getStorage().getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                       //inserir no database

                        DatabaseReference refUpload = database.push();
                        String id = refUpload.getKey();
                        Upload upload = new Upload(id,nome,uri.toString());
                        //salvando upload no database
                        refUpload.setValue(upload);

                    }).addOnFailureListener( e -> {

            });


        }).addOnFailureListener(e -> {
            e.printStackTrace();
            Toast.makeText(this,"Upload deu ruim",Toast.LENGTH_SHORT).show();
        });
    }

    //retornar o tipo(.png, . jpg) da imagem
    private String getFileExtension(Uri imagemUri) {
        ContentResolver cr = getContentResolver();
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(cr.getType(imagemUri));
    }
    //resultado da startActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("Result", "requestCode: "+ requestCode + ", resultCode "+ resultCode );
        if(requestCode==117 && resultCode == Activity.RESULT_OK){
            //CASO O USUARIO SELECIONOU UMA IMAGEM DA GALERIA

            //endereco da imagem selecionada
            imagemUri = data.getData();
            storage_imagem.setImageURI(imagemUri);

        }
    }

    public byte[] convertImagemtoByte(ImageView storage_imagem){
        //converter ImageView -> byte[]
        Bitmap bitmap = ((BitmapDrawable)  storage_imagem.getDrawable()).getBitmap();
        //objeto baos -> armazenar imagem convertida
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        return baos.toByteArray();
    }



    //upload de uma imagem convertida para bytes
    public void uploadImagemByte(){
        byte[] data = convertImagemtoByte(storage_imagem);


        //criar uma referencia para a imagem no Storage
        StorageReference imagemRef = armazenar.getReference().child("imagem/01.jpeg");

        //realiza o upload da imagem
        imagemRef.putBytes(data).addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(this,"Foi!",Toast.LENGTH_SHORT).show();


        }).addOnFailureListener(e -> {
            Toast.makeText(this,"Não Foi!",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        })
        ;


        //armazenar.getReference().putBytes();
    }
}
