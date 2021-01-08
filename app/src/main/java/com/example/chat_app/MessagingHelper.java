package com.example.chat_app;

import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MessagingHelper {

    private static FirebaseFirestore fdb;

    public static void getMessagesOnDB() {

        fdb = FirebaseFirestore.getInstance();

        //MainActivity.displayMessages.append("Running" + '\n');
        fdb.collection("messages")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                List<Message> readData = task.getResult().toObjects(Message.class);

                                Object[] data = readData.toArray();
                                Arrays.sort(data);

                                for (Object m : data) {
                                    processMessages((Message) m);
                                    //MainActivity.displayMessages.append(m.toString() + '\n' + '\n');
                                }
                            }
                        } else {
                            Log.w(TAG, "Goofed");
                        }
                     }
                 });


    }

    private static void processMessages(Message m) {

        MainActivity.displayMessages.append(m.text + '\n');

    }

    public static void sendMessage() {

        String text = MainActivity.enterMessages.getText().toString();

        fdb.collection("messages").add(new Message(new Date(), "blank", text, "Alex"));
        MainActivity.displayMessages.setText("");
        getMessagesOnDB();

    }

}
