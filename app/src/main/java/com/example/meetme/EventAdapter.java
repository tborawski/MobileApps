package com.example.meetme;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class EventAdapter extends ArrayAdapter<Event> {
    private static final String TAG = "Event";
    public EventAdapter(Context context, ArrayList<Event> events){
        super(context, 0, events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Event e = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_event_listview, parent, false);
        }
        TextView eName = (TextView) convertView.findViewById(R.id.label);
        TextView eDate = (TextView) convertView.findViewById(R.id.date);
        TextView sTime = (TextView) convertView.findViewById(R.id.etime);

        eName.setText(e.name);
        eDate.setText(e.date);
        sTime.setText(e.time);

        return convertView;
    }
}
