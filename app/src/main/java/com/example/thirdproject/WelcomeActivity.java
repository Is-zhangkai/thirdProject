package com.example.thirdproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import okhttp3.internal.http2.Http2Reader;

public class WelcomeActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_welcome);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);



        final Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                sharedPreferences=getSharedPreferences("token",MODE_PRIVATE);
                final String token=sharedPreferences.getString("token","null");
                Log.i("sa",token);
                try {
                    Thread.sleep(1200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (token.equals("null")){
                            Intent intent=new Intent(WelcomeActivity.this,SignInActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                           Intent intent=new Intent(WelcomeActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

            }
        });
        thread.start();





    }
}