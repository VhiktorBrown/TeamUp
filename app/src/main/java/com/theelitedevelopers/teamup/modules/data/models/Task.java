package com.theelitedevelopers.teamup.modules.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

public class Task implements Parcelable {
    String id;
    String title;
    String description;
    String date;
    String uid;
    String employee;
    Integer taskStatus;
    Timestamp datePosted;
    Timestamp deadLine;
    String postedBy;
    String postedByUid;

    public Task(){}
    protected Task(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        date = in.readString();
        uid = in.readString();
        employee = in.readString();
        if (in.readByte() == 0) {
            taskStatus = null;
        } else {
            taskStatus = in.readInt();
        }
        datePosted = in.readParcelable(Timestamp.class.getClassLoader());
        deadLine = in.readParcelable(Timestamp.class.getClassLoader());
        postedBy = in.readString();
        postedByUid = in.readString();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Timestamp getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(Timestamp datePosted) {
        this.datePosted = datePosted;
    }

    public Timestamp getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(Timestamp deadLine) {
        this.deadLine = deadLine;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getPostedByUid() {
        return postedByUid;
    }

    public void setPostedByUid(String postedByUid) {
        this.postedByUid = postedByUid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(date);
        parcel.writeString(uid);
        parcel.writeString(employee);
        if (taskStatus == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(taskStatus);
        }
        parcel.writeParcelable(datePosted, i);
        parcel.writeParcelable(deadLine, i);
        parcel.writeString(postedBy);
        parcel.writeString(postedByUid);
    }
}
