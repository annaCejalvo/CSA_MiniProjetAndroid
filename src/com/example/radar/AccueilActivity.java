package com.example.radar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AccueilActivity extends Activity {
	Button btnShowLocation;

	// GPSTracker class
	GPSTracker gps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Button b = (Button) findViewById(R.id.btn_alentours);
		btnShowLocation = (Button) findViewById(R.id.btn_ShowLocation);
		
		showLocation();

		setContentView(R.layout.accueil);
	}

	public void showLocation() {
		// show location button click event
		btnShowLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// create class object
				gps = new GPSTracker(AccueilActivity.this);

				// check if GPS enabled
				if (gps.canGetLocation()) {

					double latitude = gps.getLatitude();
					double longitude = gps.getLongitude();

					// \n is for new line
					Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
				}
				else {
					// can't get location
					// GPS or Network is not enabled
					// Ask user to enable GPS/network in settings
					gps.showSettingsAlert();
				}
				gps.stopUsingGPS();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.accueil, menu);
		return true;
	}

	public void onClickGeo(View v) {
		//Intent i = new Intent(this, PhotoVille.class);
		//startActivity(i);
	}

	public void onClickAlentours(View v) {
		Intent i = new Intent(this, PhotoVille.class);
		startActivity(i);
	}
}
