package com.example.digitrecognizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.digitrecognizer.myViews.PaintView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PaintView myDrawingKit;
    private Python py;
    private LoadingAlert alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        myDrawingKit = findViewById(R.id.my_drawing_kit);

        alert = new LoadingAlert(this);

        navStuff();

        if (!Python.isStarted())
            Python.start(new AndroidPlatform(getApplicationContext()));

        py = Python.getInstance();
    }

    public void navStuff() {
        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setSelectedItemId(R.id.nav_paint);
        navigationView.setSelectedItemId(R.id.nav_paint);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_cam:
                        startActivity(new Intent(getApplicationContext(), camera.class));
                        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                        finish();
                        return true;
                    case R.id.nav_paint:
                        return true;
                }
                return false;
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
                Toast.makeText(MainActivity.this, "The number is " + result, Toast.LENGTH_SHORT).show();
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
}