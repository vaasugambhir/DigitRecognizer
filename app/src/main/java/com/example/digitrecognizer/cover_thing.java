package com.example.digitrecognizer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class cover_thing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover_thing);

        TextView head = findViewById(R.id.head);
        TextView tapStart = findViewById(R.id.tap_start);
        TextView sign = findViewById(R.id.signature);

        Animation headAnim = AnimationUtils.loadAnimation(this, R.anim.righttoleft);
        Animation tapStartAnim = AnimationUtils.loadAnimation(this, R.anim.blink_anim);
        Animation signAnim = AnimationUtils.loadAnimation(this, R.anim.lefttoright);

        head.startAnimation(headAnim);
        tapStart.startAnimation(tapStartAnim);
        sign.startAnimation(signAnim);
    }

    public void start(View v){
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        finish();
    }
}