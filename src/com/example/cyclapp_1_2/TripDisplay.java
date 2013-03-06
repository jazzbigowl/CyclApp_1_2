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
import android.view.Menu;
import android.widget.TextView;

public class TripDisplay extends Activity {
	int key;
	private SQLiteAdapterReader mySQLiteAdapterReader;
//	SimpleCursorAdapter cursorAdapter;
	Cursor cursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_display);
        Bundle extras = getIntent().getExtras();
        key = extras.getInt("myKey");
        
//        TextView myHelloText;
//        myHelloText = (TextView)findViewById(R.id.label);
//        myHelloText.setText("The ID is: " + extras.getInt("myKey"));
        
        mySQLiteAdapterReader = new SQLiteAdapterReader(this);
		mySQLiteAdapterReader.openToRead();

		cursor = mySQLiteAdapterReader.queueOne(key);
		cursor.moveToFirst();
		
		
		
		final int item_id = cursor.getInt(cursor.getColumnIndex(SQLiteAdapter.KEY_ID));
		String item_name = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_NAME));
		String item_date = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_DATE));
		String item_distance = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_TRIP_DISTANCE));
		String item_speed = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_AVERAGE_SPEED));
		String item_start = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_START_TIME));
		String item_end = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_END_TIME));
		String item_time = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_TRIP_TIME));
		String item_startLat = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_START_LAT));
		String item_startLon = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_START_LON));
		String item_endLat = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_END_LAT));
		String item_endLon = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_END_LON));
		ArrayList<String> item_speeds = (ArrayList<String>) splitIt(cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_SPEEDS)));
		
		String bitch = "";
		for (String s: item_speeds) {
			bitch += s + ", ";
		}
		
//		String penis = "id: " + item_id + "\nname: " + item_name + "\ndate: " + item_date + "\ndistance: " + item_distance 
//				+ "\nspeed: " + item_speed + "\nstart time:"  + item_start + "\nend time: " + item_end 
//				+ "\ntime: " + item_time + "\nstart lat: " + item_startLat + "\nstart lon: " + item_startLon 
//				+ "\nend lat: " + item_endLat + "\nend lon: " + item_endLon + "\n"  + "\nall speeds array: " + bitch;
//		myHelloText.setText(penis);
		
		TextView tripName;
		TextView tripDate;
		TextView tripDistance;
		TextView tripSpeed;
		TextView tripTime;
		TextView tripSpeeds;
		tripName = (TextView)findViewById(R.id.trip_name);
		tripDate = (TextView)findViewById(R.id.trip_date);
		tripDistance = (TextView)findViewById(R.id.trip_distance);
		tripSpeed = (TextView)findViewById(R.id.trip_speed);
		tripTime = (TextView)findViewById(R.id.trip_time);
		tripSpeeds = (TextView)findViewById(R.id.trip_speeds);
		tripName.setText(item_name);
		tripDate.setText(item_date);
		if (getDistanceMeasurement().equals("mi")) {
			tripDistance.setText(Double.toString(round(Double.parseDouble(item_distance), 2, BigDecimal.ROUND_HALF_UP)) + " " + getDistanceMeasurement());
		} else {
			tripDistance.setText(Double.toString(round((Double.parseDouble(item_distance) * 1.60934), 2, BigDecimal.ROUND_HALF_UP)) + " " + getDistanceMeasurement());
		}
		if (getSpeedMeasurement().equals("mph")) {
			tripSpeed.setText(Double.toString(Double.parseDouble(item_speed)) + " " + getSpeedMeasurement());
		} else {
			tripSpeed.setText(Double.toString(round((Double.parseDouble(item_speed)* 1.60934), 2, BigDecimal.ROUND_HALF_UP)) + " " + getSpeedMeasurement());
		}
		tripTime.setText(item_time);
		tripSpeeds.setText(bitch);
		
    }
    
	public static double round(double unrounded, int precision, int roundingMode) {
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
    
    public static List<String> splitIt(String source) {
	  Pattern p = Pattern.compile("\\b([0-9]+)(\\.)([0-9]+)\\b");
	  Matcher m = p.matcher(source);
	  List<String> result = new ArrayList<String>();
	  while (m.find()) {
	    result.add(m.group());
	  }
	  return result;
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }
}
