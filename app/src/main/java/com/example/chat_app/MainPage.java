package com.example.chat_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;



public class MainPage extends AppCompatActivity {

    FirebaseFirestore fdb;
    ArrayList<User> otherUsers;
    TextView tempTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        MainActivity.makeNavBar(this);

        fdb = FirebaseFirestore.getInstance();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        otherUsers = new ArrayList<>();
        ArrayList<String> al = new ArrayList<>();

        fdb.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.getResult() != null) {
                    try {
                        ArrayList<String> temp = (ArrayList<String>) task.getResult().get("conversations");
                        func(temp, 0);

                    } catch (Exception e) {}
                }
            }
        });

    }

    private void func(ArrayList<String> temp, int pos) {

        if (pos == temp.size() || temp.size() == 0) {
            otherFunction();
            return;
        }

        fdb.collection("users").document(temp.get(pos)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.getResult() != null) {
                    try {
                        Map<String, Object> name = task.getResult().getData();
                        otherUsers.add(new User((String) name.get("email"), (String) name.get("name"), (String) name.get("photoURL")));
                        func(temp, pos + 1);

                    } catch (Exception e) {

                    }
                }


            }
        });
    }

    private void otherFunction() {

        ListView lv = (ListView) findViewById(R.id.lv);

        otherUsers.add(new User("No Email", "Default Room", "https://media-exp1.licdn.com/dms/image/C5603AQEtPW8yKmS2PA/profile-displayphoto-shrink_800_800/0/1610343703620?e=1616630400&v=beta&t=X9_nDlsS_CRiyMPp38ySKjfxX-k57g3teAxhhp7z2yg"));;

        ChatListAdapter adapter = new ChatListAdapter(this, R.layout.message_display, otherUsers);
        lv.setAdapter(adapter);

        Context con = this;

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //User item = parent.getItemAtPosition(position);

                Intent intent = new Intent(con, ChatDisplay.class);
                //based on item add info to intent
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //getMessagesOnDB();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if (id == R.id.sign_out) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, SignIn.class));

        } else if (id == R.id.dark_mode) {
            if (MainActivity.getIsDark()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

            }
            MainActivity.isDark = !MainActivity.isDark;
        }
        return true;
    }

}