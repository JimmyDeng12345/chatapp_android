package com.example.chat_app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chat_app.ui.dashboard.DashboardFragment;
import com.example.chat_app.ui.profile.ProfileFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.WriteResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.example.chat_app.ui.dashboard.DashboardFragment.FIELD_NAME;

public class SignIn extends AppCompatActivity {

    public static boolean createOn = true;
    public static boolean signInCompleted = false;
    public static boolean accountMade = false;
    private GoogleSignInClient mGoogleSignInClient;
    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private EditText name;

    public static FirebaseFirestore ff = FirebaseFirestore.getInstance();
    private final int GOOGLE_SIGN_IN = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);
        System.out.println("running running");
        name = findViewById(R.id.name);
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        password.getText().toString();
        Button create = findViewById(R.id.create);
        TextView bottomText = findViewById(R.id.textView4);
        Button signIn = findViewById(R.id.button4);
        Button google = findViewById(R.id.googleSignIn);
        google.setBackgroundColor(Color.RED);

        System.out.println("Line 62 Sign In");

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                mGoogleSignInClient = GoogleSignIn.getClient(SignIn.this, gso);
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
            }
        });

        System.out.println("Line 78 Sign In");
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = name.getText().toString();
                String userEmail = email.getText().toString();
                String userPassword = password.getText().toString();
                if (userName == null || userEmail == null || userPassword == null) {
                    Toast.makeText(SignIn.this, "Please Fill In All Fields", Toast.LENGTH_LONG);
                } else {
                    if (createOn) {
                        mAuth.createUserWithEmailAndPassword(userEmail, userPassword);
                        startSignIn(userEmail, userPassword, true);
                    } else {
                        startSignIn(userEmail, userPassword, false);
                    }
                }
            }
        });

        System.out.println("Line 95 Sign In");
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
                    signIn.setText("Create Account");
                }
            }
        });

    }

    public static boolean changeState() {
        createOn = !createOn;
        return createOn;
    }

    public void addUserToDB() {
        if (SignIn.accountMade) {
            return;
        }
        System.out.println("adding line 139");
        FirebaseUser user = mAuth.getCurrentUser();
        User currentProfile = new User(user.getEmail(), user.getDisplayName(), "");
        Map<String, Object> docData = new HashMap<>();
        docData.put("email", currentProfile.email);
        String display = currentProfile.name;
        if (display == null || display.equals("")) {
            display = name.getText().toString();
        }
        docData.put("name", display);
        docData.put("photoURL", currentProfile.photoURL);
        ff.collection("users").orderBy("index", Query.Direction.DESCENDING).limit(1).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            System.out.println("User add");

                            String total = document.getData().get("index").toString();
                            int asNum = Integer.parseInt(total) + 1;
                            docData.put("index", asNum + "");
                            ff.collection("users").document(user.getUid()).set(docData);
                        }
                    }
                });
    }


    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            completeSignIn(true);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignIn.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

    private void startSignIn(String email, String password, boolean addUser) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignIn.this, "Success", Toast.LENGTH_LONG).show();
                            completeSignIn(addUser);
                            //Do Success Thing

                        } else {
                            Toast.makeText(SignIn.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            System.out.println("Failure");
                            //Do Failure Thing
                        }
                    }
                });
    }



    private void completeSignIn(boolean addUser) {
        if (addUser) {
            addUserToDB();
        }
        System.out.println((addUser && !SignIn.accountMade) + "yeeeeeeees");
        signInCompleted = true;
        finish();
        //startActivity(new Intent(this, MainPage.class));
        startActivity(new Intent(this, MainPage.class));
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == GOOGLE_SIGN_IN) {
            super.onActivityResult(requestCode, resultCode, data);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            GoogleSignInAccount account = null;
            System.out.println("GoodDay Line 293");

            try {
                account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
                System.out.println("GoodDay Line 297");

            } catch (ApiException e) {
                Toast.makeText(SignIn.this, e.toString(), Toast.LENGTH_LONG);
                System.out.println("GoodDay Line 302" + e.toString());
            }
            System.out.println("GoodDay Line 304");
        }
    }


    public static void signOut() {
        mAuth.signOut();
    }

    /*  */
}