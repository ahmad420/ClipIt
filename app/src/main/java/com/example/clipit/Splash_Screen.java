package com.example.clipit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class Splash_Screen extends AppCompatActivity {

    private static final float ROTATE_FROM = 30.0f;
    private static final float ROTATE_TO = 360.0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImageView Logo;



        setContentView(R.layout.activity_spalsh_screen);
        super.onCreate(savedInstanceState);
        Logo = findViewById(R.id.logo_image_view);


        RotateAnimation r; // = new RotateAnimation(ROTATE_FROM, ROTATE_TO);
        r = new RotateAnimation(ROTATE_FROM, ROTATE_TO, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        r.setDuration((long) 2*1500);
        r.setRepeatCount(0);
        Logo.startAnimation(r);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                Intent intent = new Intent(Splash_Screen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

        }, 5000);
    }
}