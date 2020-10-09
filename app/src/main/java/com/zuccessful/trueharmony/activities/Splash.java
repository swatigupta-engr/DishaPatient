package com.zuccessful.trueharmony.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.ImageView;

import com.zuccessful.trueharmony.R;

public class Splash extends Activity {
    private static int SPLASH_SCREEN_TIME_OUT=2000;
    ImageView img;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //This method is used so that your splash activity
        //can cover the entire screen.

        setContentView(R.layout.splash_main);
        img =(ImageView) findViewById(R.id.img);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                img.setBackgroundResource(R.drawable.splash_main);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        Intent i=new Intent(Splash.this,   LoginActivity.class);
                        //Intent is used to switch from one activity to another.

                        startActivity(i);

                        finish();
                        //the current activity will get finished.
                    }
                }, SPLASH_SCREEN_TIME_OUT);
            }
        }, 2000);
        //this will bind your MainActivity.class file with activity_main.


    }
}

