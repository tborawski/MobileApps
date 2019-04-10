package com.example.meetme;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuildingsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String KEY = "Address";

    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private ListView mListView;
    private EditText mFilter;
    private ArrayAdapter mAdapter;
    private ArrayList<String> mAddresses;
    private LocationManager mLocationManager;
    private String mLastKnownLocation;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buildings);

        findViewById(R.id.buildings_back_button).setOnClickListener(this);

        mFilter = findViewById(R.id.search_text);
        mListView = findViewById(R.id.buildings_listView);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        setActionBarDrawerToggle();
        handleNavigationClickEvents();
        setUpUsernameDisplay();
        handleSelectedItem();
        displayLocation();
        searchBuilding();
        setUpList();
        setList();
    }

    private void handleNavigationClickEvents() {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();

                        int i = menuItem.getItemId();

                        switch (i) {
                            case R.id.home:
                                goHome();
                                break;
                            case R.id.add_event:
                                addEvent();
                                break;
                            case R.id.my_groups:
                                myGroups();
                                break;
                            case R.id.settings:
                                openSettings();
                                break;
                        }
                        return true;
                    }
                });

    }

    private void setActionBarDrawerToggle() {
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
    }

    private void setUpUsernameDisplay() {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        View v = navigationView.getHeaderView(0);
        TextView userEmail = v.findViewById(R.id.navigation_bar_email);
        userEmail.setText(mAuth.getCurrentUser().getEmail());
    }

    private void setUpList() {
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
    }

    private void handleSelectedItem() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String temp = parent.getAdapter().getItem(position).toString();

                Intent returnIntent = new Intent(BuildingsActivity.this, AddEventActivity.class);
                returnIntent.putExtra(KEY, temp);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    private void displayLocation() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
        }

        Snackbar popUp = Snackbar.make(findViewById(android.R.id.content), "Your most recent location:\n " + mLastKnownLocation, 7000)
                .setAction("Action", null);
        View view = popUp.getView();
        TextView textView = view.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        popUp.show();
    }

    private void getLocation() {
        Geocoder locationAddress = new Geocoder(this);

        if (ActivityCompat.checkSelfPermission(BuildingsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (BuildingsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(BuildingsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        } else {
            Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location location1 = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location location2 = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (location != null) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();

                try {
                    List<Address> addresses = locationAddress.getFromLocation(lat, lng, 1);
                    mLastKnownLocation = addresses.get(0).getLocality();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (location1 != null) {
                double lat = location1.getLatitude();
                double lng = location1.getLongitude();

                try {
                    List<Address> addresses = locationAddress.getFromLocation(lat, lng, 1);
                    mLastKnownLocation = addresses.get(0).getLocality();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (location2 != null) {
                double lat = location2.getLatitude();
                double lng = location2.getLongitude();

                try {
                    List<Address> addresses = locationAddress.getFromLocation(lat, lng, 1);
                    mLastKnownLocation = addresses.get(0).getLocality();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                mLastKnownLocation = "Unable to trace your location.";
            }
        }
    }

    protected void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }

    private void searchBuilding() {
        mFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do nothing.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mListView.setAdapter(mAdapter);
                (BuildingsActivity.this).mAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Do nothing.
            }
        });
    }

    private void setList() {
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mAddresses);
        Collections.sort(mAddresses);
        mListView.setAdapter(mAdapter);
    }

    private void goHome() {
        Intent intent = new Intent(BuildingsActivity.this, MainPageActivity.class);
        startActivity(intent);
    }

    private void addEvent() {
        Intent intent = new Intent(BuildingsActivity.this, AddEventActivity.class);
        startActivity(intent);
    }

    private void myGroups() {
        Intent intent = new Intent(BuildingsActivity.this, MyGroupsActivity.class);
        startActivity(intent);
    }

    private void openSettings() {
        Intent intent = new Intent(BuildingsActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        switch (i) {
            case R.id.buildings_back_button:
                Intent intent = new Intent(BuildingsActivity.this, AddEventActivity.class);
                startActivity(intent);
                break;
        }
    }
}
