package com.adityagunjal.sdl_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.adityagunjal.sdl_project.interfaces.DataChanged;
import com.adityagunjal.sdl_project.models.ModelUser;
import com.adityagunjal.sdl_project.ui.chat.ChatFragment;
import com.adityagunjal.sdl_project.ui.draft.DraftFragment;
import com.adityagunjal.sdl_project.ui.home.HomeFragment;
import com.adityagunjal.sdl_project.ui.ask.AskFragment;
import com.adityagunjal.sdl_project.ui.recent.RecentFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    Fragment currentFragment;

    FirebaseAuth firebaseAuth;

    DrawerLayout drawer;
    NavigationView navigationView;
    BottomNavigationView bottomNavigation;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(bottomNavListener);

        currentFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(navListener);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        toggle.setDrawerIndicatorEnabled(false);

        findViewById(R.id.drawer_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(drawer.isDrawerVisible(GravityCompat.START)){
                    drawer.closeDrawer(GravityCompat.START);
                }else{
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        setUser();

    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            if(currentFragment.getClass().equals(HomeFragment.class)){
                super.onBackPressed();
            }
            else{
                bottomNavigation.setSelectedItemId(R.id.nav_home);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    private NavigationView.OnNavigationItemSelectedListener navListener =
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;
                    switch(menuItem.getItemId()){
                        case R.id.drawable_nav_profile:
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                            break;
                        case R.id.drawable_nav_bookmarks:
                            break;
                        case R.id.drawable_nav_drafts:
                            selectedFragment = new DraftFragment();
                            break;
                        case R.id.drawable_nav_settings:
                            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                            break;
                        case R.id.drawable_nav_logout:
                            firebaseAuth.signOut();
                            SharedPreferences preferences = getApplicationContext().getSharedPreferences("user_login", MODE_PRIVATE);
                            preferences.edit().clear().commit();

                            SplashActivity.isAlreadyStarted = false;
                            SplashActivity.databaseReference.removeEventListener(SplashActivity.valueEventListener);
                            finish();
                            break;
                    }

                    if(selectedFragment != null && !currentFragment.getClass().equals(selectedFragment.getClass())){
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                        if(selectedFragment.getClass().equals(DraftFragment.class));{
                            getSupportActionBar().hide();
                            currentFragment = selectedFragment;
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    }

                    return true;
                }
            };

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            setTitle("Home");
                            break;
                        case R.id.nav_ask:
                            selectedFragment = new AskFragment();
                            setTitle("Ask");
                            break;
                        case R.id.nav_recent:
                            selectedFragment = new RecentFragment();
                            setTitle("Recent");
                            break;
                        case R.id.nav_chat:
                            selectedFragment = new ChatFragment();
                            setTitle("Chat");
                            break;
                    }

                    if(!currentFragment.getClass().equals(selectedFragment.getClass())){
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                        currentFragment = selectedFragment;
                        getSupportActionBar().show();
                        return true;
                    }else{
                        return false;
                    }
                }
            };

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.top_search:
                startActivity(new Intent(this, SearchableActivity.class));
                break;
            case R.id.top_notifications_icon:
                Toast.makeText(this, "Notifications Clicked", Toast.LENGTH_SHORT).show();
             //   onNotifsClicked();
                break;
        }

        return true;
    }

    public void setUser(){

        SplashActivity.userInfo.setDataChangedListener(new DataChanged() {
            @Override
            public void onDataChanged(ModelUser modelUser, String userID, Bitmap profilePic) {

                CircleImageView circleImageView = findViewById(R.id.drawer_icon);

                View navHeaderView = navigationView.getHeaderView(0);
                TextView navHeaderUsernameTextView = navHeaderView.findViewById(R.id.nav_header_username);
                if(modelUser != null)
                    navHeaderUsernameTextView.setText(modelUser.getUsername());

                CircleImageView navHeaderProfile = navHeaderView.findViewById(R.id.imageView);

                if(profilePic == null){
                    navHeaderProfile.setImageResource(R.drawable.ic_profile_icon);
                }else{
                    circleImageView.setImageBitmap(profilePic);
                    navHeaderProfile.setImageBitmap(profilePic);
                }
            }
        });

    }

    public void setTitle(String title){
        TextView titleText = findViewById(R.id.title);
        titleText.setText(title);
    }

}
