package com.example.meetme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MessageAdapter extends ArrayAdapter<Message> {

    public MessageAdapter(Context context, ArrayList<Message> messages) {
        super(context, 0, messages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message m = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_message_layout, parent, false);
        }

        TextView mSender = convertView.findViewById(R.id.chat_sender);
        TextView mMessage = convertView.findViewById(R.id.chat_message);

        mSender.setText(m.sender);
        mMessage.setText(m.message);

        return convertView;
    }
}
