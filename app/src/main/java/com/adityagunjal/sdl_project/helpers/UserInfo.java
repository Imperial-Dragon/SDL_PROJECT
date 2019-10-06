package com.adityagunjal.sdl_project.helpers;

import android.graphics.Bitmap;
import android.util.Log;

import com.adityagunjal.sdl_project.interfaces.DataChanged;
import com.adityagunjal.sdl_project.models.ModelUser;

import java.util.ArrayList;

public class UserInfo{

    ArrayList<DataChanged> dataChangedArrayList = new ArrayList<>();

    ModelUser modelUser;
    Bitmap profilePic;
    String userID;

    public UserInfo(){

    }

    public void setDataChangedListener(DataChanged dataChangedListener){
        this.dataChangedArrayList.add(dataChangedListener);
        onDataChanged(dataChangedArrayList.size() - 1, modelUser, userID, profilePic);
    }

    public void onDataChanged(final int i, final ModelUser modelUser, final String userID, final Bitmap profilePic) {

        if(dataChangedArrayList.get(i) !=  null) {
            dataChangedArrayList.get(i).onDataChanged(modelUser, userID, profilePic);
        }
    }

    public void setData(String userID, ModelUser modelUser, Bitmap profilePic){
        this.userID = userID;
        this.modelUser = modelUser;
        this.profilePic = profilePic;

        try{
            for(int i = 0; i < dataChangedArrayList.size(); i++){
                onDataChanged(i, modelUser, userID, profilePic);
            }
        }catch (Exception e){
            Log.i("UserInfoException", e.toString());
        }
    }

    public ModelUser getModelUser() {
        return modelUser;
    }

    public void setModelUser(ModelUser modelUser) {
        this.modelUser = modelUser;
    }

    public Bitmap getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Bitmap profilePic) {
        this.profilePic = profilePic;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
