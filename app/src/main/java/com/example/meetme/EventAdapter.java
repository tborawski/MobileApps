package com.example.meetme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends ArrayAdapter<Event> implements Filterable {
    ArrayList<Event> ef;
    ArrayList<Event> filteredData;
    EventFilter mFilter = new EventFilter();
    public EventAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
        this.ef = events;
        this.filteredData = events;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Event e = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_event_listview, parent, false);
        }
        TextView eName = convertView.findViewById(R.id.label);
        TextView eDate = convertView.findViewById(R.id.date);
        TextView sTime = convertView.findViewById(R.id.stime);
        TextView eTime = convertView.findViewById(R.id.etime);
        TextView ePlace = convertView.findViewById(R.id.eplace);

        eName.setText(e.name);
        eDate.setText(e.date);
        sTime.setText(e.startTime);
        eTime.setText(e.endTime);
        ePlace.setText(e.loc);

        return convertView;
    }

    public int getCount(){
        return filteredData.size();
    }

    public Event getItem(int position){
        return filteredData.get(position);
    }

    public Filter getFilter(){
        return mFilter;
    }

    private class EventFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint){
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final List<Event> list = ef;
            int count = list.size();
            final ArrayList<Event> nlist = new ArrayList<>(count);

            String filterableString;

            for(int i = 0; i < count; i++){
                filterableString = list.get(i).name;
                if(filterableString.toLowerCase().contains(filterString)){
                    nlist.add(list.get(i));
                }
            }
            results.values = nlist;
            results.count = nlist.size();
            return results;
        }

        @SuppressWarnings("unckecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results){
            filteredData = (ArrayList<Event>) results.values;
            notifyDataSetChanged();
        }
    }


}
