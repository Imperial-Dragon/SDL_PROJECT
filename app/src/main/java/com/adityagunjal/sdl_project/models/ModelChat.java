package com.adityagunjal.sdl_project.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class ModelChat implements Serializable {
    public String chatID;
    public long timestamp;
    public ArrayList<String> participants;
    public ArrayList<ModelMessage> messages;

    public ModelChat(){}

    public ModelChat(String chatID, ArrayList<String> participants){
        this.chatID = chatID;
        this.participants = participants;
        timestamp = -1 * new Date().getTime();
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public ArrayList<String> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<String> participants) {
        this.participants = participants;
    }

    public ArrayList<ModelMessage> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<ModelMessage> messages) {
        this.messages = messages;
    }

}
