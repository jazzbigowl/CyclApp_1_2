/*
 * Author: Jeremy Bouchat
 * Year: 2013
 * Project Report: https://www.dropbox.com/s/8ba5y8kax3lqhz5/Report.docx
 */
package com.example.cyclapp_1_2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class TripDisplay extends Activity implements OnClickListener {
	int key;
	String locations;
	private SQLiteAdapterReader mySQLiteAdapterReader;
	Cursor cursor;
	ListView lv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trip_display);
		Bundle extras = getIntent().getExtras();
		key = extras.getInt("myKey");

		// initiate database reader
		mySQLiteAdapterReader = new SQLiteAdapterReader(this);
		mySQLiteAdapterReader.openToRead();

		cursor = mySQLiteAdapterReader.queueOne(key);
		cursor.moveToFirst();

		// button listeners
		View viewRouteButton = findViewById(R.id.view_trip_button);
		viewRouteButton.setOnClickListener(TripDisplay.this);
		View viewSpeedsButton = findViewById(R.id.view_speeds_button);
		viewSpeedsButton.setOnClickListener(TripDisplay.this);
		View deleteButton = findViewById(R.id.delete_button);
		deleteButton.setOnClickListener(TripDisplay.this);

		// get needed variables from database
		String item_name = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_NAME));
		String item_date = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_DATE));
		String item_time = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_TIME));
		String item_distance = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_TRIP_DISTANCE));
		String item_speed = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_AVERAGE_SPEED));
		String item_trip_time = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_TRIP_TIME));
		ArrayList<String> item_speeds = (ArrayList<String>) splitSpeeds(cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_SPEEDS)));

		// work out maximum speed
		Double max = 0.0;
		for (String s: item_speeds) {
			Double newSpeed = (round(Double.parseDouble(s), 2, BigDecimal.ROUND_HALF_UP));
			if (newSpeed > max) {
				max = newSpeed;
			}
		}


		TextView tripName;
		TextView tripDate;
		TextView tripDistance;
		TextView tripSpeed;
		TextView tripMaxSpeed;
		TextView tripTime;
		tripName = (TextView)findViewById(R.id.trip_name);
		tripDate = (TextView)findViewById(R.id.trip_date);
		tripDistance = (TextView)findViewById(R.id.trip_distance);
		tripSpeed = (TextView)findViewById(R.id.trip_speed);
		tripMaxSpeed = (TextView)findViewById(R.id.trip_max_speed);
		tripTime = (TextView)findViewById(R.id.trip_time);
		tripName.setText(item_name);
		tripDate.setText(item_time.substring(0, 5) + " on " + item_date);

		// display distance
		if (getDistanceMeasurement().equals("mi")) {
			tripDistance.setText(Double.toString(round(Double.parseDouble(item_distance), 2, BigDecimal.ROUND_HALF_UP)) + " " + getDistanceMeasurement());
		} else {
			tripDistance.setText(Double.toString(round((Double.parseDouble(item_distance) * 1.60934), 2, BigDecimal.ROUND_HALF_UP)) + " " + getDistanceMeasurement());
		}
		
		// display speed
		if (getSpeedMeasurement().equals("mph")) {
			tripSpeed.setText(Double.toString(round(Double.parseDouble(item_speed), 2, BigDecimal.ROUND_HALF_UP)) + " " + getSpeedMeasurement());
			tripMaxSpeed.setText(Double.toString(max) + " " + getSpeedMeasurement());
		} else {
			tripSpeed.setText(Double.toString(round((Double.parseDouble(item_speed)* 1.60934), 2, BigDecimal.ROUND_HALF_UP)) + " " + getSpeedMeasurement());
			tripMaxSpeed.setText(Double.toString(round((max * 1.60934), 2, BigDecimal.ROUND_HALF_UP)) + " " + getSpeedMeasurement());
		}

		//display time
		tripTime.setText(formatTime(Integer.parseInt(item_trip_time)));
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.view_trip_button:
			Intent routeMap = new Intent(this, RouteMap.class);
			routeMap.putExtra("key", key);
			routeMap.putExtra("locations", locations);
			startActivity(routeMap);
			break;
		case R.id.view_speeds_button:
			LineGraph line = new LineGraph(cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_SPEEDS)), cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_TIMES)), getSpeedMeasurement());
			Intent lineIntent = line.getIntent(this);
			startActivity(lineIntent);
			break;
		case R.id.delete_button:

			new AlertDialog.Builder(this)
			.setMessage("Are you sure you want to delete this trip?")
			.setCancelable(false)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					mySQLiteAdapterReader.delete_byID(key);
					TripDisplay.this.finish();
				}
			})
			.setNegativeButton("No", null)
			.show();
			break;
		}
	}

	private String formatTime(Integer time) {  //format time for displaying HH:MM:SS
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
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

		return timeString;
	}

	private static double round(double unrounded, int precision, int roundingMode) { //method for rounding doubles
		BigDecimal bd = new BigDecimal(unrounded);
		BigDecimal rounded = bd.setScale(precision, roundingMode);
		return rounded.doubleValue();
	}

	// Method that returns Measurement for distance from preferences
	private String getDistanceMeasurement() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(TripDisplay.this);
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
					startActivity(new Intent(TripDisplay.this, Preferences.class));
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
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(TripDisplay.this);
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
					startActivity(new Intent(TripDisplay.this, Preferences.class));
				}
			});
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show it
			alertDialog.show();
			return listPrefs;
		}
	}

	private static List<String> splitSpeeds(String source) {
		Pattern p = Pattern.compile("\\b([0-9]+)(\\.)([0-9]+)\\b");
		Matcher m = p.matcher(source);
		List<String> result = new ArrayList<String>();
		while (m.find()) {
			result.add(m.group());
		}
		return result;
	}

}
