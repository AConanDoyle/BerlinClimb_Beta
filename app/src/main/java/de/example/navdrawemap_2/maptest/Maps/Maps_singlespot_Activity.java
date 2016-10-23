package de.example.navdrawemap_2.maptest.Maps;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import de.example.navdrawemap_2.maptest.R;

public class Maps_singlespot_Activity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private MapboxMap mapboxMap;
    private Maps_singlespot_Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_singlespot);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapView = (MapView) findViewById(R.id.mapviewmapbox);
        mapView.onCreate(savedInstanceState);

        context = this;
        onMapReady(mapboxMap);

    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onMapReady(MapboxMap mapboxMap) {

        final Intent intentbundleData = getIntent();
        final Bundle extraBundle = intentbundleData.getExtras();

        if (intentbundleData != null) {
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(final MapboxMap mapboxMap) {

                    Number lat = null;
                    Number longC = null;
                    if (!extraBundle.isEmpty()) {
                        // Coordinates for the destination
                        longC = extraBundle.getDouble("long");
                        lat = extraBundle.getDouble("lat");
                    }

                    mapboxMap.addMarker(new MarkerOptions()
                            .title(intentbundleData.getStringExtra("heads"))
                            .icon(setIconColour(intentbundleData.getStringExtra("use")))
                            .snippet("Kletter oder Boulderspot: " + intentbundleData.getStringExtra("use") +
                                    "\n" + intentbundleData.getStringExtra("krouten") +
                                    "\n" + intentbundleData.getStringExtra("brouten")
                                    + "\n" + intentbundleData.getStringExtra("inout"))
                            .position(new LatLng(lat.doubleValue(), longC.doubleValue())));

                    CameraPosition position = new CameraPosition.Builder()
                            .target(new LatLng(lat.doubleValue(), longC.doubleValue()))
                            .zoom(13) // Sets the zoom
                            .build(); // Creates a CameraPosition from the builder

                    mapboxMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(position), 6000);
                }
            });
        }
    }

    public Icon setIconColour (String type) {

        IconFactory iconFactory = IconFactory.getInstance(Maps_singlespot_Activity.this);
        Drawable iconDrawableOrange = ContextCompat.getDrawable(Maps_singlespot_Activity.this, R.drawable.marker_orange_36px);
        Drawable iconDrawableRed = ContextCompat.getDrawable(Maps_singlespot_Activity.this, R.drawable.marker_red_36px);
        Drawable iconDrawableDeppRed = ContextCompat.getDrawable(Maps_singlespot_Activity.this, R.drawable.marker_deep_red_36px);
        Icon icon = null;

        switch (type) {
            case "Klettern & Bouldern":
                return icon = iconFactory.fromDrawable(iconDrawableDeppRed);
            case "Bouldern":
                return icon = iconFactory.fromDrawable(iconDrawableOrange);
            case "Klettern":
                return icon = iconFactory.fromDrawable(iconDrawableRed);
            default:
                return icon = iconFactory.fromDrawable(iconDrawableDeppRed);
        }
    }

}
