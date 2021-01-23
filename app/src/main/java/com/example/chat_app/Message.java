package com.example.chat_app;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;

public class Message implements Comparable {

    public Date createdAt;
    public String photoURL, text, uid, sendTo;

    public Message() {
        createdAt = new Date(0);
        photoURL = "";
        text = "";
        sendTo = "";
    }

    public Message(Date createdAt, String text, String photoURL) {
        this.createdAt = createdAt;
        this.text = text;
        uid = "";
        sendTo = "";
        this.photoURL = photoURL;
    }

    public Message(Date createdAt, String text, String photoURL, String uid) {

        this.createdAt = createdAt;
        this.photoURL = photoURL;
        this.text = text;
        this.uid = uid;
        sendTo = "";

    }


    @Override
    public int compareTo(Object o) {
        Message two = (Message) o;
        return createdAt.compareTo(two.createdAt);
    }
}
