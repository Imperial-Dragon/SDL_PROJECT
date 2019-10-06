package com.adityagunjal.sdl_project.models;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class ModelAnswer implements Serializable {
    public String userID, questionID, date, answerID;
    public Object timestamp;
    public HashMap<String, String> answer;
    public int upvotes, downvotes, comments, views;

    public ModelAnswer(){}

    public ModelAnswer(String username, String questionID, HashMap<String, String> answer,String date) {
        this.userID = username;
        this.questionID = questionID;
        this.timestamp = -1 * new Date().getTime();
        this.date = date;
        this.answer = answer;
        this.upvotes = 0;
        this.downvotes = 0;
        this.comments = 0;
        this.views = 0;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public HashMap<String, String> getAnswer() {
        return answer;
    }

    public void setAnswer(HashMap<String, String> answer) {
        this.answer = answer;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public String getAnswerID() {
        return answerID;
    }

    public void setAnswerID(String answerID) {
        this.answerID = answerID;
    }
}
