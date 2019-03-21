package com.example.meetme;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class Event {
    public String name;
    public String time;
    public String date;

    public Event(QueryDocumentSnapshot doc){
        this.name = doc.getData().get("Name").toString();
        this.date = doc.getData().get("Date").toString();
        this.time = doc.getData().get("Time").toString();

    }
}
