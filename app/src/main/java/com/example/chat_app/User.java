package com.example.chat_app;

public class User {

    public String email, name, photoURL;
    int index;

    public User(String email, String name, String photoURL) {
        this.photoURL = photoURL;
        this.email = email;
        this.name = name;
    }

    public User(String email, String name, String photoURL, int index) {
        this.photoURL = photoURL;
        this.email = email;
        this.name = name;
        this.index = index;
    }

    public User() {
        this.email = "";
        this.name = "Barack Obama";
        this.photoURL = "";
    }

    public User(String email, String name, String photoURL, String uid) {
        this.photoURL = photoURL;
        this.email = uid;
        this.name = name;
    }

    public boolean isEqualTo(User other) {
        return this.email == other.email && this.name == other.name;
    }




}
