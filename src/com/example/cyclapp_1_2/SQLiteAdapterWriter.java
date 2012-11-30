package com.example.cyclapp_1_2;

import android.content.ContentValues;
import android.content.Context;

public class SQLiteAdapterWriter extends SQLiteAdapter {

	public SQLiteAdapterWriter(Context c) {
		super(c);
	}
	
	public SQLiteAdapter openToWrite() throws android.database.SQLException {
		sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, null, MYDATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getWritableDatabase();
		return this; 
	}
	
	public long insert(String name, String time, String startLat, String startLon, String endLat, String endLon, String aveSpeed, String startTime, String endTime, String distance, String date, String speeds){

		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_NAME, name);
		contentValues.put(KEY_DATE, date);
		contentValues.put(KEY_TRIP_TIME, time);
		contentValues.put(KEY_TRIP_DISTANCE, distance);
		contentValues.put(KEY_START_TIME, startTime);
		contentValues.put(KEY_END_TIME, endTime);
		contentValues.put(KEY_START_LAT, startLat);
		contentValues.put(KEY_START_LON, startLon);
		contentValues.put(KEY_END_LAT, endLat);
		contentValues.put(KEY_END_LON, endLon);
		contentValues.put(KEY_AVERAGE_SPEED, aveSpeed);
		contentValues.put(KEY_SPEEDS, speeds);
		return sqLiteDatabase.insert(MYDATABASE_TABLE, null, contentValues);
	}
	
	public void update_byID(int id, String v1){
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, v1);
		sqLiteDatabase.update(MYDATABASE_TABLE, values, KEY_ID+"="+id, null);
	}

}
