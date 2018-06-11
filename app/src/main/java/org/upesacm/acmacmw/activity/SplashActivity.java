package org.upesacm.acmacmw.activity;

import android.content.Intent;
import  android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.upesacm.acmacmw.EventRegistration;
import org.upesacm.acmacmw.R;

public class SplashActivity extends AppCompatActivity {
      private static int SPLASH_TIME_OUT=2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_post_layout);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(SplashActivity.this,EventRegistration.class);
                startActivity(homeIntent);
                finish();
            }
        },SPLASH_TIME_OUT);
    }
}
