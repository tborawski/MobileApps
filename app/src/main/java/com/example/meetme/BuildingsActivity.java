package com.example.meetme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BuildingsActivity extends AppCompatActivity {

    private ListView mListView;
    private HashMap<String, String> mAddresses;
    private List<HashMap<String, String>>  mListItems;
    private SimpleAdapter mAdapter;
    private Iterator mIterator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buildings);

        mListView = (ListView) findViewById(R.id.buildings_listView);

        mAddresses = new HashMap<>();

        mAddresses.put("Adventure Recreation Center (ARC)", "855 Woody Hayes Dr");
        mAddresses.put("Animal Science Building", "2029 Fyffe Rd");
        mAddresses.put("Archer House", "2130 Neil Ave");
        mAddresses.put("Aronoff Laboratory", "318 W 12th Ave");
        mAddresses.put("Baker Hall", "93, 113, & 129 W 12th Ave");
        mAddresses.put("Baker Systems Engineering", "1971 Neil Ave");
        mAddresses.put("Blackburn House", "136 W Woodruff Ave");
        mAddresses.put("Bolz Hall", "2036 Neil Ave Mall");
        mAddresses.put("Bradley Hall", "221 W 12th Ave");
        mAddresses.put("Busch House", "2115 N High St");
        mAddresses.put("Caldwell Laboratory", "2024 Neil Ave");
        mAddresses.put("Campbell Hall", "1787 Neil Ave");
        mAddresses.put("Canfield Hall", "236 W 11th Ave");
        mAddresses.put("Celeste Laboratory of Chemistry", "120 W 18th Ave");
        mAddresses.put("Cockins Hall", "1958 Neil Ave");
        mAddresses.put("Cunz Hall", "1841 Neil Ave");
        mAddresses.put("Curl Hall", "80 W Woodruff Ave");
        mAddresses.put("Denney Hall", "164 Annie & John Glenn Ave");
        mAddresses.put("Derby Hall", "154 N Oval Mall");
        mAddresses.put("Dreese Laboratories", "2015 Neil Ave");
        mAddresses.put("Drinko Hall", "55 W 12th Ave");
        mAddresses.put("Eighteenth Avenue Library", "175 W 18th Ave");
        mAddresses.put("Enarson Classroom Building", "2009 Millikin Rd");
        mAddresses.put("Evans Hall", "520 King Ave");
        mAddresses.put("Evans Laboratory", "88 W 18th Ave");
        mAddresses.put("Fawcett Center for Tomorrow", "2400 Olentangy River Rd");
        mAddresses.put("Fisher Hall", "2100 Neil Ave");
        mAddresses.put("Fontana Laboratories", "116 W 19th Ave");
        mAddresses.put("Hagerty Hall", "1775 College Rd");
        mAddresses.put("Hitchcock Hall", "2070 Neil Ave");
        mAddresses.put("Hopkins Hall", "128 N Oval Mall");
        mAddresses.put("Houston House", "97 W Lane Ave");
        mAddresses.put("Independence Hall", "1923 Neil Ave Mall");
        mAddresses.put("Jennings Hall", "1735 Neil Ave");
        mAddresses.put("Journalism Building", "242 W 18th Ave");
        mAddresses.put("Kennedy Commons", "251 W 12th Ave");
        mAddresses.put("Knowlton Hall", "275 W Woodruff Ave");
        mAddresses.put("Lincoln Tower", "1800 Cannon Dr");
        mAddresses.put("MacQuigg Laboratory", "105 W Woodruff Ave");
        mAddresses.put("Mason Hall", "250 W Woodruff Ave");
        mAddresses.put("McPherson Chemical Laboratory", "140 W 18th Ave");
        mAddresses.put("Mendenhall Laboratory", "125 S Oval Mall");
        mAddresses.put("Morril Tower", "1900 Cannon Dr");
        mAddresses.put("Morrison Tower", "196 W 11th Ave");
        mAddresses.put("North Recreation Center", "149 W Lane Ave");
        mAddresses.put("Norton House", "2114 Neil Ave");
        mAddresses.put("Nosker House", "124 W Woodruff Ave");
        mAddresses.put("Ohio Union", "1739 N High St");
        mAddresses.put("Park-Stradley Hall", "120 W 11th Ave");
        mAddresses.put("Paterson Hall", "191 W 12th Ave");
        mAddresses.put("Physical Activity and Education Services (PAES)", "305 Annie & John Glenn Ave");
        mAddresses.put("Physics Research Building", "191 W Woodruff Ave");
        mAddresses.put("Psychology Building", "1835 Neil Ave");
        mAddresses.put("Recreation and Physical Activity Center (RPAC)", "337 Annie & John Glenn Ave");
        mAddresses.put("Residences on Tenth", "230 W 10th Ave");
        mAddresses.put("Riverwatch Tower", "364 W Lane Ave");
        mAddresses.put("Schoenbaum Hall", "210 W Woodruff Ave");
        mAddresses.put("Scott House", "160 W Woodruff Ave");
        mAddresses.put("Scott Laboratory", "201 W 19th Ave");
        mAddresses.put("Siebert Hall", "184 W 11th Ave");
        mAddresses.put("Smith Laboratory", "174 W 18th Ave");
        mAddresses.put("Smith-Steeb Hall", "80 W 11th Ave");
        mAddresses.put("Stillman Hall", "1947 College Rd");
        mAddresses.put("Taylor Tower", "55 W Lane Ave");
        mAddresses.put("Thompson Library", "1858 Neil Ave Mall");
        mAddresses.put("Torres House", "187 W Lane Ave");
        mAddresses.put("University Hall", "230 N Oval Mall");
        mAddresses.put("Younkin Success Center", "1640 Neil Ave");

        mListItems = new ArrayList<>();
        mAdapter = new SimpleAdapter(this, mListItems, R.layout.list_item,
                new String[]{"First Line", "Second Line"},
                new int[]{R.id.item_textView, R.id.sub_item_textView});

        mIterator = mAddresses. entrySet().iterator();
        while(mIterator.hasNext()) {
            HashMap<String, String> mResultMap = new HashMap<>();
            Map.Entry pair = (Map.Entry) mIterator.next();
            mResultMap.put("First Line", pair.getKey().toString());
            mResultMap.put("Second Line", pair.getValue().toString());
            mListItems.add(mResultMap);
        }

        Collections.sort(mListItems, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
                return o1.get("First Line").compareToIgnoreCase(o2.get("First Line"));
            }
        });

        mListView.setAdapter(mAdapter);

    }

    /** Called when the user taps the Back button */
    public void openScheduleActivity(View view) {
        Intent intent = new Intent(BuildingsActivity.this, ScheduleActivity.class);
        startActivity(intent);
    }
}
