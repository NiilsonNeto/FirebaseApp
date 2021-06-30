package com.nilson.firebaseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nilson.firebaseapp.model.User;

public class CadastroActivity extends AppCompatActivity {
        private Button btnCadastrar;
        private EditText editEmail,editNome, editSenha;

        // Autentificacao
        private FirebaseAuth auth = FirebaseAuth.getInstance();

        //referencia para gravar os nome no database
        private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        btnCadastrar = findViewById(R.id.btn_cadastro_cadastrar);
        editEmail = findViewById(R.id.edit_cadastro_email);
        editNome = findViewById(R.id.edit_cadastro_usuario);
        editSenha = findViewById(R.id.edit_cadastro_senha);

        btnCadastrar.setOnClickListener(v -> {
            cadastrar();
        });
    }
    public void cadastrar(){
            String nome = editNome.getText().toString();
            String email = editEmail.getText().toString();
            String senha = editSenha.getText().toString();

            if(nome.isEmpty()||email.isEmpty()||senha.isEmpty()){
                Toast.makeText(this,"Preencha Corretamente",Toast.LENGTH_SHORT).show();

                return;
            }
            Task<AuthResult> t = auth.createUserWithEmailAndPassword(email,senha);
            t.addOnCompleteListener(task -> {
                //esse listener executado com sucesso ou fracasso
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Deu Bom",Toast.LENGTH_SHORT).show();
                finish();
                }else{

                    Toast.makeText(getApplicationContext(),"errou",Toast.LENGTH_SHORT).show();
                }
            });

            t.addOnSuccessListener(authResult -> {
                //request para mudar nome do usuario
                UserProfileChangeRequest update = new UserProfileChangeRequest.Builder().setDisplayName(nome).build();


                //inserir na database
                User u = new User(authResult.getUser().getUid(),email,nome);
                databaseReference.child(u.getId()).setValue(u);


                //set nome do usuario
                authResult.getUser().updateProfile(update);
            });



    }
}
