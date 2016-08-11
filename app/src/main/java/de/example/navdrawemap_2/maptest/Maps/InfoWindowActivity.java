package de.example.navdrawemap_2.maptest.Maps;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import de.example.navdrawemap_2.maptest.ListViewSpots.ListViewSpotsActivity;
import de.example.navdrawemap_2.maptest.R;

public class InfoWindowActivity extends AppCompatActivity {

    private Intent intentbundleData;
    TextView textViewTitle, textViewSnippet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_window);

        textViewTitle = (TextView) findViewById(R.id.text_titel);
        textViewSnippet = (TextView) findViewById(R.id.text_snippet);
        intentbundleData = getIntent();

        if (intentbundleData != null) {
            textViewTitle.setText(intentbundleData.getStringExtra("title"));
            textViewSnippet.setText(intentbundleData.getStringExtra("snippet"));
        } else {
            textViewTitle.setText("N.A.");
            textViewSnippet.setText("N.A.");
        }

        Button btn_rout_bike = (Button) findViewById(R.id.buttonRouting_bike);
        Button btn_rout_walk = (Button) findViewById(R.id.buttonrouting_walk);

        if (btn_rout_bike != null) {
            btn_rout_bike.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("routingprofile", "PROFILE_CYCLING");
                    intentbundleData.putExtras(bundle);
                    callRouting();
                    onPause();
                }
            });
        }

        if (btn_rout_walk != null) {
            btn_rout_walk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("routingprofile", "PROFILE_WALKING");
                    intentbundleData.putExtras(bundle);
                    callRouting();
                    onPause();
                }
            });
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_infowindow, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int item_id = item.getItemId();
        switch (item_id) {
            case R.id.item_overview:
                Intent intentoverview = new Intent(this, ListViewSpotsActivity.class);
                startActivity(intentoverview);
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Calls the Routing Activity
    public void callRouting () {

        if (networkstatus() != null) {
            Intent intentMapRouting = new Intent(this, Routing_Activity.class);
            intentMapRouting.putExtras(intentbundleData);
            startActivity(intentMapRouting);
        } else {
            Snackbar snackbar = Snackbar
                    .make(findViewById(android.R.id.content),
                            "Bitte überprüfen Sie ihre Netzwerkverbindung.", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    public NetworkInfo networkstatus () {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni;
    }

    // lifecycle methods
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("title", textViewTitle.getText().toString());
        savedInstanceState.putString("snippet", textViewSnippet.getText().toString());
    }

    public void onPause() {
        super.onPause();
        Log.d("lifecycle", "onPause invoked");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("lifecycle", "onResume invoked");
    }

    @Override
    public void onRestart(){
        super.onRestart();
        Log.d("lifecycle", "onRestart invoked");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d("lifecycle", "onLowMemory invoked");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("lifecycle", "onDestroy invoked");
    }

}


