package com.example.qolo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ViewPostActivity extends AppCompatActivity {

    //This activity is created so as to see post individually with it's likes and comments

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
    }
}