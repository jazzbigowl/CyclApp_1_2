package com.example.cyclapp_1_2;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.view.Menu;
import android.widget.SimpleCursorAdapter;
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
        
        TextView myHelloText;
        myHelloText = (TextView)findViewById(R.id.label);
        myHelloText.setText("The ID is: " + extras.getInt("myKey"));
        
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
		
		String penis = item_id + "\n" + item_name + "\n" + item_date + "\n" + item_distance + "\n" + item_speed 
				+ "\n" + item_start + "\n" + item_end + "\n" + item_time + "\n" + item_startLat + "\n" + item_startLon 
				+ "\n" + item_endLat + "\n" + item_endLon + "\n"  + "\n" + bitch;
		myHelloText.setText(penis);
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
