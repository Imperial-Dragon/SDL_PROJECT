package com.adityagunjal.sdl_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.adityagunjal.sdl_project.interfaces.CustomeOnBackPressed;
import com.adityagunjal.sdl_project.ui.profile.ShowProfileFragment;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportFragmentManager().beginTransaction().replace(R.id.profile_frame_container, new ShowProfileFragment()).commit();
    }

    @Override
    public void onBackPressed() {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.profile_frame_container);

        if(!(fragment instanceof CustomeOnBackPressed) || !((CustomeOnBackPressed)fragment).onBackPressed()){
            super.onBackPressed();
        }
    }
}
