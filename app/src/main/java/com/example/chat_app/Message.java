package com.example.chat_app;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;

public class Message implements Comparable {

    public Date createdAt;
    public String photoURL, text, uid;

    public Message() {
        createdAt = new Date(0);
        photoURL = "";
        text = "";
    }

    public Message(Date createdAt, String photoURL, String text, String uid) {

        this.createdAt = createdAt;
        this.photoURL = photoURL;
        this.text = text;
        this.uid = uid;

    }


    @Override
    public int compareTo(Object o) {
        Message two = (Message) o;
        return createdAt.compareTo(two.createdAt);
    }
}
