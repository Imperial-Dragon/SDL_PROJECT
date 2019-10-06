package com.adityagunjal.sdl_project.models;

import java.io.Serializable;

public class ModelChatUser implements Serializable {
    String userID;
    String chatID;
    String username, lastMessage, profilePicPath, lastUpdated;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePicPath() {
        return profilePicPath;
    }

    public void setProfilePicPath(String profilePicPath) {
        this.profilePicPath = profilePicPath;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public ModelChatUser(){}

    public ModelChatUser(String username, String lastMessage, String lastUpdated, String profilePicPath) {
        this.username = username;
        this.lastMessage = lastMessage;
        this.profilePicPath = profilePicPath;
        this.lastUpdated = lastUpdated;
    }
}
