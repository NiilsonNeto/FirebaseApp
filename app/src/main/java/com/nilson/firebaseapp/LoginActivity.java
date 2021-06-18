package com.nilson.firebaseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {
    private Button btnCadastrar, btnLogin;
    private EditText editEmail, editSenha;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLogin =findViewById(R.id.btn_login);
        btnCadastrar =findViewById(R.id.btn_cadastro);
        editEmail = findViewById(R.id.edit_login_email);
        editSenha = findViewById(R.id.edit_login_senha);
//Caso logado nilson.neto1998@gmail.com

        if(auth.getCurrentUser()!=null){
            String email = auth.getCurrentUser().getEmail();
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            intent.putExtra("email",email);
            startActivity(intent);
        }
        btnCadastrar.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),CadastroActivity.class);
            startActivity(intent);
        });
        btnLogin.setOnClickListener(v -> {
            login();
        });
    }
    public void login(){
        String email = editEmail.getText().toString();
        String senha = editSenha.getText().toString();

        if(email.isEmpty()||senha.isEmpty()){
            Toast.makeText(this,"Preencha Corretamente",Toast.LENGTH_SHORT).show();
            return;
        }
        //para logar
        Task<AuthResult> t = auth.signInWithEmailAndPassword(email,senha);

        //listener sucesso
        t.addOnSuccessListener(authResult ->{
            Toast.makeText(this,"Entro",Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        });
        //e para exception
        t.addOnFailureListener(e -> {
            //Toast.makeText(this,"Entro nao",Toast.LENGTH_SHORT).show();
            //Log.e("entro nao",e.getMessage() + "classe: " + e.getClass().toString());

           try{
               throw e;
           }catch (FirebaseAuthInvalidUserException userException){
               //Exceção para email
               Toast.makeText(this,"Erro o email bobao",Toast.LENGTH_SHORT).show();
           }catch(FirebaseAuthInvalidCredentialsException credException){
               //Exceção para senha
               Toast.makeText(this,"Erro a senha bobao",Toast.LENGTH_SHORT).show();
           }catch(Exception ex){
               //Exceção generica
               Toast.makeText(this,"Entro nao",Toast.LENGTH_SHORT).show();
           }
        });

    }
}
