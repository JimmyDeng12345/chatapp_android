package com.example.chat_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ChatDisplay extends AppCompatActivity {

    private static FirebaseFirestore fdb;

    private static TextView displayMessages;
    private static EditText enterMessages;
    private static ImageView sender_photo;
    private AppBarConfiguration mAppBarConfiguration;


    public static ArrayList<Message> list;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_display);

        fdb = FirebaseFirestore.getInstance();

        enterMessages = (EditText) findViewById(R.id.textbox);
        Button sendMessage = (Button) findViewById(R.id.sendButton);
        mListView = (ListView) findViewById(R.id.textdisplay);
        list = new ArrayList<>();

        mListView.setDivider(null);
        mListView.setDividerHeight(0);

        mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mListView.setStackFromBottom(true);

        CustomListAdapter adapter = new CustomListAdapter(this, R.layout.message_display, list);
        mListView.setAdapter(adapter);

        Context con = this;

        String myUid = FirebaseAuth.getInstance().getUid();

        Bundle extras = getIntent().getExtras();

        String otherUid = extras.getString("otherUid");
        String otherURL = extras.getString("photoURL");

        sendMessage.setOnClickListener(v -> {

            if (enterMessages.getText().toString().equals("Sign Out")) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(con, MainActivity.class));
            } else {
                if (otherUid.length() == 0) {
                    sendMessage();
                } else {
                    sendMessage(myUid, otherUid);
                }
            }
        });

        if (otherUid.length() != 0) {


            fdb.collection("new_messages/rooms/" + myUid + "," + otherUid).addSnapshotListener((value, error) -> {

                if (error != null) {
                    return;
                }

                //Do not know how we are storing personal photo, temp for now
                String tempURL = "https://www.biography.com/.image/ar_1:1%2Cc_fill%2Ccs_srgb%2Cg_face%2Cq_auto:good%2Cw_300/MTc2Njk4NDEwOTMyMzgxNjc1/margaret-thatcher_500x500_gettyimages-108932085.jpg";

                for (QueryDocumentSnapshot doc : value) {
                    Message temp = doc.toObject(Message.class);
                    temp.photoURL = tempURL;
                    temp.uid = myUid;
                    list.add(temp);
                }

                Collections.sort(list, new MessageComparator());
                updateDisplay();

            });

            fdb.collection("new_messages/rooms/" + otherUid + "," + myUid).addSnapshotListener((value, error) -> {

                if (error != null) {
                    return;
                }

                for (QueryDocumentSnapshot doc : value) {
                    Message temp = doc.toObject(Message.class);
                    temp.photoURL = otherURL;
                    temp.uid = otherUid;
                    list.add(temp);
                }

                Collections.sort(list, new MessageComparator());
                updateDisplay();

            });

        } else {

            fdb.collection("messages").addSnapshotListener((value, error) -> {

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
            });
        }
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
                                list.clear();
                                list.addAll(readData);
                                Collections.sort(list);
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
        String url1 = "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/b87d2841-ca20-4e28-819f-ac43f7bfe8ea/de4ezgs-274b3117-50ed-4073-9c8f-4ac1d9cc67dd.jpg?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOiIsImlzcyI6InVybjphcHA6Iiwib2JqIjpbW3sicGF0aCI6IlwvZlwvYjg3ZDI4NDEtY2EyMC00ZTI4LTgxOWYtYWM0M2Y3YmZlOGVhXC9kZTRlemdzLTI3NGIzMTE3LTUwZWQtNDA3My05YzhmLTRhYzFkOWNjNjdkZC5qcGcifV1dLCJhdWQiOlsidXJuOnNlcnZpY2U6ZmlsZS5kb3dubG9hZCJdfQ.v3QBzdLRI8ZT4R5JiYQPFxIxTIHEu7qMDa8N_DBMDn0";


        fdb.collection("messages").add(new Message(new Date(), text, url1, MainActivity.getUID()));

    }

    public void sendMessage(String myUID, String otherUID) {
        String text = enterMessages.getText().toString();
        enterMessages.setText("");

        //Temp personal profile photo, where is it being stored?
        String tempURL = "data:image/jpeg;base64,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";

        fdb.collection("new_messages/rooms/" + myUID + "," + otherUID).add(new Message(new Date(), text, tempURL, myUID));
        enterMessages.setText("new_messages/rooms/" + myUID + "," + otherUID);

    }
}