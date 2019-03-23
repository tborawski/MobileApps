package com.example.meetme;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;

public class BuildingsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String KEY="Address";

    private ArrayList<String>  mAddresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buildings);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        findViewById(R.id.buildings_back_button).setOnClickListener(this);

        TextView textView = findViewById(R.id.username_textView);
        textView.setText(auth.getCurrentUser().getEmail());

        ListView listView = (ListView) findViewById(R.id.buildings_listView);

        mAddresses = new ArrayList<>();

        mAddresses.add("Adventure Recreation Center (ARC)\n855 Woody Hayes Dr");
        mAddresses.add("Animal Science Building\n2029 Fyffe Rd");
        mAddresses.add("Archer House\n2130 Neil Ave");
        mAddresses.add("Aronoff Laboratory\n318 W 12th Ave");
        mAddresses.add("Baker Hall\n93, 113, & 129 W 12th Ave");
        mAddresses.add("Baker Systems Engineering\n1971 Neil Ave");
        mAddresses.add("Blackburn House\n136 W Woodruff Ave");
        mAddresses.add("Bolz Hall\n2036 Neil Ave Mall");
        mAddresses.add("Bradley Hall\n221 W 12th Ave");
        mAddresses.add("Busch House\n2115 N High St");
        mAddresses.add("Caldwell Laboratory\n2024 Neil Ave");
        mAddresses.add("Campbell Hall\n1787 Neil Ave");
        mAddresses.add("Canfield Hall\n236 W 11th Ave");
        mAddresses.add("Celeste Laboratory of Chemistry\n120 W 18th Ave");
        mAddresses.add("Cockins Hall\n1958 Neil Ave");
        mAddresses.add("Cunz Hall\n1841 Neil Ave");
        mAddresses.add("Curl Hall\n80 W Woodruff Ave");
        mAddresses.add("Denney Hall\n164 Annie & John Glenn Ave");
        mAddresses.add("Derby Hall\n154 N Oval Mall");
        mAddresses.add("Dreese Laboratories\n2015 Neil Ave");
        mAddresses.add("Drinko Hall\n55 W 12th Ave");
        mAddresses.add("Eighteenth Avenue Library\n175 W 18th Ave");
        mAddresses.add("Enarson Classroom Building\n2009 Millikin Rd");
        mAddresses.add("Evans Hall\n520 King Ave");
        mAddresses.add("Evans Laboratory\n88 W 18th Ave");
        mAddresses.add("Fawcett Center for Tomorrow\n2400 Olentangy River Rd");
        mAddresses.add("Fisher Hall\n2100 Neil Ave");
        mAddresses.add("Fontana Laboratories\n116 W 19th Ave");
        mAddresses.add("Hagerty Hall\n1775 College Rd");
        mAddresses.add("Hitchcock Hall\n2070 Neil Ave");
        mAddresses.add("Hopkins Hall\n128 N Oval Mall");
        mAddresses.add("Houston House\n97 W Lane Ave");
        mAddresses.add("Independence Hall\n1923 Neil Ave Mall");
        mAddresses.add("Jennings Hall\n1735 Neil Ave");
        mAddresses.add("Journalism Building\n242 W 18th Ave");
        mAddresses.add("Kennedy Commons\n251 W 12th Ave");
        mAddresses.add("Knowlton Hall\n275 W Woodruff Ave");
        mAddresses.add("Lincoln Tower\n1800 Cannon Dr");
        mAddresses.add("MacQuigg Laboratory\n105 W Woodruff Ave");
        mAddresses.add("Mason Hall\n250 W Woodruff Ave");
        mAddresses.add("McPherson Chemical Laboratory\n140 W 18th Ave");
        mAddresses.add("Mendenhall Laboratory\n125 S Oval Mall");
        mAddresses.add("Morril Tower\n1900 Cannon Dr");
        mAddresses.add("Morrison Tower\n196 W 11th Ave");
        mAddresses.add("North Recreation Center\n149 W Lane Ave");
        mAddresses.add("Norton House\n2114 Neil Ave");
        mAddresses.add("Nosker House\n124 W Woodruff Ave");
        mAddresses.add("Ohio Union\n1739 N High St");
        mAddresses.add("Park-Stradley Hall\n120 W 11th Ave");
        mAddresses.add("Paterson Hall\n191 W 12th Ave");
        mAddresses.add("Physical Activity and Education Services (PAES)\n305 Annie & John Glenn Ave");
        mAddresses.add("Physics Research Building\n191 W Woodruff Ave");
        mAddresses.add("Psychology Building\n1835 Neil Ave");
        mAddresses.add("Recreation and Physical Activity Center (RPAC)\n337 Annie & John Glenn Ave");
        mAddresses.add("Residences on Tenth\n230 W 10th Ave");
        mAddresses.add("Riverwatch Tower\n364 W Lane Ave");
        mAddresses.add("Schoenbaum Hall\n210 W Woodruff Ave");
        mAddresses.add("Scott House\n160 W Woodruff Ave");
        mAddresses.add("Scott Laboratory\n201 W 19th Ave");
        mAddresses.add("Siebert Hall\n184 W 11th Ave");
        mAddresses.add("Smith Laboratory\n174 W 18th Ave");
        mAddresses.add("Smith-Steeb Hall\n80 W 11th Ave");
        mAddresses.add("Stillman Hall\n1947 College Rd");
        mAddresses.add("Taylor Tower\n55 W Lane Ave");
        mAddresses.add("Thompson Library\n1858 Neil Ave Mall");
        mAddresses.add("Torres House\n187 W Lane Ave");
        mAddresses.add("University Hall\n230 N Oval Mall");
        mAddresses.add("Younkin Success Center\n1640 Neil Ave");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mAddresses);

        Collections.sort(mAddresses);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tempListView = mAddresses.get(position);

                Intent returnIntent = new Intent(BuildingsActivity.this, ScheduleActivity.class);
                returnIntent.putExtra(KEY, tempListView);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if(i == R.id.buildings_back_button) {
            Intent intent = new Intent(BuildingsActivity.this, ScheduleActivity.class);
            startActivity(intent);
        }
    }
}
