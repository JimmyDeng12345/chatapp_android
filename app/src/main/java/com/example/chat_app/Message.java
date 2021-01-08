package com.example.chat_app;

import java.sql.Timestamp;
import java.util.Date;

public class Message {

    public Date createdAt;
    public String photoURL, text;

    public Message() {
        createdAt = new Date(0);
        photoURL = "";
        text = "";
    }

    public Message(Date createdAt, String photoURL, String text) {

        this.createdAt = createdAt;
        this.photoURL = photoURL;
        this.text = text;

    }

}
