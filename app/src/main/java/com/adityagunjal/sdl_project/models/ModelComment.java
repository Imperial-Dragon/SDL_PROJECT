package com.adityagunjal.sdl_project.models;

import java.util.Calendar;
import java.util.Date;

public class ModelComment {

    String commentID;
    public String userID;
    public String lastUpdated;
    public String comment;
    public Object timestamp;


    public ModelComment(){}

    public ModelComment(String commentID, String comment, String userID){

        this.commentID = commentID;
        this.userID = userID;
        this.comment = comment;

        lastUpdated = Calendar.getInstance().getTime().toString();
        timestamp = new Date().getTime();
    }


    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getComment() {
        return comment;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }
}
