package com.example.meetme;

import com.google.firebase.firestore.QueryDocumentSnapshot;

public class Message {
    public String sender;
    public String message;

    public Message(QueryDocumentSnapshot doc) {
        this.sender = doc.getData().get("Sender").toString();
        this.message = doc.getData().get("Message").toString();
    }
}
