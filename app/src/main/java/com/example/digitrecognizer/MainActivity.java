package com.example.digitrecognizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.digitrecognizer.myViews.PaintView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    private PaintView myDrawingKit;
    private Python py;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        myDrawingKit = findViewById(R.id.my_drawing_kit);

        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setSelectedItemId(R.id.nav_paint);

        if (!Python.isStarted())
            Python.start(new AndroidPlatform(getApplicationContext()));

        py = Python.getInstance();


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
        int h = myDrawingKit.retHeight(), w = myDrawingKit.retWidth();
        Bitmap px_colors_bitmap = createBitmapFromView(myDrawingKit, w, h, getApplicationContext());
        String encodedImage = getStringImage(px_colors_bitmap);

        // passing string in python
        PyObject pyO = py.getModule("CharacterDetector");
        PyObject obj = pyO.callAttr("main", encodedImage);
        String result = obj.toString();
        Toast.makeText(this, "The number is " + result, Toast.LENGTH_SHORT).show();

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

    public @NonNull Bitmap createBitmapFromView(@NonNull View view, int width, int height, Context context) {
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

    public static float px2dp(Resources resource, float px)  {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX,
                px,
                resource.getDisplayMetrics()
        );
    }

    public String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}