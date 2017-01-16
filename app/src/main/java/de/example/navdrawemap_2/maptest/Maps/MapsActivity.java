package de.example.navdrawemap_2.maptest.Maps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.UiSettings;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.example.navdrawemap_2.maptest.ListViewSpots.ListViewSpotsActivity;
import de.example.navdrawemap_2.maptest.OverActivity;
import de.example.navdrawemap_2.maptest.R;
import de.example.navdrawemap_2.maptest.XMLParser;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnInfoWindowClickListener {

    // Map stuff
    private static final int MY_PERMISSIONS_REQUEST_GET_LOCATION = 1;
    protected LocationManager locationManager;
    protected LatLng myposition;
    protected Location myLocation;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private MapsActivity context;

    // Data
    private String gml = null;

    // Notes for DOM / Data
    String KEY_FM = "gml:featureMember"; // entering Tag for XML-File
    String KEY_NAME = "ogr:NAME";
    String KEY_LAT = "ogr:LAT";
    String KEY_LONG = "ogr:LONG";
    String KEY_USE = "ogr:NUTZEN";
    String KEY_KROUT = "ogr:KROUTEN";
    String KEY_BROUT = "ogr:BROUTEN";
    String KEY_INOUT = "ogr:INOUT";

    // Floating Action Buttons & Menu
    FloatingActionMenu materialDesignFAM;
    FloatingActionButton fabBoulder, fabClimb, fabBoulderandClimb, locationButton;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapView = (MapView) findViewById(R.id.mapviewmapbox);

        // FA Buttons
        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        fabClimb = (FloatingActionButton) findViewById(R.id.floating_action_menu_climb);
        fabBoulder = (FloatingActionButton) findViewById(R.id.floating_action_menu_boulder);
        fabBoulderandClimb = (FloatingActionButton) findViewById(R.id.floating_action_menu_BandC);
        locationButton = (FloatingActionButton) findViewById(R.id.location_toggle_fab);

        mapView.onCreate(savedInstanceState);
        context = this;
        onMapReady(mapboxMap);
    }

    //setup the optionmenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // method for the options menu and calls for other activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int item_id = item.getItemId();
        switch (item_id) {
            case R.id.item_overview:
                Intent intentoverview = new Intent(this, ListViewSpotsActivity.class);
                startActivity(intentoverview);
                break;
            case R.id.item_over:
                Intent intentover = new Intent(this, OverActivity.class);
                startActivity(intentover);
                break;
            case R.id.item_close:
                System.exit(0);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private LocationListener locationListener = new LocationListener() {
        public String TAG;

        @Override
        public void onLocationChanged(Location location) {
            String longitude = "Longitude: " + location.getLongitude();
            Log.v(TAG, longitude);
            String latitude = "Latitude: " + location.getLatitude();
            Log.v(TAG, latitude);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_GET_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                 /*   if (myLocation !=null) {
                        myposition = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    }
                } else {
                    myposition = new LatLng(52.52001, 13.40495); */
                }
            }
            break;

            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void onMapReady(MapboxMap mapboxMap) {

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {

                int hasFineLocationPermission = 0;
                int hasCoarseLocationPermission = 0;
                List<String> permissions = new ArrayList<String>();

                // If the Androidversion is higher than M, the code checks for the runtime-permissions
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    hasFineLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                    hasCoarseLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
                }
                if (!permissions.isEmpty()) {
                    ActivityCompat.requestPermissions(context,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_GET_LOCATION);
                }

                // two If-Checks for Runtime Permissions/ in these case fine and coarse location
                if (hasFineLocationPermission != 0) {
                    ActivityCompat.requestPermissions(context,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_GET_LOCATION);
                }

                if (hasCoarseLocationPermission != 0) {
                    ActivityCompat.requestPermissions(context,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_GET_LOCATION);
                }

                // Locationmanager which combines the GPS and the Network (WIFI and MobilePhoneNetwork) providers
                // plus one listener which is import for the regular updates
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                if (locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER)
                        && hasCoarseLocationPermission == 0) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 10, locationListener);
                }
                if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)
                        && hasFineLocationPermission == 0) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 10, locationListener);
                }

                // default location
                myposition = new LatLng(52.52001, 13.40495);

                // requesting location for android smaller than marshmallow (API23)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000,
                            0, locationListener);
                    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        myposition = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    }
                }

                if (hasCoarseLocationPermission == 0) {
                    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000,
                                0, locationListener);
                        myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (myLocation != null) {
                            myposition = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                        }
                    }
                }

                if (hasFineLocationPermission == 0) {
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (myLocation != null) {
                            myposition = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                        }
                    }
                }

                // move to users location if location is known, otherwise to city center
                CameraPosition position = new CameraPosition.Builder()
                        .target(myposition)
                        .zoom(11)
                        .build();
                mapboxMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(position), 6000);

                // some UserInterface settings
                UiSettings mUiSettings = mapboxMap.getUiSettings();
                mUiSettings.setAttributionEnabled(false);
                mUiSettings.setAllGesturesEnabled(true);

                mapboxMap.setOnInfoWindowClickListener(context);
                mapboxMap.setMyLocationEnabled(true);

                // loading data and sets the markers
                String filename = "spotsberlin4";
                try {
                    XMLParser parser = new XMLParser();
                    gml = parser.loadFile(filename, getResources(), true);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // method that places all markers on the map
                setMarkers(mapboxMap, gml, true, true);

                fabClimb.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        mapboxMap.removeAnnotations();
                        setMarkers(mapboxMap, gml, true, false);
                        // Message for the user
                        Snackbar snackbar = Snackbar
                                .make(v, "Kletterspots", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                });

                fabBoulder.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        mapboxMap.removeAnnotations();
                        setMarkers(mapboxMap, gml, false, true);
                        Snackbar snackbar = Snackbar
                                .make(v, "Boulderspots", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                });

                fabBoulderandClimb.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        mapboxMap.removeAnnotations();
                        setMarkers(mapboxMap, gml, true, true);
                        Snackbar snackbar = Snackbar
                                .make(v, "Boulder- und Kletterspots", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                });

                // button for settting Camera to userlocation
                locationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (myposition != null) {
                            CameraPosition position = new CameraPosition.Builder()
                                    .target(myposition)
                                    .zoom(13)
                                    .build();
                            mapboxMap.animateCamera(CameraUpdateFactory
                                    .newCameraPosition(position), 3000);
                        }
                    }
                });
            }
        });
    }

    // method for setting the markers on the map; requires the filter variables boulder ...
    public void setMarkers(MapboxMap mapboxMap, String gml, final Boolean climb, Boolean boulder) {

        XMLParser parser = new XMLParser();
        Document doc = parser.getDomElement(gml);
        NodeList nl = doc.getElementsByTagName(KEY_FM);

        // set markers on map
        for (int i = 0; i < nl.getLength(); i++) {
            Element e = (Element) nl.item(i);
            final double LAT = Double.parseDouble(parser.getValue(e, KEY_LAT));
            final double LONG = Double.parseDouble(parser.getValue(e, KEY_LONG));
            final String name = parser.getValue(e, KEY_NAME);
            final String use = parser.getValue(e, KEY_USE);
            String kRouten = parser.getValue(e, KEY_KROUT);
            String bRouten = parser.getValue(e, KEY_BROUT);
            String inout = parser.getValue(e, KEY_INOUT);

           /* if (high_wire && use.equals("Hochseilgarten")) {
                // Icon Colour
                BitmapDescriptor pointIcon = BitmapDescriptorFactory
                        .defaultMarker(setMarkerColor(inout));

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(LAT, LONG))
                        .title(name)
                        .anchor(0, 1)
                        .icon(pointIcon).snippet("\n" + "Kletterrouten: " + kRouten +
                                "\nBoulderrouten: " + bRouten + "\nInfo: " + info));

            } */

            if (climb && use.equals("Klettern")) {

                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(LAT, LONG))
                        .title(name)
                        .icon(setIconColour(use))
                        .snippet("\nKletter oder Boulderspot: " + use +
                                "\nKletterrouten: " + kRouten + "\nBoulderrouten: "
                                + bRouten + "\nIn- oder Outdoor: " + inout));
            }


            if (boulder && use.equals("Bouldern")) {

                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(LAT, LONG))
                        .title(name)
                        .icon(setIconColour(use))
                        .snippet("\nKletter oder Boulderspot: " + use +
                                "\nKletterrouten: " + kRouten + "\nBoulderrouten: "
                                + bRouten + "\nIn- oder Outdoor: " + inout));
            }

            if (climb && use.equals("Klettern & Bouldern") &&
                    boulder && use.equals("Klettern & Bouldern")) {

                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(LAT, LONG))
                        .title(name)
                        .icon(setIconColour(use))
                        .snippet("\nKletter oder Boulderspot: " + use +
                                "\nKletterrouten: " + kRouten + "\nBoulderrouten: "
                                + bRouten + "\nIn- oder Outdoor: " + inout));
            }
        }
    }

    // method for infowindowbubble in map when is clicked;
    public boolean onInfoWindowClick(@NonNull com.mapbox.mapboxsdk.annotations.Marker marker) {

        LatLng position = marker.getPosition();
    //    distance(position);
        Double LongC = position.getLongitude();
        Double Lat = position.getLatitude();

        Intent intentinfoWindow = new Intent(this, InfoWindowActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("title", marker.getTitle());
        bundle.putString("snippet", marker.getSnippet());
        bundle.putDouble("longitude", LongC);
        bundle.putDouble("latitude", Lat);
        intentinfoWindow.putExtras(bundle);
        startActivity(intentinfoWindow);

        return false;
    }

    public Icon setIconColour (String type) {

        IconFactory iconFactory = IconFactory.getInstance(MapsActivity.this);
        Drawable iconDrawableDeppRed = ContextCompat.getDrawable(MapsActivity.this, R.drawable.marker_deep_red_36px);
        Drawable iconDrawableRed = ContextCompat.getDrawable(MapsActivity.this, R.drawable.marker_red_36px);
        Drawable iconDrawableOrange = ContextCompat.getDrawable(MapsActivity.this, R.drawable.marker_orange_36px);
        Icon icon;

        switch (type) {
            case "Klettern & Bouldern":
                return icon = iconFactory.fromDrawable(iconDrawableDeppRed);
            case "Klettern":
                return icon = iconFactory.fromDrawable(iconDrawableRed);
            case "Bouldern":
                return icon = iconFactory.fromDrawable(iconDrawableOrange);
            default:
                return icon = iconFactory.fromDrawable(iconDrawableDeppRed);
        }
    }

    // lifecycle methods
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    public void onPause() {
        super.onPause();
        mapView.onPause();
        Log.d("lifecycle", "onPause invoked");
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("lifecycle", "onDestroy invoked");
    }

}