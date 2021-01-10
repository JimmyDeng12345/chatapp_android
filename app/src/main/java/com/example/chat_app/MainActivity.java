package com.example.chat_app;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // List of permissions needed for the program, add new ones as needed
    //See checkPermissions method below
    private final String[] PERMISSIONS = {Manifest.permission.CAMERA};
    private final int[] PERMISSION_CODES = {1};
    public static boolean createOn = true;
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private static FirebaseFirestore fdb;

    public static TextView displayMessages;
    public static EditText enterMessages;
    public static Button sendMessage;
    public static ImageView sender_photo;

    public static ArrayList<Message> list;

    private ListView mListView;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //displayMessages = (TextView) findViewById(R.id.textdisplay);
        enterMessages = (EditText) findViewById(R.id.textbox);
        sendMessage = (Button) findViewById(R.id.sendButton);
        mListView = (ListView) findViewById(R.id.textdisplay);
        list = new ArrayList<>();

        mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mListView.setStackFromBottom(true);

        fdb = FirebaseFirestore.getInstance();

        getMessagesOnDB();

        CustomListAdapter adapter = new CustomListAdapter(this, R.layout.message_display, list);
        mListView.setAdapter(adapter);


        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = "ID";
            String channelName = "NAME";
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }

        //FirebaseAuth.getInstance().signOut();
        FirebaseUser currUser = mAuth.getCurrentUser();

        //Toast.makeText(this, currUser.getEmail(), Toast.LENGTH_LONG).show();
        if (currUser == null) {
            setContentView(R.layout.create_account);
            EditText name = findViewById(R.id.name);
            EditText email = findViewById(R.id.email);
            EditText password = findViewById(R.id.password);
            password.getText().toString();
            Button create = findViewById(R.id.create);
            TextView bottomText = findViewById(R.id.textView4);
            Button signIn = findViewById(R.id.button4);

            create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String userEmail = email.getText().toString();
                    String userPassword = password.getText().toString();
                    if (createOn) {
                        mAuth.createUserWithEmailAndPassword(userEmail, userPassword);

                    }
                    startSignIn(userEmail, userPassword);
                }
            });

            signIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeState();
                    if (createOn) {
                        create.setText("Create Account");
                        bottomText.setText("Already Have An Account?");
                        name.setVisibility(View.VISIBLE);
                        signIn.setText("Sign In");
                    } else {
                        create.setText("Sign In");
                        bottomText.setText("Need An Account?");
                        name.setVisibility(View.GONE);
                        signIn.setText("Create An Account");
                    }
                }
            });

        }
    }

    public void updateDisplay() {

        CustomListAdapter adapter = new CustomListAdapter(this, R.layout.message_display, list);

        mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mListView.setAdapter(adapter);

        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mListView.setSelection(adapter.getCount() - 1);
            }
        });


    }

    public static boolean changeState() {
        createOn = !createOn;
        return createOn;
    }


    private void startSignIn(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();
                            System.out.println("GoodDay: Success");
                            setContentView(R.layout.activity_main);
                            //Do Success Thing
                        } else {
                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            System.out.println("GoodDay: Failure");
                            //Do Failure Thing
                        }
                    }
                });
    }


    private final int CAMERA_CODE = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_CODE);
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void userSelect() throws Exception {
        final String[] userOptions = {"Use Camera", "Choose From Phone", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Upload Profile Photo");
        builder.setItems(userOptions, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(PERMISSIONS, PERMISSION_CODES[0]);
                } else {
                    if (userOptions[i].equals("Use Camera")) {
                        // Open Camera
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivity(cameraIntent);

                    } else if (userOptions[i].equals("Choose From Phone")) {
                        Intent picker = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivity(picker);

                    } else {
                        dialogInterface.dismiss();
                    }
                }
            }
        });
        builder.show();
    }


    public static String getUID() {
        return mAuth.getCurrentUser().getUid();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ContextWrapper cw = new ContextWrapper(getApplicationContext());

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
        for (int i = 0; i < PERMISSIONS.length; i++) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(PERMISSIONS, PERMISSION_CODES[i]);
            }
        }
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
        //MainActivity.displayMessages.setText("");
        getMessagesOnDB();

    }


}