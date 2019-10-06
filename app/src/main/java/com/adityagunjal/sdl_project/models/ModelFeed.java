package com.adityagunjal.sdl_project.models;

import com.adityagunjal.sdl_project.R;

public class ModelFeed {

    public ModelQuestion question;
    public ModelUser user;
    public ModelAnswer answer;

    public ModelFeed(){

    }

    public ModelFeed(ModelQuestion question, ModelUser user, ModelAnswer answer) {
        this.question = question;
        this.user = user;
        this.answer = answer;
    }

    public ModelQuestion getQuestion() {
        return question;
    }

    public void setQuestion(ModelQuestion question) {
        this.question = question;
    }

    public ModelUser getUser() {
        return user;
    }

    public void setUser(ModelUser user) {
        this.user = user;
    }

    public ModelAnswer getAnswer() {
        return answer;
    }

    public void setAnswer(ModelAnswer answer) {
        this.answer = answer;
    }
}
