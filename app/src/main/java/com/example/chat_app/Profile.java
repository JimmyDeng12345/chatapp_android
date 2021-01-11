package com.example.chat_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class Profile extends AppCompatActivity {

    private final int PICTURE_CODE = 1;
    private ImageView profilePicture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        MainActivity.makeNavBar(this);
        setContentView(R.layout.activity_profile);
        profilePicture = findViewById(R.id.profilePicture);
        profilePicture.setImageBitmap(Camera.getCircularImage(BitmapFactory.decodeResource(this.getResources(), R.drawable.obama2)));
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Profile.this, Camera.class), PICTURE_CODE);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE_CODE && resultCode == RESULT_OK) {
            Bitmap photo = Camera.getProfilePic();
            if (photo != null) {
                profilePicture.setImageBitmap(photo);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MainActivity.checkMenu(item);
        return super.onOptionsItemSelected(item);

    }
}