package com.example.assignment03.Activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.assignment03.Fragments.AboutUsFragment;
import com.example.assignment03.Fragments.MainFragment;
import com.example.assignment03.R;
import com.example.assignment03.StaticResource;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    // Declare bottom navigation view
    public static BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup default fragment
        Fragment fragment = new MainFragment();
        StaticResource.swapFragment(fragment, getSupportFragmentManager());

        // Setup bottom navigation view function
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.home) {
                    Fragment fragment = new MainFragment();
                    StaticResource.swapFragment(fragment, getSupportFragmentManager());
                    Toast.makeText(getApplicationContext(),"Home", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (menuItem.getItemId() == R.id.aboutus) {
                    Fragment fragment = new AboutUsFragment();
                    StaticResource.swapFragment(fragment, getSupportFragmentManager());
                    Toast.makeText(getApplicationContext(),"About Us", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
    }
}
