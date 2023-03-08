package com.gokulsundar4545.connectwithpeople;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;


public class SplashScreen extends AppCompatActivity {


    private  static  int SPLASH_TIME_OUT=1000;
    SharedPreferences msharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Handler handler;



        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent=new Intent(SplashScreen.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
            }
        },SPLASH_TIME_OUT);

    }


}