package com.example.digitrecognizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

public class MyPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    final int vibrationSeconds = 5;
    final int countDownSeconds = 20;
    private MediaPlayer player, player1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle("About");
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorLightYellow));
        setSupportActionBar(toolbar);
        navStuff1();

        player = MediaPlayer.create(this, R.raw.button);
        player1 = MediaPlayer.create(this, R.raw.button1);
    }

    private void navStuff1() {
        drawerLayout = findViewById(R.id.drawer_activity);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        setListener();
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        player1.start();
        switch (item.getItemId()) {
            case R.id.nav_paint:
                startActivity(new Intent(getApplicationContext(), Paint.class));
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
                break;
            case R.id.nav_cam:
                startActivity(new Intent(getApplicationContext(), Camera.class));
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
                break;
            case R.id.nav_about:
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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
}