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

    public Message(Date createdAt, String photoURL, String text, String uid) {

        this.createdAt = createdAt;
        this.photoURL = photoURL;
        this.text = text;
        this.uid = uid;
        sendTo = "";

    }

    public Message(Date createdAt, String photoURL, String text, String uid, String sendTo) {

        this.createdAt = createdAt;
        this.photoURL = photoURL;
        this.text = text;
        this.uid = uid;
        this.sendTo = sendTo;

    }


    @Override
    public int compareTo(Object o) {
        Message two = (Message) o;
        return createdAt.compareTo(two.createdAt);
    }
}
