/*
 * Author: Jeremy Bouchat
 * Year: 2013
 * Project Report: https://www.dropbox.com/s/8ba5y8kax3lqhz5/Report.docx
 */
package com.example.cyclapp_1_2;

import android.content.ContentValues;
import android.content.Context;

public class SQLiteAdapterWriter extends SQLiteAdapter {

	/**
	 * Class constructor.
	 *
	 * @param  c  Context passed into super
	 */
	public SQLiteAdapterWriter(Context c) {
		super(c);
	}
	
	/**
	 * Open database so that it can be writen to.
	 */
	public SQLiteAdapter openToWrite() throws android.database.SQLException {
		sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, null, MYDATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getWritableDatabase();
		return this; 
	}
	
	/**
	 * Add a record to database.
	 *
	 * @param  name  Name of record to store.
	 * @param  tripTime Time of record to store.
	 * @param  startLat Start Latitude of record to store.
	 * @param  startLon Start Longitude of record to store.
	 * @param  endLat End Latitude of record to store.
	 * @param  endLon End Longitude of record to store.
	 * @param  aveSpeed Average speed of record to store.
	 * @param  starTime Start time of record to store.
	 * @param  endTime End time of record to store.
	 * @param  distance Distance of record to store.
	 * @param  date Date of record to store.
	 * @param  time The time of record to store.
	 * @param  speeds Speeds of record to store.
	 * @param  locations Locations of record to store.
	 * @param  times Times of record to store.
	 */
	public long insert(String name, String tripTime, String startLat, String startLon, String endLat, String endLon, String aveSpeed, String startTime, String endTime, String distance, String date, String time, String speeds, String locations, String times){

		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_NAME, name);
		contentValues.put(KEY_DATE, date);
		contentValues.put(KEY_TIME, time);
		contentValues.put(KEY_TRIP_TIME, tripTime);
		contentValues.put(KEY_TRIP_DISTANCE, distance);
		contentValues.put(KEY_START_TIME, startTime);
		contentValues.put(KEY_END_TIME, endTime);
		contentValues.put(KEY_START_LAT, startLat);
		contentValues.put(KEY_START_LON, startLon);
		contentValues.put(KEY_END_LAT, endLat);
		contentValues.put(KEY_END_LON, endLon);
		contentValues.put(KEY_AVERAGE_SPEED, aveSpeed);
		contentValues.put(KEY_SPEEDS, speeds);
		contentValues.put(KEY_LOCATIONS, locations);
		contentValues.put(KEY_TIMES, times);
		return sqLiteDatabase.insert(MYDATABASE_TABLE, null, contentValues);
	}
	
	/**
	 * Update a record in database.
	 *
	 * @param  id	Record key to update.
	 * @param  v1	New values of record.
	 */
	public void update_byID(int id, String v1){
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, v1);
		sqLiteDatabase.update(MYDATABASE_TABLE, values, KEY_ID+"="+id, null);
	}

}
