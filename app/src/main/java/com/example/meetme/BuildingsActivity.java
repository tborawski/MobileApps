package com.example.meetme;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

    public static final String KEY="Address";

    private List<HashMap<String, String>>  mListItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buildings);

        ListView listView = (ListView) findViewById(R.id.buildings_listView);

        HashMap<String, String> addresses = new HashMap<>();

        addresses.put("Adventure Recreation Center (ARC)", "855 Woody Hayes Dr");
        addresses.put("Animal Science Building", "2029 Fyffe Rd");
        addresses.put("Archer House", "2130 Neil Ave");
        addresses.put("Aronoff Laboratory", "318 W 12th Ave");
        addresses.put("Baker Hall", "93, 113, & 129 W 12th Ave");
        addresses.put("Baker Systems Engineering", "1971 Neil Ave");
        addresses.put("Blackburn House", "136 W Woodruff Ave");
        addresses.put("Bolz Hall", "2036 Neil Ave Mall");
        addresses.put("Bradley Hall", "221 W 12th Ave");
        addresses.put("Busch House", "2115 N High St");
        addresses.put("Caldwell Laboratory", "2024 Neil Ave");
        addresses.put("Campbell Hall", "1787 Neil Ave");
        addresses.put("Canfield Hall", "236 W 11th Ave");
        addresses.put("Celeste Laboratory of Chemistry", "120 W 18th Ave");
        addresses.put("Cockins Hall", "1958 Neil Ave");
        addresses.put("Cunz Hall", "1841 Neil Ave");
        addresses.put("Curl Hall", "80 W Woodruff Ave");
        addresses.put("Denney Hall", "164 Annie & John Glenn Ave");
        addresses.put("Derby Hall", "154 N Oval Mall");
        addresses.put("Dreese Laboratories", "2015 Neil Ave");
        addresses.put("Drinko Hall", "55 W 12th Ave");
        addresses.put("Eighteenth Avenue Library", "175 W 18th Ave");
        addresses.put("Enarson Classroom Building", "2009 Millikin Rd");
        addresses.put("Evans Hall", "520 King Ave");
        addresses.put("Evans Laboratory", "88 W 18th Ave");
        addresses.put("Fawcett Center for Tomorrow", "2400 Olentangy River Rd");
        addresses.put("Fisher Hall", "2100 Neil Ave");
        addresses.put("Fontana Laboratories", "116 W 19th Ave");
        addresses.put("Hagerty Hall", "1775 College Rd");
        addresses.put("Hitchcock Hall", "2070 Neil Ave");
        addresses.put("Hopkins Hall", "128 N Oval Mall");
        addresses.put("Houston House", "97 W Lane Ave");
        addresses.put("Independence Hall", "1923 Neil Ave Mall");
        addresses.put("Jennings Hall", "1735 Neil Ave");
        addresses.put("Journalism Building", "242 W 18th Ave");
        addresses.put("Kennedy Commons", "251 W 12th Ave");
        addresses.put("Knowlton Hall", "275 W Woodruff Ave");
        addresses.put("Lincoln Tower", "1800 Cannon Dr");
        addresses.put("MacQuigg Laboratory", "105 W Woodruff Ave");
        addresses.put("Mason Hall", "250 W Woodruff Ave");
        addresses.put("McPherson Chemical Laboratory", "140 W 18th Ave");
        addresses.put("Mendenhall Laboratory", "125 S Oval Mall");
        addresses.put("Morril Tower", "1900 Cannon Dr");
        addresses.put("Morrison Tower", "196 W 11th Ave");
        addresses.put("North Recreation Center", "149 W Lane Ave");
        addresses.put("Norton House", "2114 Neil Ave");
        addresses.put("Nosker House", "124 W Woodruff Ave");
        addresses.put("Ohio Union", "1739 N High St");
        addresses.put("Park-Stradley Hall", "120 W 11th Ave");
        addresses.put("Paterson Hall", "191 W 12th Ave");
        addresses.put("Physical Activity and Education Services (PAES)", "305 Annie & John Glenn Ave");
        addresses.put("Physics Research Building", "191 W Woodruff Ave");
        addresses.put("Psychology Building", "1835 Neil Ave");
        addresses.put("Recreation and Physical Activity Center (RPAC)", "337 Annie & John Glenn Ave");
        addresses.put("Residences on Tenth", "230 W 10th Ave");
        addresses.put("Riverwatch Tower", "364 W Lane Ave");
        addresses.put("Schoenbaum Hall", "210 W Woodruff Ave");
        addresses.put("Scott House", "160 W Woodruff Ave");
        addresses.put("Scott Laboratory", "201 W 19th Ave");
        addresses.put("Siebert Hall", "184 W 11th Ave");
        addresses.put("Smith Laboratory", "174 W 18th Ave");
        addresses.put("Smith-Steeb Hall", "80 W 11th Ave");
        addresses.put("Stillman Hall", "1947 College Rd");
        addresses.put("Taylor Tower", "55 W Lane Ave");
        addresses.put("Thompson Library", "1858 Neil Ave Mall");
        addresses.put("Torres House", "187 W Lane Ave");
        addresses.put("University Hall", "230 N Oval Mall");
        addresses.put("Younkin Success Center", "1640 Neil Ave");

        mListItems = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(this, mListItems, R.layout.list_item,
                new String[]{"First Line", "Second Line"},
                new int[]{R.id.item_textView, R.id.sub_item_textView});

        Iterator iterator = addresses.entrySet().iterator();
        while(iterator.hasNext()) {
            HashMap<String, String> mResultMap = new HashMap<>();
            Map.Entry pair = (Map.Entry) iterator.next();
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

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tempListView = mListItems.get(position).toString();

                Intent returnIntent = new Intent(BuildingsActivity.this, ScheduleActivity.class);
                returnIntent.putExtra(KEY, tempListView);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

    }

    /** Called when the user taps the Back button */
    public void goBackToScheduleActivity(View view) {
        Intent intent = new Intent(BuildingsActivity.this, ScheduleActivity.class);
        startActivity(intent);
    }
}
