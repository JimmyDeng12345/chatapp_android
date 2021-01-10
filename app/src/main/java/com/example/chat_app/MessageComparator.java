package com.example.chat_app;

import java.util.Comparator;

public class MessageComparator implements Comparator<Message> {
    @Override
    public int compare(Message o1, Message o2) {
        return o1.createdAt.compareTo(o2.createdAt);
    }
}
