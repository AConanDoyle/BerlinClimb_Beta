package de.example.navdrawemap_2.maptest;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;

import de.example.navdrawemap_2.maptest.Maps.MapsActivity;

public class SplashscreenActivity extends Activity {

    private final int SPLASH_DISPLAY_LENGHT = 700; // time howe long SplashScreen is visible
    View view;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = new View (this);
        setContentView(R.layout.activity_splashscreen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashscreenActivity.this, MapsActivity.class);
                ActivityOptions options = ActivityOptions.makeScaleUpAnimation(view, 0 ,
                        0, view.getWidth(), view.getHeight());
                startActivity(mainIntent, options.toBundle());
                SplashscreenActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGHT);
    }
}
