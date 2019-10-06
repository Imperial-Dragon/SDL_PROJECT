package com.adityagunjal.sdl_project.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelUser implements Serializable {
    public String firstName, lastName, username, email, registrationID, imagePath, bio;
    int questionCount, answerCount,draftCount;
    public ArrayList<String> questionsArrayList;
    public ArrayList<String> answersArrayList;
    public ArrayList<String> chatsList;
    public ArrayList<String> draftsArrayList;

    public int getDraftCount() {
        return draftCount;
    }

    public void setDraftCount(int draftCount) {
        this.draftCount = draftCount;
    }

    public ArrayList<String> getDraftsArrayList() {
        return draftsArrayList;
    }

    public void setDraftsArrayList(ArrayList<String> draftList) {
        this.draftsArrayList = draftList;
    }

    public ModelUser(){}

    public String getFirstName() {
        return firstName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public int getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(int answerCount) {
        this.answerCount = answerCount;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegistrationID() {
        return registrationID;
    }

    public void setRegistrationID(String registrationID) {
        this.registrationID = registrationID;
    }

    public ArrayList<String> getQuestionsArrayList() {
        return questionsArrayList;
    }

    public void setQuestionsArrayList(ArrayList<String> questionsArrayList) {
        this.questionsArrayList = questionsArrayList;
    }

    public ArrayList<String> getAnswersArrayList() {
        return answersArrayList;
    }

    public void setAnswersArrayList(ArrayList<String> answersArrayList) {
        this.answersArrayList = answersArrayList;
    }

    public ArrayList<String> getChatsList() {
        return chatsList;
    }

    public void setChatsList(ArrayList<String> chatsList) {
        this.chatsList = chatsList;
    }

    public ModelUser(String firstName, String lastName, String username, String email, String registrationID){
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.registrationID = registrationID;
        this.imagePath = "default";
        this.bio = "-";
        this.answerCount = 0;
        this.questionCount = 0;
    }
}
