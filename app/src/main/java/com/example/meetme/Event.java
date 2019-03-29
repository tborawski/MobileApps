package com.example.meetme;

import com.google.firebase.firestore.QueryDocumentSnapshot;

public class Event {
    public String name;
    public String date;
    public String startTime;
    public String endTime;
    public String loc;
    public String id;

    public Event(QueryDocumentSnapshot doc){
        this.name = doc.getData().get("Name").toString();
        this.date = doc.getData().get("Date").toString();
        this.startTime = doc.getData().get("Start Time").toString();
        this.endTime = doc.getData().get("End Time").toString();
        this.loc = doc.getData().get("Place").toString();
        this.id = doc.getId();
    }
}
