package com.example.cyclapp_1_2;

import java.util.ArrayList;
import java.util.Date;

import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Navigation extends Activity  implements OnClickListener {

	Button buttonSortDefault, buttonSort1;

	private SQLiteAdapterWriter mySQLiteAdapterWriter;

	SimpleCursorAdapter cursorAdapter;
	Cursor cursor;

	int time = 0;
	int hours = 0;
	int minutes = 0;
	int seconds = 0;
	Location curLocation, oldLocation, startLocation, endLocation;
	double curLocationTime = 0, oldLocationTime = 0, startLocationTime = 0, endLocationTime = 0; // in milliseconds
	ArrayList<Location> Locations = new ArrayList<Location>();
	ArrayList<Double> Speeds = new ArrayList<Double>();
	String tripName = null;
	protected Handler taskHandler = new Handler();
	protected Boolean isRunning = false; 
	protected Boolean isPaused = false;




	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.navigation);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


		mySQLiteAdapterWriter = new SQLiteAdapterWriter(this);
		mySQLiteAdapterWriter.openToWrite();

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

		Location curLocation = locationManager.getLastKnownLocation(provider);

		updateWithNewLocation(curLocation);

		locationManager.requestLocationUpdates(provider, 0, 0, locationListener);

		View startbut = findViewById(R.id.start_button);
		startbut.setOnClickListener(Navigation.this);
		View stopbut = findViewById(R.id.stop_button);
		stopbut.setOnClickListener(Navigation.this);
		View pausebut = findViewById(R.id.pause_button);
		pausebut.setOnClickListener(Navigation.this);
		View resumebut = findViewById(R.id.resume_button);
		resumebut.setOnClickListener(Navigation.this);
		View mapbut = findViewById(R.id.map_button);
		mapbut.setOnClickListener(Navigation.this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start_button:
			// User presses Start
			isRunning = true;
			startLocationTime = System.currentTimeMillis();
			setTimer();
			break;
		case R.id.stop_button:
			// User presses Stop
			isRunning = false;
			if (Locations.size() != 0) {
				startLocation = Locations.get(0);
			}
			endLocation = curLocation;
			endLocationTime = System.currentTimeMillis();
			getNameDialog();
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
		case R.id.map_button:
			Intent m = new Intent(this, Map.class);
			startActivity(m);
			break;
		}
	}

	private void getNameDialog() {
		AlertDialog.Builder myDialog
		= new AlertDialog.Builder(Navigation.this);

		myDialog.setTitle("Enter Trip Name");

		//		TextView dialogTxt_id = new TextView(Navigation.this);
		//		LayoutParams dialogTxt_idLayoutParams
		//		= new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		//		dialogTxt_id.setLayoutParams(dialogTxt_idLayoutParams);
		//		dialogTxt_id.setText(String.valueOf("Enter a name for this trip."));


		final EditText dialogName_id = new EditText(Navigation.this);
		LayoutParams dialogName_idLayoutParams
		= new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		dialogName_id.setLayoutParams(dialogName_idLayoutParams);


		LinearLayout layout = new LinearLayout(Navigation.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		//		layout.addView(dialogTxt_id);

		layout.addView(dialogName_id);
		myDialog.setView(layout);

		myDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			// do something when the button is clicked
			public void onClick(DialogInterface arg0, int arg1) {
				tripName = dialogName_id.getText().toString();
				addToDB();
			}
		});

		myDialog.show();


	}

	private void addToDB() {
		if (Speeds.size() != 0) {
			// Work out name
			String data_name = tripName;
			if (tripName.equals(null)) {
				tripName = "No Name";
			}

			// work out average speed
			double ave = 0; 
			for (Double s : Speeds) {
				ave += s;
			}
			ave = ave / Speeds.size();

			// work out trip distance
			double tripDistance = calculateDistance(Locations.get(0).getLatitude(), Locations.get(0).getLongitude(), curLocation.getLatitude(), curLocation.getLongitude());

			String data_time = Integer.toString(time);
			String data_start_lat = Double.toString(startLocation.getLatitude());
			String data_start_lon = Double.toString(startLocation.getLongitude());
			String data_end_lat = Double.toString(endLocation.getLatitude());
			String data_end_lon = Double.toString(endLocation.getLongitude());
			String data_ave_speed = Double.toString(ave);
			String data_start_time = Double.toString(startLocationTime);
			String data_end_time = Double.toString(endLocationTime);
			String data_distance = Double.toString(tripDistance);
			String data_date = new Date().toString();
			String data_speeds = speedsToString();
			mySQLiteAdapterWriter.insert(data_name, data_time, data_start_lat, data_start_lon, data_end_lat, data_end_lon, data_ave_speed, data_start_time, data_end_time, data_distance, data_date, data_speeds);
			Toast.makeText(
					Navigation.this,
					"Item added to database.",
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(
					Navigation.this,
					"Unable to add item to database.",
					Toast.LENGTH_LONG).show();
		}
	}

	private String speedsToString() {
		String speedsAsString = "";
		for (Double s: Speeds) {
			speedsAsString += Double.toString(s) + ",";
		}
		speedsAsString = speedsAsString.substring(0, speedsAsString.length() - 1);
		speedsAsString += "$";
		return speedsAsString;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mySQLiteAdapterWriter.close();
	}

	private void updateWithNewLocation(Location location) {
		TextView myLocationText;
		TextView mySpeedText;
		myLocationText = (TextView)findViewById(R.id.myLocationText);
		mySpeedText = (TextView)findViewById(R.id.mySpeedText);


		String latLongString = "No location found";
		if (location != null) {
			oldLocationTime = curLocationTime;
			curLocationTime = System.currentTimeMillis();
			oldLocation = curLocation;
			curLocation = location;
			if ((isRunning) && (!isPaused)) {
				Locations.add(location);
			}
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			latLongString = "Lat:" + lat + "\nLong:" + lng;
		}

		myLocationText.setText("Your Current Position is:\n" +
				latLongString);
		double speed = calculateSpeed();
		if ((isRunning) && (!isPaused)) {
			Speeds.add(speed);
		}
		mySpeedText.setText("Your Current Speed is:\n" +
				speed);
	}

	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}

		public void onProviderDisabled(String provider) {
			Toast.makeText(
					Navigation.this,
					"Provider Disabled.",
					Toast.LENGTH_LONG).show();
		}
		public void onProviderEnabled(String provider) {
			Toast.makeText(
					Navigation.this,
					"Provider Enabled.",
					Toast.LENGTH_LONG).show();
		}
		public void onStatusChanged(String provider, int status, 
				Bundle extras) {
			Toast.makeText(
					Navigation.this,
					"Status Changed.",
					Toast.LENGTH_LONG).show();
		}
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

	// Method for calculating Distance
	private double calculateDistance(double oldLat, double oldLon, double curLat, double curLon) {
		// Using Pythagoras
		double lat = 69.1 * Math.abs(curLat - oldLat);
		double lon = 69.1 * Math.abs(curLon - oldLon) * Math.cos(curLat/57.3);
		double distance = Math.sqrt(Math.pow(lat, 2) + Math.pow(lon, 2));
		return distance;


		// Method for calculating Distance using Haverside
		// returns distance in miles
		// http://introcs.cs.princeton.edu/java/12types/GreatCircle.java.html
		//		double a = Math.pow(Math.sin((oldLat - curLat) / 2), 2) + Math.cos(curLat) * Math.cos(oldLat) * Math.pow(Math.sin((oldLon - curLon)/2), 2);
		//		// great circle distance in radians
		//		double angle2 = 2 * Math.asin(Math.min(1, Math.sqrt(a)));
		//		// convert back to degrees
		//		angle2 = Math.toDegrees(angle2);
		//		// each degree on a great circle of Earth is 60 nautical miles
		//		double distance2 = 60 * angle2;
		//		return distance2;
	}

	// Method for calculating speed
	public double calculateSpeed() {
		if (oldLocationTime == 0) {
			return 0;
		} else {
			// TimeDiff in hours
			double TimeDiff   = ((curLocationTime - oldLocationTime) / (1000*60*60)); //hours
			double Distance = calculateDistance(oldLocation.getLatitude(), oldLocation.getLongitude(), curLocation.getLatitude(), curLocation.getLongitude());
			double speed = (Distance / TimeDiff);
			return speed;
		}

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivity(new Intent(Navigation.this, Preferences.class));
			return true;
		case R.id.exit:
			new AlertDialog.Builder(this)
			.setMessage("Are you sure you want to exit?")
			.setCancelable(false)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					//CustomTabActivity.this.finish();
					Navigation.this.finish();
				}
			})
			.setNegativeButton("No", null)
			.show();
			//this.finish();
			return true;
		}
		return false;
	}

}
