package com.example.digitrecognizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class camera extends AppCompatActivity {

    private final int RC_PIC_CODE = 101;
    private ImageView myPic;
    private Python py;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        myPic = findViewById(R.id.camera_img);

        if (!Python.isStarted())
            Python.start(new AndroidPlatform(this));

        py = Python.getInstance();

        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setSelectedItemId(R.id.nav_paint);

        navigationView.setSelectedItemId(R.id.nav_cam);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_paint:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                        finish();
                        return true;
                    case R.id.nav_cam:
                        return true;
                }
                return false;
            }
        });
    }

    public void takePic(View v) {
        Intent goToCam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(goToCam, RC_PIC_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PIC_CODE) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                Bitmap picture = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                myPic.setImageBitmap(picture);
                myPic.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            else {
                Toast.makeText(this, "Try again :(", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void convert(View v) {
        BitmapDrawable drawable = (BitmapDrawable)myPic.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        String encodedImage = getStringImage(bitmap);

        // passing string in python
        PyObject pyO = py.getModule("CharacterDetector");
        PyObject obj = pyO.callAttr("main", encodedImage);
        String result = obj.toString();
        Toast.makeText(this, "The number is " + result, Toast.LENGTH_SHORT).show();

        int number = Integer.parseInt(result);
        VoicePlayer player = new VoicePlayer(getBaseContext(), number);
        player.play();

        /*
        obj = pyO.callAttr("main2", encodedImage);
        String num = obj.toString();
        byte[] data = android.util.Base64.decode(num, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        myPic.setImageBitmap(bmp);
         */
    }

    public String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}