package com.example.chat_app;

public class StringWrapper {
    public String uid;

    public StringWrapper(String uid){
        this.uid = uid;
    }
    public String toString() {
        return uid;
    }

    public StringWrapper() {
        uid = null;
    }
}
