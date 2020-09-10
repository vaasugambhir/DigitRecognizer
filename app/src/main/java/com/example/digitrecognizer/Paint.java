package com.example.digitrecognizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.digitrecognizer.myViews.PaintView;
import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;

public class Paint extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private PaintView myDrawingKit;
    private Python py;
    private LoadingAlert alert;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    final int vibrationSeconds = 5;
    final int countDownSeconds = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDrawingKit = findViewById(R.id.my_drawing_kit);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Paint");
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorBlueLight));
        setSupportActionBar(toolbar);

        alert = new LoadingAlert(this);

        navStuff1();

        if (!Python.isStarted())
            Python.start(new AndroidPlatform(getApplicationContext()));

        py = Python.getInstance();
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

    public void convert(View v) {
        alert.startLoading();
        int h = myDrawingKit.retHeight(), w = myDrawingKit.retWidth();
        Bitmap px_colors_bitmap = createBitmapFromView(myDrawingKit, w, h);
        String encodedImage = getStringImage(px_colors_bitmap);

        // passing string in python
        PyObject pyO = py.getModule("CharacterDetector");
        PyObject obj = pyO.callAttr("main", encodedImage);
        final String result = obj.toString();
        new CountDownTimer(2000, 2000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                alert.dismissDialog();
                Toast.makeText(Paint.this, "The number is " + result, Toast.LENGTH_SHORT).show();
                int number = Integer.parseInt(result);
                VoicePlayer player = new VoicePlayer(getBaseContext(), number);
                player.play();
            }
        }.start();

        /*
        obj = pyO.callAttr("main2", encodedImage);
        String num = obj.toString();
        byte[] data = android.util.Base64.decode(num, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        myPic.setImageBitmap(bmp);
         */
    }

    public void clear_kit(View view) {
        myDrawingKit.clear();
    }

    public @NonNull Bitmap createBitmapFromView(@NonNull View view, int width, int height) {
        if (width > 0 && height > 0) {
            view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
        }
        view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        Drawable background = view.getBackground();

        if (background != null) {
            background.draw(canvas);
        }
        view.draw(canvas);

        return bitmap;
    }

    /*
    public static float px2dp(Resources resource, float px)  {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX,
                px,
                resource.getDisplayMetrics()
        );
    }

     */

    public String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_paint:
                break;
            case R.id.nav_cam:
                startActivity(new Intent(getApplicationContext(), Camera.class));
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}