package com.example.chat_app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignIn extends AppCompatActivity {

    public static boolean createOn = true;

    private GoogleSignInClient mGoogleSignInClient;
    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final int GOOGLE_SIGN_IN = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);

        //FirebaseAuth.getInstance().signOut();
        FirebaseUser currUser = mAuth.getCurrentUser();
        if (currUser != null) {
            finish();
        }

        EditText name = findViewById(R.id.name);
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        password.getText().toString();
        Button create = findViewById(R.id.create);
        TextView bottomText = findViewById(R.id.textView4);
        Button signIn = findViewById(R.id.button4);
        Button google = findViewById(R.id.googleSignIn);
        google.setBackgroundColor(Color.RED);

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

    public static boolean changeState() {
        createOn = !createOn;
        return createOn;
    }

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            System.out.println("GoodDay Line 208");
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignIn.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

    private void startSignIn(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignIn.this, "Success", Toast.LENGTH_LONG).show();
                            System.out.println("Success");
                            finish();
                            //Do Success Thing

                        } else {
                            Toast.makeText(SignIn.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            System.out.println("Failure");
                            //Do Failure Thing
                        }
                    }
                });
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
}