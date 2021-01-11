package com.example.chat_app;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

public class Camera extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.content_main);
        try {
            userSelect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // List of permissions needed for the program, add new ones as needed
    //See checkPermissions method below
    private final String[] PERMISSIONS = {Manifest.permission.CAMERA};
    private final int[] PERMISSION_CODES = {1};
    private final int CAMERA_CODE = 1;
    public static Bitmap myProfilePic = null;
    public static boolean hasImage = false;

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
        for (int i = 0; i < PERMISSIONS.length; i++) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(PERMISSIONS, PERMISSION_CODES[i]);
            }
        }
    }

    private void userSelect() throws Exception {
        final String[] userOptions = {"Use Camera", "Choose From Phone", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Camera.this);
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


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            hasImage = true;
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            myProfilePic = photo;
            ContextWrapper cw = new ContextWrapper(getApplicationContext());

        }
        finish();
    }

    public static Bitmap getProfilePic() {
        return myProfilePic;
    }

    public static Bitmap getCircularImage(Bitmap bitmap) {

        int squareWidth = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap destination = Bitmap.createBitmap(squareWidth, squareWidth, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(destination);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Rect rect = new Rect(0, 0, squareWidth, squareWidth);
        RectF rectF = new RectF(rect);
        canvas.drawOval(rectF, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        float left = (squareWidth - bitmap.getWidth()) / 2;
        float top = (squareWidth - bitmap.getHeight()) / 2;
        canvas.drawBitmap(bitmap, left, top, paint);
        bitmap.recycle();
        return destination;

    }


}