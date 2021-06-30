package com.nilson.firebaseapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class NavigationActivity extends AppCompatActivity {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ImageView btnMenu;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        btnMenu = findViewById(R.id.navigation_icon);
        drawerLayout = findViewById(R.id.nav_drawerLayout);

        btnMenu.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });
        NavigationView navigationView = findViewById(R.id.navegationView);


        //TextView textNome = navigationView.getHeaderView( 0 ).findViewById(R.id.nav_header_nome);
        //TextView textEmail = navigationView.getHeaderView( 0 ).findViewById(R.id.nav_header_email);
        //textNome.setText(auth.getCurrentUser().getDisplayName());
        //textEmail.setText(auth.getCurrentUser().getEmail());


        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.nav_header_nome)).setText(auth.getCurrentUser().getDisplayName());
        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.nav_header_email)).setText(auth.getCurrentUser().getEmail());




        //recuperar o navControllar -> realiza troca de fragment
        NavController navController = Navigation.findNavController(this,R.id.nav_host_fragment);

        //Justar navController com navView(menu)

        NavigationUI.setupWithNavController(navigationView,navController);

        //Evento logout

        navigationView.getMenu().findItem(R.id.nav_menu_logout).setOnMenuItemClickListener(item -> {
            auth.signOut();
            finish();
            return false;
        });

    }
}
