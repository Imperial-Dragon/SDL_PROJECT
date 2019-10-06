package com.adityagunjal.sdl_project.models;

public class ModelUsernameEmail {
    public String email, uid;

    public ModelUsernameEmail(){}

    public  ModelUsernameEmail(String email, String uid){
        this.email = email;
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
