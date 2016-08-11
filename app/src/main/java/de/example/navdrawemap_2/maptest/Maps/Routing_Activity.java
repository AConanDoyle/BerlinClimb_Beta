package de.example.navdrawemap_2.maptest.Maps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.Constants;
import com.mapbox.services.commons.ServicesException;
import com.mapbox.services.commons.geojson.LineString;
import com.mapbox.services.commons.models.Position;
import com.mapbox.services.directions.v4.models.Waypoint;
import com.mapbox.services.directions.v5.DirectionsCriteria;
import com.mapbox.services.directions.v5.MapboxDirections;
import com.mapbox.services.directions.v5.models.DirectionsResponse;
import com.mapbox.services.directions.v5.models.DirectionsRoute;

import java.text.DecimalFormat;
import java.util.List;

import de.example.navdrawemap_2.maptest.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Routing_Activity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    private MapView mapView;
    private MapboxMap map;

    // For Routing
    private DirectionsRoute currentRoute;
    protected LatLng myposition;
    protected Location myLocation;
    Double lat, longC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapboxAccountManager.start(this, getString(R.string.accessToken));
        setContentView(R.layout.activity_maps_singlespot);

        mapView = (MapView) findViewById(R.id.mapviewmapbox);
        mapView.onCreate(savedInstanceState);

        // Locationmanager which combines the GPS and the Network (WIFI and MobilePhoneNetwork) providers
        // plus one listener which is import for the regular updates
        LocationManager locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);

        // default location for routing
        myposition = new LatLng(52.52001, 13.40495);

        // gets location from NETWORK_PROVIDER
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (myLocation != null) {
                    myposition = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                }
            }
        }

        // gets location from gps
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (myLocation != null) {
                    myposition = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                }
            }
        }


        // Add start and destination to the map
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {

                mapboxMap.setMyLocationEnabled(true);
                final Intent intentbundleData = getIntent();
                Bundle extraBundle = intentbundleData.getExtras();

                if (!extraBundle.isEmpty()) {
                    // Coordinates for the destination
                    longC = extraBundle.getDouble("longitude");
                    lat = extraBundle.getDouble("latitude");

                    mapboxMap.addMarker(new MarkerOptions()
                            .position(myposition)
                            .title("Du befindest dich hier"));

                    mapboxMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, longC))
                            .title("Ziel")
                            .snippet(intentbundleData.getStringExtra("title")));
                }
            }

        });

        final Intent intentbundleData = getIntent();
        Bundle extraBundle = intentbundleData.getExtras();
        if (!extraBundle.isEmpty()) {
            // Coordinates for the destination
            longC = extraBundle.getDouble("longitude");
            lat = extraBundle.getDouble("latitude");
        }

        // put Coordinates into Waypoint and call getRoute-Method
        final Waypoint destination = new Waypoint(longC, lat);
        final Waypoint origin = new Waypoint(myposition.getLongitude(), myposition.getLatitude());

        final Position destinationPosition = Position.fromCoordinates(longC, lat);
        final Position originPosition = Position.fromCoordinates(myposition.getLongitude(), myposition.getLatitude());

        // Setup the MapView
        mapView = (MapView) findViewById(R.id.mapviewmapbox);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;
                String routingProfile;

                // add origin and destination to the map
                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(origin.getLatitude(), origin.getLongitude()))
                        .title("Du befindest dich hier"));
                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(destination.getLatitude(), destination.getLongitude()))
                        .title("Ziel")
                        .snippet(intentbundleData.getStringExtra("title")));

                // Define the Routingprofile
                if (intentbundleData.getStringExtra("routingprofile").equals("PROFILE_CYCLING")) {
                    routingProfile = DirectionsCriteria.PROFILE_CYCLING;
                } else {
                    routingProfile = DirectionsCriteria.PROFILE_CYCLING;
                }

                // get route
                try {
                    getRoute(originPosition, destinationPosition, routingProfile);
                } catch (ServicesException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getRoute(Position origin, Position destination, String routingProfile)
            throws ServicesException {

        MapboxDirections client = new MapboxDirections.Builder()
                .setOrigin(origin)
                .setDestination(destination)
                .setProfile(routingProfile)
                .setAccessToken(getString(R.string.accessToken))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                // You can get the generic HTTP info about the response
                Log.d(TAG, "Response code: " + response.code());
                if (response.body() == null) {
                    Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                    return;
                }

                // Print some info about the route
                currentRoute = response.body().getRoutes().get(0);
                Log.d(TAG, "Distance: " + currentRoute.getDistance());

                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content)
                                , "Die Strecke ist " + (new DecimalFormat("###.##")
                                  .format(currentRoute.getDistance() / 1000)) + " Kilometer lang."
                                , Snackbar.LENGTH_LONG);
                snackbar.show();

                // Draw the route on the map
                drawRoute(currentRoute);
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
                Toast.makeText(Routing_Activity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void drawRoute(DirectionsRoute route) {

        // Convert LineString coordinates into LatLng[]
        LineString lineString = LineString.fromPolyline(route.getGeometry(), Constants.OSRM_PRECISION_V5);
        List<Position> coordinates = lineString.getCoordinates();
        LatLng[] points = new LatLng[coordinates.size()];
        for (int i = 0; i < coordinates.size(); i++) {
            points[i] = new LatLng(
                    coordinates.get(i).getLatitude(),
                    coordinates.get(i).getLongitude());
        }

        // draw line on MapView
        map.addPolyline(new PolylineOptions()
                .add(points)
                .color(Color.parseColor("#3887be"))
                .width(5));

        setBoundingBox();
    }

    private void setBoundingBox() {

        // default BoundingBox coordinates for berlin
        LatLng northeast = new LatLng(52.38198, 13.07167);
        LatLng southwest = new LatLng(52.66940, 13.83110);

        // determining latitude values for BoundingBox
        if (myposition.getLatitude() > lat) {

            northeast.setLatitude(myposition.getLatitude());
            southwest.setLatitude(lat);

        } else {

            northeast.setLatitude(lat);
            southwest.setLatitude(myposition.getLatitude());

        }
        // determining longitude values for BoundingBox
        if (myposition.getLongitude() > longC) {

            northeast.setLongitude(myposition.getLongitude());
            southwest.setLongitude(longC);

        } else {

            northeast.setLongitude(longC);
            southwest.setLongitude(myposition.getLongitude());

        }

        // BoundingBox
        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(northeast)     // Northeast
                .include(southwest)    // Southwest
                .build();
        map.animateCamera(CameraUpdateFactory
                .newLatLngBounds(latLngBounds, 120), 5000);

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
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}