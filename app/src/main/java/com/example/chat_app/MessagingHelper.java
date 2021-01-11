//package com.example.chat_app;
//
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.util.Log;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QuerySnapshot;
//import com.squareup.picasso.Picasso;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Date;
//import java.util.List;
//
//import static android.content.ContentValues.TAG;
//
//public class MessagingHelper {
//
//    private static FirebaseFirestore fdb;
//    private static ArrayList<Message> messageArrayList;
//
//    public static void getMessagesOnDB() {
//
//        fdb = FirebaseFirestore.getInstance();
//
//        messageArrayList = new ArrayList<>();
//        //MainActivity.displayMessages.append("Running" + '\n');
//        fdb.collection("messages")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//
//                        if (task.isSuccessful()) {
//                            if (task.getResult() != null) {
//                                List<Message> readData = task.getResult().toObjects(Message.class);
//
//                                Object [] data = readData.toArray();
//                                Arrays.sort(data);
//
//                                MainActivity.list.clear();
//                                for (int i = 0; i < data.length; i++) {
//                                    Object o = data[i];
//                                    processMessages((Message) o);
//                                }
//
//
//
//                            }
//                        } else {
//                            Log.w(TAG, "Goofed");
//                        }
//                     }
//                 });
//
//
//    }
//
//    public static void processMessages(Message m) {
//
//        updateDisplay();
//
//    }
//
//    public static void sendMessage() {
//
//        String text = MainActivity.enterMessages.getText().toString();
//
//        fdb.collection("messages").add(new Message(new Date(), "blank", text, MainActivity.getUID()));
//        //MainActivity.displayMessages.setText("");
//        getMessagesOnDB();
//
//    }
//
//}
