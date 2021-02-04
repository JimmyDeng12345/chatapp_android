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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;



public class MainPage extends AppCompatActivity {

    FirebaseFirestore fdb;
    ArrayList<User> otherUsers;
    TextView tempTV;

    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        MainActivity.makeNavBar(this);

        lv = (ListView) findViewById(R.id.lv);

        tempTV = findViewById(R.id.tv_temp);

        fdb = FirebaseFirestore.getInstance();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        otherUsers = new ArrayList<>();
        ArrayList<String> al = new ArrayList<>();

        fdb.collection("users").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                tempTV.setText("");
               // tempTV.append(value.getData().toString());
            }
        });

        fdb.collection("users/" + uid + "/strangers").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }

                for (QueryDocumentSnapshot doc : value) {
                    //tempTV.append(doc.toObject(StringWrapper.class).toString());
                    func(doc.toObject(StringWrapper.class).toString());
                }

            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ChatDisplay.class);
                i.putExtra("otherUid", otherUsers.get(position).email);
                i.putExtra("photoURL", otherUsers.get(position).photoURL);
                startActivity(i);
            }
        });

        otherFunction();

    }

    private void func(String s) {

//        if (pos == temp.size() || temp.size() == 0) {
//            otherFunction();
//            return;
//        }
        if (s == null) {
            return;
        }

        fdb.collection("users").document(s).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.getResult() != null) {
                    try {
                        Map<String, Object> name = task.getResult().getData();
                        otherUsers.add(new User((String) name.get("email"), (String) name.get("name"), (String) name.get("photoURL"), s));
                      //  func(temp, pos + 1);
                        update();

                    } catch (Exception e) {
                        //throw e;
                    }
                }
            }
        });
    }

    private void update() {

        ChatListAdapter adapter = new ChatListAdapter(this, R.layout.message_display, otherUsers);
        lv.setAdapter(adapter);
    }

    private void otherFunction() {

        otherUsers.add(new User("", "Default Room", "https://media-exp1.licdn.com/dms/image/C5603AQEtPW8yKmS2PA/profile-displayphoto-shrink_800_800/0/1610343703620?e=1616630400&v=beta&t=X9_nDlsS_CRiyMPp38ySKjfxX-k57g3teAxhhp7z2yg"));;

        update();

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