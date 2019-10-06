package com.adityagunjal.sdl_project.models;

import java.io.Serializable;
import java.util.Date;

public class ModelQuestion implements Serializable {
    public String text, date, userID, username;
    public int answers;
    public Object timestamp;
    public String qID;

    public ModelQuestion(){}

    public ModelQuestion(String text, String date, String userID, int answers) {
        this.text = text;
        this.date = date;
        this.userID = userID;
        this.answers = answers;
        this.timestamp = -1 * new Date().getTime();
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getAnswers() {
        return answers;
    }

    public void setAnswers(int answers) {
        this.answers = answers;
    }

    public String getqID() {
        return qID;
    }

    public void setqID(String qID) {
        this.qID = qID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
