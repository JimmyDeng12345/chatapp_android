package com.example.chat_app;

public class User {

    public String email, name, photoURL;

    public User(String email, String name, String photoURL) {
        this.photoURL = photoURL;
        this.email = email;
        this.name = name;
    }

    public User() {

    }
}
