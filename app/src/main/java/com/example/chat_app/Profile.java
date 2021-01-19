package com.example.chat_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Context;
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
        setContentView(R.layout.activity_profile);


    }


    public void setUpProfile(ImageView profilePicture, View v) {
        this.profilePicture = profilePicture;
        profilePicture = v.findViewById(R.id.profilePicture);
        profilePicture.setImageBitmap(Camera.getCircularImage(BitmapFactory.decodeResource(v.getResources(), R.drawable.obama2)));

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
        finish();
    }
}