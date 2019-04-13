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

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class BuildingsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final String KEY = "Address";

    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private ListView mListView;
    private EditText mFilter;
    private ArrayAdapter mAdapter;
    private LocationManager mLocationManager;
    private LatLng mCurrent;
    private String mLastKnownLocation, mSuggestion1, mSuggestion2, mSuggestion3;

    private float mSmallestDistance = -1;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    ArrayList<String> mAddresses = new ArrayList<>();
    ArrayList<LatLng> mLatLng = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buildings);

        setUpViews();
        setSupportActionBar(mToolbar);
        setActionBarDrawerToggle();
        handleNavigationClickEvents();
        setUpUsernameDisplay();
        handleSelectedItem();
        displayLocation();
        searchBuilding();
        setUpBuildingsList();
        setList();
    }

    private void setUpViews() {
        mFilter = findViewById(R.id.search_text);
        mListView = findViewById(R.id.buildings_listView);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToolbar = findViewById(R.id.toolbar);

        findViewById(R.id.suggestions_button).setOnClickListener(this);
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
        userEmail.setText(mAuth.getCurrentUser().getDisplayName());
    }

    private void setUpBuildingsList() {
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

    private void setUpLatLngList() {
        mLatLng.add(new LatLng(40.003647, -83.028633));
        mLatLng.add(new LatLng(40.005378, -83.0141));
        mLatLng.add(new LatLng(39.996701, -83.016743));
        mLatLng.add(new LatLng(39.9967985, -83.0105888));
        mLatLng.add(new LatLng(40.0016851, -83.0159304));
        mLatLng.add(new LatLng(40.0042743, -83.0126187));
        mLatLng.add(new LatLng(40.0031498, -83.0148524));
        mLatLng.add(new LatLng(39.9965293, -83.0129184));
        mLatLng.add(new LatLng(40.0046977, -83.0092981));
        mLatLng.add(new LatLng(40.002415, -83.015033));
        mLatLng.add(new LatLng(39.9977753, -83.0158234));
        mLatLng.add(new LatLng(39.9960507, -83.0132601));
        mLatLng.add(new LatLng(40.002888, -83.013685));
        mLatLng.add(new LatLng(40.0012907, -83.0150313));
        mLatLng.add(new LatLng(39.9986745, -83.0170397));
        mLatLng.add(new LatLng(40.0042832, -83.0109543));
        mLatLng.add(new LatLng(40.0013193, -83.012536));
        mLatLng.add(new LatLng(40.0003459, -83.0120677));
        mLatLng.add(new LatLng(40.0022061, -83.0158436));
        mLatLng.add(new LatLng(39.9968085, -83.0087004));
        mLatLng.add(new LatLng(40.0015949, -83.0133316));
        mLatLng.add(new LatLng(40.0022112, -83.0167914));
        mLatLng.add(new LatLng(40.0023822, -83.0108684));
        mLatLng.add(new LatLng(40.0103278, -83.0223228));
        mLatLng.add(new LatLng(40.0048344, -83.0159791));
        mLatLng.add(new LatLng(40.0033153, -83.0119478));
        mLatLng.add(new LatLng(39.998904, -83.010009));
        mLatLng.add(new LatLng(40.003526, -83.0153655));
        mLatLng.add(new LatLng(40.0003459, -83.0120677));
        mLatLng.add(new LatLng(40.0058515, -83.0117212));
        mLatLng.add(new LatLng(39.9921595, -83.0140289));
        mLatLng.add(new LatLng(39.9967692, -83.0154358));
        mLatLng.add(new LatLng(40.0020133, -83.0150373));
        mLatLng.add(new LatLng(39.9966994, -83.0138192));
        mLatLng.add(new LatLng(40.0035993, -83.0165923));
        mLatLng.add(new LatLng(39.9984329, -83.0219665));
        mLatLng.add(new LatLng(40.0035396, -83.0116613));
        mLatLng.add(new LatLng(40.0044393, -83.015542));
        mLatLng.add(new LatLng(40.0025415, -83.0123793));
        mLatLng.add(new LatLng(39.9998465, -83.0219281));
        mLatLng.add(new LatLng(39.9960141, -83.0128952));
        mLatLng.add(new LatLng(40.0052748, -83.0127879));
        mLatLng.add(new LatLng(40.0047335, -83.0137567));
        mLatLng.add(new LatLng(40.0048678, -83.012146));
        mLatLng.add(new LatLng(39.9976605, -83.0085321));
        mLatLng.add(new LatLng(39.9958423, -83.0107059));
        mLatLng.add(new LatLng(39.9964296, -83.0128473));
        mLatLng.add(new LatLng(39.9997246, -83.0173421));
        mLatLng.add(new LatLng(40.003382, -83.0142529));
        mLatLng.add(new LatLng(39.9985554, -83.0162271));
        mLatLng.add(new LatLng(39.9992124, -83.017992));
        mLatLng.add(new LatLng(39.9946575, -83.013481));
        mLatLng.add(new LatLng(40.0065016, -83.0185681));
        mLatLng.add(new LatLng(40.0044726, -83.0146437));
        mLatLng.add(new LatLng(40.0043872, -83.0133751));
        mLatLng.add(new LatLng(40.0023138, -83.0144077));
        mLatLng.add(new LatLng(39.9959804, -83.0122849));
        mLatLng.add(new LatLng(40.0023916, -83.0132783));
        mLatLng.add(new LatLng(39.995802, -83.0094998));
        mLatLng.add(new LatLng(40.0018437, -83.0109258));
        mLatLng.add(new LatLng(40.0057997, -83.0106577));
        mLatLng.add(new LatLng(39.9990591, -83.0153077));
        mLatLng.add(new LatLng(40.0059581, -83.0136565));
        mLatLng.add(new LatLng(40.00051, -83.0144294));
        mLatLng.add(new LatLng(39.9949753, -83.0141675));
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
        Geocoder locationAddress = new Geocoder(this, Locale.US);

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
                mCurrent = new LatLng(location.getLatitude(), location.getLongitude());

                try {
                    List<Address> addresses = locationAddress.getFromLocation(lat, lng, 1);
                    mLastKnownLocation = addresses.get(0).getAddressLine(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (location1 != null) {
                double lat = location1.getLatitude();
                double lng = location1.getLongitude();
                mCurrent = new LatLng(location1.getLatitude(), location1.getLongitude());


                try {
                    List<Address> addresses = locationAddress.getFromLocation(lat, lng, 1);
                    mLastKnownLocation = addresses.get(0).getAddressLine(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (location2 != null) {
                double lat = location2.getLatitude();
                double lng = location2.getLongitude();
                mCurrent = new LatLng(location2.getLatitude(), location2.getLongitude());

                try {
                    List<Address> addresses = locationAddress.getFromLocation(lat, lng, 1);
                    mLastKnownLocation = addresses.get(0).getAddressLine(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                mLastKnownLocation = "Unable to trace your location.";
            }
        }
    }

    private void suggestBuildings() {
        setUpLatLngList();

        Geocoder location = new Geocoder(this, Locale.US);
        ArrayList<Address> suggestions = new ArrayList<>();

        for (int i = 0; i < mLatLng.size(); i++) {
            float distance = getDistance(mCurrent, mLatLng.get(i));

            if (distance < mSmallestDistance) {
                try {
                    List<Address> addresses = location.getFromLocation(mLatLng.get(i).latitude, mLatLng.get(i).longitude, 1);
                    suggestions.add(addresses.get(0));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mSmallestDistance = distance;
            }
            if (suggestions.size() == 3) {
                mSuggestion1 = suggestions.get(0).getAddressLine(0);
                mSuggestion2 = suggestions.get(1).getAddressLine(0);
                mSuggestion3 = suggestions.get(2).getAddressLine(0);
                return;
            }
        }

        buildAlertSuggestions();
    }

    private float getDistance(LatLng current, LatLng last) {
        if (last == null) {
            return 0;
        }

        Location currentLoc = new Location("");
        currentLoc.setLatitude(current.latitude);
        currentLoc.setLongitude(current.longitude);

        Location lastLoc = new Location("");
        lastLoc.setLatitude(last.latitude);
        lastLoc.setLongitude(last.longitude);

        return lastLoc.distanceTo(currentLoc);
    }

    private void buildAlertSuggestions() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(BuildingsActivity.this);

        dialog.setTitle("Suggested Buildings:");
        dialog.setMessage("1. " + mSuggestion1 + "\n\n2. " + mSuggestion2 + "\n\n3. " + mSuggestion3);

        dialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.create().show();
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
            case R.id.suggestions_button:
                suggestBuildings();
                break;
        }
    }
}
