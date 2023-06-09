package com.theelitedevelopers.teamup.modules.data.models;

import com.google.firebase.Timestamp;

public class Chat {
    String id;
    String uid;
    String title;
    String message;
    String description;
    String image;
    String name;
    Timestamp date;
    String lastMessage;
    String owner;
    String gender;
    String messagedLast;
    Timestamp dateLastMessageSent;

    public Chat(){}

    public Chat(String name, String lastMessage) {
        this.name = name;
        this.lastMessage = lastMessage;
    }

    public Chat(String uid, String message, Timestamp date) {
        this.uid = uid;
        this.message = message;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMessagedLast() {
        return messagedLast;
    }

    public void setMessagedLast(String messagedLast) {
        this.messagedLast = messagedLast;
    }

    public Timestamp getDateLastMessageSent() {
        return dateLastMessageSent;
    }

    public void setDateLastMessageSent(Timestamp dateLastMessageSent) {
        this.dateLastMessageSent = dateLastMessageSent;
    }
}
