package com.example.cyclapp_1_2;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class WhereAmI extends Activity implements OnClickListener {

	Location location;

	int time = 0;
	int hours = 0;
	int minutes = 0;
	int seconds = 0;
	protected Handler taskHandler = new Handler();
	protected Boolean isRunning = false;
	protected Boolean isPaused = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.where_am_i);

		LocationManager locationManager;
		String svcName = Context.LOCATION_SERVICE;
		locationManager = (LocationManager)getSystemService(svcName);

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setSpeedRequired(false);
		criteria.setCostAllowed(true);
		String provider = locationManager.getBestProvider(criteria, true);

		Location location = locationManager.getLastKnownLocation(provider);

		updateWithNewLocation(location);

		locationManager.requestLocationUpdates(provider, 500, 1, locationListener);

		View addressbut = findViewById(R.id.address_button);
		addressbut.setOnClickListener(WhereAmI.this);
		View startbut = findViewById(R.id.start_button);
		startbut.setOnClickListener(WhereAmI.this);
		View stopbut = findViewById(R.id.stop_button);
		stopbut.setOnClickListener(WhereAmI.this);
		View pausebut = findViewById(R.id.pause_button);
		pausebut.setOnClickListener(WhereAmI.this);
		View resumebut = findViewById(R.id.resume_button);
		resumebut.setOnClickListener(WhereAmI.this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.address_button:
			String addressString = "No address found";
			if (location != null) {
				double latitude = location.getLatitude();
				double longitude = location.getLongitude();
				Geocoder gc = new Geocoder(this, Locale.getDefault());
				try {
					List<Address> addresses = gc.getFromLocation(latitude, longitude, 1);
					StringBuilder sb = new StringBuilder();
					if (addresses.size() > 0) {
						Address address = addresses.get(0);

						for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
							sb.append(address.getAddressLine(i)).append("\n");

						//sb.append(address.getLocality()).append("\n");
						sb.append(address.getPostalCode()).append("\n");
						sb.append(address.getCountryName());
					}
					addressString = sb.toString();
				} catch (IOException e) {}
				Toast.makeText(
						WhereAmI.this,
						addressString,
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(
						WhereAmI.this,
						addressString,
						Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.start_button:
			// User presses Start
			isRunning = true;
			setTimer();
			break;
		case R.id.stop_button:
			// User presses Stop
			isRunning = false;
			break;
		case R.id.pause_button:
			// User presses pause
			if (isRunning) {
				isPaused = true;
			}
			break;
		case R.id.resume_button:
			// User presses resume
			if (isRunning) {
				isPaused = false;
			}
			break;
		}
	}




	private void updateWithNewLocation(Location location) {
		TextView myLocationText;
		myLocationText = (TextView)findViewById(R.id.myLocationText);

		String latLongString = "No location found";
		if (location != null) {
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			latLongString = "Lat:" + lat + "\nLong:" + lng;
		}

		myLocationText.setText("Your Current Position is:\n" +
				latLongString);
	}

	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}

		public void onProviderDisabled(String provider) {}
		public void onProviderEnabled(String provider) {}
		public void onStatusChanged(String provider, int status, 
				Bundle extras) {}
	};

	private void setTimer() {
		final long elapse = 1000;
		Runnable t = new Runnable() {
			public void run()
			{
				runNextTimedTask();
				if( isRunning )
				{
					taskHandler.postDelayed( this, elapse );
				}
			}
		};
		taskHandler.postDelayed( t, elapse );

	}

	private void runNextTimedTask() {
		if (!isPaused) {
			// run my task.
			time += 1;		
			TextView t = new TextView(this); 
			t = (TextView)findViewById(R.id.timer_label); 
			hours = time / 3600;
			minutes = (time % 3600) / 60;
			seconds = time % 60;
			String timeString = hours + ":" + minutes + ":" + seconds;
			t.setText(timeString);
		}
	}

}