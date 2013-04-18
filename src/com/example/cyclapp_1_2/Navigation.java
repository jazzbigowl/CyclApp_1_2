/*
 * Author: Jeremy Bouchat
 * Year: 2013
 * Project Report: https://www.dropbox.com/s/8ba5y8kax3lqhz5/Report.docx
 */
package com.example.cyclapp_1_2;

import java.math.BigDecimal;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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

public class Navigation extends MapActivity  implements OnClickListener {

	private SQLiteAdapterWriter mySQLiteAdapterWriter;

	SimpleCursorAdapter cursorAdapter;
	Cursor cursor;

	Format formatter;

	int time = 0;
	int hours = 0;
	int minutes = 0;
	int seconds = 0;
	double distance;
	Location curLocation, oldLocation, startLocation, endLocation;
	double curLocationTime = 0, oldLocationTime = 0, startLocationTime = 0, endLocationTime = 0; // in milliseconds
	ArrayList<Location> Locations = new ArrayList<Location>();
	ArrayList<Double> Speeds = new ArrayList<Double>();
	ArrayList<String> Times = new ArrayList<String>();
	String tripName = null;
	protected Handler taskHandler = new Handler();
	protected Boolean isRunning = false; 
	protected Boolean isPaused = false;

	// MAP STUFF
	private MapView myMap;
	private MapController controller;
	private PositionOverlay myPositionOverlay;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.navigation);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// initiate writer to database
		mySQLiteAdapterWriter = new SQLiteAdapterWriter(this);
		mySQLiteAdapterWriter.openToWrite();

		LocationManager locationManager;
		String svcName = Context.LOCATION_SERVICE;
		locationManager = (LocationManager)getSystemService(svcName);

		// set up location retrieving criterias
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setSpeedRequired(false);
		criteria.setCostAllowed(true);
		String provider = locationManager.getBestProvider(criteria, true);

		//get current location
		Location curLocation = locationManager.getLastKnownLocation(provider);

		updateWithNewLocation(curLocation);

		locationManager.requestLocationUpdates(provider, 0, 0, locationListener);

		//listeners
		View startStopButton = findViewById(R.id.start_stop_button);
		startStopButton.setOnClickListener(Navigation.this);
		View pauseResumeButton = findViewById(R.id.pause_resume_button);
		pauseResumeButton.setOnClickListener(Navigation.this);
		View mapbut = findViewById(R.id.map_button);
		mapbut.setOnClickListener(Navigation.this);

		TextView speedMeasurementLabel;
		speedMeasurementLabel = (TextView)findViewById(R.id.mySpeedMeasurementText);
		speedMeasurementLabel.setText(getSpeedMeasurement());
		TextView distanceMeasurementLabel;
		distanceMeasurementLabel = (TextView)findViewById(R.id.myDistanceMeasurementText);
		distanceMeasurementLabel.setText(getDistanceMeasurement());

		pauseResumeButton.setEnabled(false);

		// MAP STUFF
		initMapView();
		initMyLocation();

		// Add the MyPositionOverlay
		myPositionOverlay = new PositionOverlay();
		List<Overlay> overlays = myMap.getOverlays();
		overlays.add(myPositionOverlay);
		myMap.postInvalidate();
	}

	public void onClick(View v) {
		Button startStopButton = (Button) findViewById(R.id.start_stop_button);
		Button pauseResumeButton = (Button) findViewById(R.id.pause_resume_button);

		switch (v.getId()) {
		case R.id.start_stop_button:
			// User presses Start Stop button
			if (isRunning.equals(false)) { // User presses start
				startStopButton.setText("STOP");
				pauseResumeButton.setEnabled(true);
				isRunning = true;
				startLocationTime = System.currentTimeMillis();
				setTimer();
			} else if (isRunning.equals(true)) { // User presses stop
				pauseResumeButton.setEnabled(false);
				isRunning = false;
				if (Locations.size() != 0) {
					startLocation = Locations.get(0);
				}
				endLocation = curLocation;
				endLocationTime = System.currentTimeMillis();
				getNameDialog();
			}
			break;
		case R.id.pause_resume_button:
			// User presses pause resume button
			if (isRunning) {
				if (isPaused.equals(false)) {
					pauseResumeButton.setText("RESUME");
					isPaused = true;
				} else if (isPaused.equals(true)) {
					pauseResumeButton.setText("PAUSE");
					isPaused = false;
				}
			}
			break;
		case R.id.map_button:
			Intent map = new Intent(this, Map.class); 
			// all the extras bellow are unecesary right now, but may come in useful when improving app
			if (isRunning && !isPaused) {
				map.putExtra("isRunning", true);
				map.putExtra("isPaused", false);
				map.putExtra("myTimer", time);
			} else if (isRunning && isPaused){
				map.putExtra("isRunning", true);
				map.putExtra("isPaused", true);
				map.putExtra("myTimer", time);
			} else {
				map.putExtra("isRunning", false);
				map.putExtra("isPaused", true);
				map.putExtra("myTimer", time);
			}
			startActivity(map);
			break;
		}
	}

	@Override
	public void onBackPressed() { // prompt user when exiting this activity, he will lose data if not saved
		AlertDialog.Builder myDialog
		= new AlertDialog.Builder(Navigation.this);

		myDialog.setTitle("Warning!");

		TextView dialogTxt_id = new TextView(Navigation.this);
		LayoutParams dialogTxt_idLayoutParams
		= new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		dialogTxt_id.setLayoutParams(dialogTxt_idLayoutParams);
		dialogTxt_id.setText(String.valueOf("Unsaved trips will be lost." +
				"\nAre you sure you want to quit?"));
		LinearLayout layout = new LinearLayout(Navigation.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(dialogTxt_id);
		myDialog.setView(layout);
		myDialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
			// do something when the button is clicked
			public void onClick(DialogInterface arg0, int arg1) {
				Navigation.this.finish();
			}
		});
		myDialog.setPositiveButton("No", new DialogInterface.OnClickListener() {
			// do something when the button is clicked
			public void onClick(DialogInterface arg0, int arg1) {	
			}
		});
		myDialog.show();
	}

	private void getNameDialog() { // prompt user for a name to save the journey
		AlertDialog.Builder myDialog
		= new AlertDialog.Builder(Navigation.this);
		myDialog.setTitle("Enter Trip Name");
		final EditText dialogName_id = new EditText(Navigation.this);
		@SuppressWarnings("deprecation")
		LayoutParams dialogName_idLayoutParams
		= new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		dialogName_id.setLayoutParams(dialogName_idLayoutParams);
		LinearLayout layout = new LinearLayout(Navigation.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(dialogName_id);
		myDialog.setView(layout);
		myDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			// do something when the button is clicked
			public void onClick(DialogInterface arg0, int arg1) {
				tripName = dialogName_id.getText().toString();
				addToDB();
				Navigation.this.finish();
			}
		});
		myDialog.show();
	}

	@SuppressLint("SimpleDateFormat")
	private void addToDB() { // add all the journey's details to the database
		if (Speeds.size() != 0) {
			// Work out name
			String data_name = tripName;
			if (tripName.equals(null)) {
				tripName = "No Name";
			}

			// work out average speed
			double ave = 0; 
			double tmpSpeed = 0;
			ArrayList<Double> tmpSpeeds = new ArrayList<Double>();
			ArrayList<String> tmpTimes = new ArrayList<String>();
			for (Double s : Speeds) {
				if (tmpSpeed != 0) {
					if (s < tmpSpeed * 20) {
						tmpSpeeds.add(s);
						tmpTimes.add(Times.get(Speeds.indexOf(s)));
					}	
				}
				tmpSpeed = s;
			}
			Speeds = tmpSpeeds;
			Times = tmpTimes;
			for (Double s : Speeds) {
				ave += s;
			}
			ave = ave / Speeds.size();

			// work out trip distance
//			double tripDistance = 0;
//			for (int i = 1; i < Locations.size(); i++) {
//				tripDistance += calculateDistance(Locations.get(i - 1).getLatitude(), Locations.get(i - 1).getLongitude(), Locations.get(i).getLatitude(), Locations.get(i).getLongitude());
//			}
			
			// variable to add to database
			String data_trip_time = Integer.toString(time);
			String data_start_lat = Double.toString(startLocation.getLatitude());
			String data_start_lon = Double.toString(startLocation.getLongitude());
			String data_end_lat = Double.toString(endLocation.getLatitude());
			String data_end_lon = Double.toString(endLocation.getLongitude());
			String data_ave_speed = Double.toString(ave);
			String data_start_time = Double.toString(startLocationTime);
			String data_end_time = Double.toString(endLocationTime);
			String data_distance = Double.toString(distance);
			formatter = new SimpleDateFormat("E, dd MMM yyyy");
			String data_date = formatter.format(new Date());
			formatter = new SimpleDateFormat("HH:mm:ss");
			String data_time = formatter.format(new Date());
			String data_speeds = speedsToString();
			String data_locations = locationsToString();
			String data_times = timesToString();
			mySQLiteAdapterWriter.insert(data_name, data_trip_time, data_start_lat, data_start_lon, data_end_lat, data_end_lon, data_ave_speed, data_start_time, data_end_time, data_distance, data_date, data_time, data_speeds, data_locations, data_times);
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

	private String speedsToString() { //converts speeds to string for storing
		String speedsAsString = "";
		for (Double s: Speeds) {
			speedsAsString += Double.toString(s) + ",";
		}
		speedsAsString = speedsAsString.substring(0, speedsAsString.length() - 1);
		speedsAsString += "$";
		return speedsAsString;
	}

	private String locationsToString() { //converts locations to string for saving
		String locationsAsString = "";
		for (Location l: Locations) {
			locationsAsString += Double.toString(l.getLatitude()) + "," + Double.toString(l.getLongitude()) + ",";
		}
		locationsAsString = locationsAsString.substring(0, locationsAsString.length() - 1);
		locationsAsString += "$";
		return locationsAsString;
	}

	private String timesToString() { // converts times to string for storing
		String timesAsString = "";
		for (String t: Times) {
			timesAsString += t + ",";
		}
		timesAsString = timesAsString.substring(0, timesAsString.length() - 1);
		timesAsString += "$";
		return timesAsString;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mySQLiteAdapterWriter.close();
	}

	@SuppressLint("SimpleDateFormat")
	private void updateWithNewLocation(Location location) { // update on new locaiton received
		TextView mySpeedText;
		TextView myLocationText;
		TextView myDistanceText;
		mySpeedText = (TextView)findViewById(R.id.mySpeedText);
		myLocationText = (TextView)findViewById(R.id.myLocationText);
		myDistanceText = (TextView)findViewById(R.id.myDistanceText);

		// work out latitudes and longitudes
		String latLongString = "No location found";
		if (location != null) {
			oldLocationTime = curLocationTime;
			curLocationTime = System.currentTimeMillis();
			oldLocation = curLocation;
			curLocation = location;
			if ((isRunning) && (!isPaused)) {
				Locations.add(location);
			}
			double lat = round(location.getLatitude(), 6, BigDecimal.ROUND_HALF_UP);
			double lng = round(location.getLongitude(), 6, BigDecimal.ROUND_HALF_UP);
			latLongString = "Lat:" + lat + "\nLon:" + lng;
		}

		// work out speed
		double speed = calculateSpeed();
		if ((isRunning) && (!isPaused)) {
			Speeds.add(speed);
			formatter = new SimpleDateFormat("HH:mm:ss");
			Times.add(formatter.format(new Date()));
		}
		if (getSpeedMeasurement().equals("mph")) {
			mySpeedText.setText(Double.toString(round(speed, 2, BigDecimal.ROUND_HALF_UP)));
		} else {
			mySpeedText.setText(Double.toString(round((speed* 1.60934), 2, BigDecimal.ROUND_HALF_UP)));
		}

		// updata distance fields
		myLocationText.setText(latLongString);
		if (Locations.size() != 0) {
			if (Locations.size() <= 2) {
			 distance += calculateDistance(Locations.get(0).getLatitude(), Locations.get(0).getLongitude(), curLocation.getLatitude(), curLocation.getLongitude());
			distance = round(distance, 2, BigDecimal.ROUND_HALF_UP);
			} else {
				 distance += calculateDistance(Locations.get(Locations.size() - 2).getLatitude(), Locations.get(Locations.size() - 2).getLongitude(), curLocation.getLatitude(), curLocation.getLongitude());
				distance = round(distance, 2, BigDecimal.ROUND_HALF_UP);
			}
			if (getDistanceMeasurement().equals("mi")) {
				myDistanceText.setText(Double.toString(distance));
			} else {
				myDistanceText.setText(Double.toString(round((distance* 1.60934), 2, BigDecimal.ROUND_HALF_UP)));
			}
		} else {
			myDistanceText.setText("0");
		}
	}

	private static double round(double unrounded, int precision, int roundingMode) { //methods for rounding doubles
		BigDecimal bd = new BigDecimal(unrounded);
		BigDecimal rounded = bd.setScale(precision, roundingMode);
		return rounded.doubleValue();
	}

	private final LocationListener locationListener = new LocationListener() { // location listener
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
		}
	};

	private void setTimer() { // setting up timer
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

	private void runNextTimedTask() { // task manager for timer
		if (!isPaused) {
			// run my task.
			time += 1;		
			TextView t = new TextView(this); 
			t = (TextView)findViewById(R.id.myTimerText); 
			hours = time / 3600;
			minutes = (time % 3600) / 60;
			seconds = time % 60;
			String timeString = "";
			if (hours < 10) {
				timeString += "0" + hours + ":";
			} else {
				timeString += hours + ":";
			}
			if (minutes < 10) {
				timeString += "0" + minutes + ":";
			} else {
				timeString += minutes + ":";
			}
			if (seconds < 10) {
				timeString += "0" + seconds;
			} else {
				timeString += seconds;
			}

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
	}

	// Method for calculating speed
	private double calculateSpeed() {
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

	// Method that returns Measurement for distance from preferences
	private String getDistanceMeasurement() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Navigation.this);
		String listPrefs = prefs.getString("listpref", "Please select a measurement in settings.");
		if (listPrefs.equals("Miles is selected")) {
			return "mi";
		} else if (listPrefs.equals("Kilometres is selected")) {
			return "km";
		} else {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			// set title
			alertDialogBuilder.setTitle("No Measurement Chosen!");
			// set dialog message
			alertDialogBuilder
			.setMessage("Please choose a measurement in settings.")
			.setCancelable(false)
			.setPositiveButton("Close",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, close
					// current activity
					dialog.cancel();	
				}
			})
			.setNegativeButton("Settings",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// Go to Settings
					startActivity(new Intent(Navigation.this, Preferences.class));
				}
			});
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show it
			alertDialog.show();
			return listPrefs;
		}
	}

	// Method that returns Measurement for speed from preferences
	private String getSpeedMeasurement() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Navigation.this);
		String listPrefs = prefs.getString("listpref", "Please select a measurement in settings.");
		if (listPrefs.equals("Miles is selected")) {
			return "mph";
		} else if (listPrefs.equals("Kilometres is selected")) {
			return "kph";
		} else {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			// set title
			alertDialogBuilder.setTitle("No Measurement Chosen!");
			// set dialog message
			alertDialogBuilder
			.setMessage("Please choose a measurement in settings.")
			.setCancelable(false)
			.setPositiveButton("Close",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, close
					// current activity
					dialog.cancel();	
				}
			})
			.setNegativeButton("Settings",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// Go to Settings
					startActivity(new Intent(Navigation.this, Preferences.class));
				}
			});
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show it
			alertDialog.show();
			return listPrefs;
		}
	}

	// MAP STUFF
	@Override
	protected boolean isRouteDisplayed() {
		// Required by MapActivity
		return false;
	}

	// MAP STUFF
	private void initMapView() {
		myMap = (MapView) findViewById(R.id.halfMap);
		controller = myMap.getController();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Navigation.this);
		String listPrefs = prefs.getString("listMapPref", "Please select a measurement in settings.");
		if (listPrefs.equals("Terrain is selected")) {
			myMap.setSatellite(false);
		} else if (listPrefs.equals("Satelite is selected")) {
			myMap.setSatellite(true);
		}
		myMap.setBuiltInZoomControls(true);

	}

	// MAP STUFF
	private void initMyLocation() {

		final MyLocationOverlay overlay = new MyLocationOverlay(this, myMap);
		overlay.enableMyLocation();
		// overlay.enableCompass(); // does not work in emulator
		overlay.runOnFirstFix(new Runnable() {
			public void run() {
				// Zoom in to current location
				controller.setZoom(20);
				controller.animateTo(overlay.getMyLocation());
			}
		});
		myMap.getOverlays().add(overlay);
	}

}
