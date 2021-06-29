package com.nilson.firebaseapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.widget.ImageView;

import com.google.android.material.navigation.NavigationView;

public class NavigationActivity extends AppCompatActivity {
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

        //recuperar o navControllar -> realiza troca de fragment
        NavController navController = Navigation.findNavController(this,R.id.nav_host_fragment);

        //Justar navController com navView(menu)

        NavigationUI.setupWithNavController(navigationView,navController);




    }
}
