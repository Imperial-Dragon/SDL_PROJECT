package com.adityagunjal.sdl_project.interfaces;

import android.graphics.Bitmap;

import com.adityagunjal.sdl_project.models.ModelUser;

public interface DataChanged {
    public void onDataChanged(ModelUser modelUser, String userID, Bitmap profilePic);
}
