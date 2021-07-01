package com.nilson.firebaseapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nilson.firebaseapp.R;
import com.nilson.firebaseapp.adapter.ImagemAdapter;
import com.nilson.firebaseapp.adapter.UserAdapter;
import com.nilson.firebaseapp.model.User;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    private RecyclerView recyclerContatos;
    private UserAdapter userAdapter;
    private DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("usuarios");
    private  DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("requests");
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ArrayList<User> listaContatos = new ArrayList<>();
    private User userLogged;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout =  inflater.inflate(R.layout.fragment_main, container, false);
        userLogged = new User(auth.getCurrentUser().getUid(),
                auth.getCurrentUser().getEmail(),
                auth.getCurrentUser().getDisplayName());

        recyclerContatos = layout.findViewById(R.id.frag_main_recycler_user);

        userAdapter = new UserAdapter(getContext(),listaContatos);
        userAdapter.setListener(new UserAdapter.ClickAdapterUser() {
            @Override
            public void adicionarContato(int position) {
                User u = listaContatos.get(position);
                //request send
                requestRef.child(userLogged.getId()).child("send").child(u.getId()).setValue(u);
                //request reaceive
                requestRef.child(u.getId()).child("recieve").child(userLogged.getId()).setValue(userLogged);

                //tirar usuario solicitado
                listaContatos.get(position).setReaceiveRequest(true);
                userAdapter.notifyDataSetChanged();

            }
        });
        recyclerContatos.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerContatos.setAdapter(userAdapter);


        return layout;

    }

    @Override
    public void onStart() {
        super.onStart();
        getUserDatabase();
    }

    public void  getUserDatabase(){
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaContatos.clear();
                int cont = 1;
                for(DataSnapshot filho : snapshot.getChildren()){
                    User u = filho.getValue(User.class);
                    // comparar com usuario logando
                    if(!userLogged.equals(u)){
                       /* if(cont%2==0){
                            u.setReaceiveRequest(true);
                        }else{
                            u.setReaceiveRequest(false);
                        }*/

                        listaContatos.add(u);
                       // c
                        //  ont++;

                    }
                }

                requestRef.child(userLogged.getId()).child("send").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot no_filho: snapshot.getChildren()){
                            User usuarioSolicitado = no_filho.getValue(User.class);
                            for (int i=0; i< listaContatos.size();i++){
                                if(listaContatos.get(i).equals(usuarioSolicitado)){
                                    listaContatos.get(i).setReaceiveRequest(true);
                                }
                            }
                        }

                        userAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
