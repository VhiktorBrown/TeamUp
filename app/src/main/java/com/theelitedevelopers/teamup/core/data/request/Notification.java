package com.theelitedevelopers.teamup.core.data.request;

public class Notification {
    String to;
    String priority;
    NotificationBody data;
    NotificationMessage notification;

    public Notification(){}

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public NotificationBody getData() {
        return data;
    }

    public void setData(NotificationBody data) {
        this.data = data;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public NotificationMessage getNotification() {
        return notification;
    }

    public void setNotification(NotificationMessage notification) {
        this.notification = notification;
    }
}
