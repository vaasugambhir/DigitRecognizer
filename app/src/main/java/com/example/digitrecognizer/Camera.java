package com.example.digitrecognizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class Camera extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final int RC_PIC_CODE = 101;
    private ImageView myPic;
    private Python py;
    private LoadingAlert alert;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private boolean taken = false;
    final int vibrationSeconds = 5;
    final int countDownSeconds = 20;
    private MediaPlayer player, player1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        alert = new LoadingAlert(this);
        myPic = findViewById(R.id.camera_img);

        toolbar = findViewById(R.id.toolbar1);
        toolbar.setTitle("Camera");
        setSupportActionBar(toolbar);

        player = MediaPlayer.create(this, R.raw.button);
        player1 = MediaPlayer.create(this, R.raw.button1);

        if (!Python.isStarted())
            Python.start(new AndroidPlatform(this));

        py = Python.getInstance();

        navStuff1();
    }

    @Override
    public void onBackPressed() {
        player.start();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
            super.onBackPressed();
    }

    private void navStuff1() {
        drawerLayout = findViewById(R.id.drawer_activity1);
        setListener();
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setListener() {
        final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final CountDownTimer countDownTimer = new CountDownTimer(countDownSeconds,countDownSeconds) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                vibrator.vibrate(vibrationSeconds);
                start();
            }
        };
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                vibrator.vibrate(vibrationSeconds);
                countDownTimer.start();
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                vibrator.cancel();
                countDownTimer.cancel();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    public void takePic(View v) {
        player.start();
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
                taken = true;
            }
            else {
                Toast.makeText(this, "Try again :(", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void convert(View v) {
        player.start();
        if (!taken) {
            Toast.makeText(this, "Please take a picture first", Toast.LENGTH_SHORT).show();
            return;
        }
        alert.startLoading();
        BitmapDrawable drawable = (BitmapDrawable)myPic.getDrawable();
        final Bitmap bitmap = drawable.getBitmap();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String encodedImage = getStringImage(bitmap);

                // passing string in python
                PyObject pyO = py.getModule("CharacterDetector");
                PyObject obj = pyO.callAttr("main", encodedImage);
                final String result = obj.toString();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alert.dismissDialog();
                        Toast.makeText(Camera.this, "The number is " + result, Toast.LENGTH_SHORT).show();
                        int number = Integer.parseInt(result);
                        VoicePlayer player = new VoicePlayer(getBaseContext(), number);
                        player.play();
                    }
                });
            }
        }).start();


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

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        player1.start();
        switch (item.getItemId()) {
            case R.id.nav_cam:
                break;
            case R.id.nav_paint:
                startActivity(new Intent(getApplicationContext(), Paint.class));
                overridePendingTransition(R.anim.righttoleft, R.anim.righttoleft1);
                finish();
                break;
            case R.id.nav_about:
                startActivity(new Intent(getApplicationContext(), MyPage.class));
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}