package com.example.qolo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainScreenActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu_main_screen,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                logout();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
    public void logout(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                    }
                });

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        mAuth = FirebaseAuth.getInstance();

        Fragment fragment = new HomeFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();

        BottomNavigationView navigationView = findViewById(R.id.bottomNavigationView);

        navigationView.setOnNavigationItemSelectedListener(listener);
    }

    BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selected_fragment = null;

            switch (item.getItemId()){
                case R.id.search:
                    selected_fragment = new SearchFragment();
                    break;
                case R.id.home:
                    selected_fragment = new HomeFragment();
                    break;
                case R.id.my_posts:
                    selected_fragment = new MyPostsFragment();
                    break;
                case R.id.story:
                    selected_fragment = new StoryFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selected_fragment).commit();
            return true;
        }
    };
    public void create(View view){
        startActivityForResult(new Intent(getApplicationContext(),CreatePostActivity.class),1);
    }

}