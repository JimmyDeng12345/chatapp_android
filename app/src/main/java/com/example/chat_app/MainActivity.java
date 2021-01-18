package com.example.chat_app;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static boolean isDark = false;
    private AppBarConfiguration mAppBarConfiguration;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FirebaseFirestore ff = FirebaseFirestore.getInstance();
        System.out.println("yeeeeeee");
        ff.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                System.out.println("Line 68");
                if (error != null) {
                    return;
                }
                System.out.println("Line 71");
                for (QueryDocumentSnapshot qds : value) {
                   User u = qds.toObject(User.class);
                   System.out.println("userrrrrr " + u.email + " " + u.name + " " + u.photoURL);
                }

            }
        });

        setContentView(R.layout.activity_main_page);
        RelativeLayout rl = findViewById(R.id.messages);
        rl.setVisibility(View.VISIBLE);
        makeNavBar(this);

        startActivity(new Intent(this, SignIn.class));


        startActivity(new Intent(this, MainPage.class));


    }
    public static String getUID() {
        String s = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (s == null) {
            return "";
        } else {
            return s;
        }
    }

    public static void makeNavBar(AppCompatActivity a) {
        BottomNavigationView navView = a.findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(a, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(a, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }


    public static boolean getIsDark() {
        return isDark;
    }

    public static void checkMenu(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sign_out) {
            FirebaseAuth.getInstance().signOut();

        } else if (id == R.id.dark_mode) {
            if (getIsDark()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

            }
            isDark = !isDark;
        }
    }






}