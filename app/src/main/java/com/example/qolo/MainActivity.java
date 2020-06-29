package com.example.qolo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView app_title,app_subtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        app_title = findViewById(R.id.app_title);
        app_subtitle = findViewById(R.id.app_subtitle);

        app_title.setAlpha(0);
        app_subtitle.setAlpha(0);

        app_title.animate().alpha(1).setDuration(2000);
        app_subtitle.animate().alpha(1).setDuration(2000);

        CountDownTimer timer = new CountDownTimer(4000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                finish();
                startActivity(intent);
            }
        }.start();
    }
}