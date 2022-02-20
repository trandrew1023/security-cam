package com.isanga.securitycam.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.isanga.securitycam.Fragments.Camera;
import com.isanga.securitycam.Fragments.Clips;
import com.isanga.securitycam.Fragments.Home;
import com.isanga.securitycam.Fragments.User;
import com.isanga.securitycam.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout; //The menu layout

    private static int MY_PERMISSIONS_REQUEST_CAMERA;
    private static int MY_PERMISSIONS_REQUEST_AUDIO;
    private static int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Camera permissions.
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }

        //Audio permissions.
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_REQUEST_AUDIO);
            }
        }

        //External storage permissions.
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
            }
        }

        //Needed to overwrite the default actionbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Sets up menu click events
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Initializes menu layout
        drawerLayout = findViewById(R.id.menu_drawer);

        //Opens Home fragment when app is first opened
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new Home())
                    .commit();
        }


    }

    //Opens a fragment corresponding to the menu item
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.gesture_menu_home:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new Home())
                        .commit();
                break;
            case R.id.gesture_menu_camera:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new Camera())
                        .commit();
                break;
            case R.id.gesture_menu_viewer:
                startActivity(new Intent(this, ViewerActivity.class));
                break;
            case R.id.gesture_menu_streamer:
                startActivity(new Intent(this, StreamerActivity.class));
                break;
            case R.id.gesture_menu_clips:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new Clips())
                        .commit();
                break;

            case R.id.gesture_menu_user:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new User())
                        .commit();
                break;
        }
        //Closes menu when an item is clicked
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
