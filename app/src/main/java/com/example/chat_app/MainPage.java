package com.example.chat_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
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

import static android.content.ContentValues.TAG;



public class MainPage extends AppCompatActivity {
    private static FirebaseFirestore fdb;

    //private static TextView displayMessages;
    private static EditText enterMessages;
    private static Button sendMessage;
    //private static ImageView sender_photo;
    private AppBarConfiguration mAppBarConfiguration;


    public static ArrayList<Message> list;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        /*


        // Builds Top Bar Navigation
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow).setDrawerLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


*/
        enterMessages = (EditText) findViewById(R.id.textbox);
        sendMessage = (Button) findViewById(R.id.sendButton);
        mListView = (ListView) findViewById(R.id.textdisplay);
        list = new ArrayList<>();

        setUp();
    }

    private void setUp() {
        mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mListView.setStackFromBottom(true);

        fdb = FirebaseFirestore.getInstance();

        CustomListAdapter adapter = new CustomListAdapter(this, R.layout.message_display, list);
        mListView.setAdapter(adapter);

        Context con = this;

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (enterMessages.getText().toString().equals("Sign Out")) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(con, MainActivity.class));
                } else {
                    sendMessage();
                }
            }
        });

        fdb.collection("messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }

                list.clear();

                for (QueryDocumentSnapshot doc : value) {
                    list.add(doc.toObject(Message.class));
                }
                Collections.sort(list, new MessageComparator());
                updateDisplay();
            }
        });
    }

    public void updateDisplay() {

        CustomListAdapter adapter = new CustomListAdapter(this, R.layout.message_display, list);

        mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mListView.setAdapter(adapter);

    }

    public void getMessagesOnDB() {

        //MainActivity.displayMessages.append("Running" + '\n');
        fdb.collection("messages")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                List<Message> readData = task.getResult().toObjects(Message.class);

                                Object [] data = readData.toArray();
                                Arrays.sort(data);

                                for (int i = 0; i < data.length; i++) {
                                    Object o = data[i];
                                    processMessages((Message) o);
                                }



                            }
                        } else {
                            Log.w(TAG, "Goofed");
                        }
                    }
                });
    }

    public void processMessages(Message m) {

        list.add(m);
        updateDisplay();

    }

    public void sendMessage() {

        String text = enterMessages.getText().toString();
        enterMessages.setText("");

        String url = "https://www.borgenmagazine.com/wp-content/uploads/2013/09/george-bush-eating-corn.jpg";

        fdb.collection("messages").add(new Message(new Date(), url, text, MainActivity.getUID()));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.sign_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, SignIn.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}