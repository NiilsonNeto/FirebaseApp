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

public class CadastroActivity extends AppCompatActivity {
        private Button btnCadastrar;
        private EditText editEmail,editNome, editSenha;

        // Autentificacao
        private FirebaseAuth auth = FirebaseAuth.getInstance();

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

                //set nome do usuario
                authResult.getUser().updateProfile(update);
            });



    }
}
