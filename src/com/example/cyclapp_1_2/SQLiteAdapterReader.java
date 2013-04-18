/*
 * Author: Jeremy Bouchat
 * Year: 2013
 * Project Report: https://www.dropbox.com/s/8ba5y8kax3lqhz5/Report.docx
 */
package com.example.cyclapp_1_2;
import android.content.Context;
import android.database.Cursor;

import com.example.cyclapp_1_2.SQLiteAdapter;


public class SQLiteAdapterReader extends SQLiteAdapter {

	/**
	 * Class Constructor
	 *
	 * @param  c  Context passed into super
	 */
	public SQLiteAdapterReader(Context c) {
		super(c);
	}
	
	/**
	 * Opens database so that it can be read from.
	 */
	public SQLiteAdapter openToRead() throws android.database.SQLException {
		sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, null, MYDATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getReadableDatabase();
		return this; 
	}

	/**
	 * Get all records queued into cursor
	 *
	 * @return      Cursor containing queued up records
	 */
	public Cursor queueAll(){
		String[] columns = new String[]{KEY_ID, KEY_NAME, 
				KEY_DATE, KEY_TIME, KEY_TRIP_TIME, KEY_TRIP_DISTANCE, KEY_START_TIME, 
				KEY_END_TIME, KEY_AVERAGE_SPEED, KEY_START_LAT, KEY_START_LON, KEY_END_LAT, KEY_END_LON, KEY_SPEEDS, KEY_LOCATIONS, KEY_TIMES};
		Cursor cursor = sqLiteDatabase.query(MYDATABASE_TABLE, columns,
				null, null, null, null, null);
		cursor.moveToFirst();

		return cursor;
	}
	
	/**
	 * Get one record from database into cursor
	 *
	 * @param  id	The record number to retrieve.
	 * @return      Cursor containing record
	 */
	public Cursor queueOne(Integer id){
		Cursor cursor = sqLiteDatabase.rawQuery("select * from " + MYDATABASE_TABLE + " where " + 
				KEY_ID + "=" + id, null);
		return cursor;
	}

	/**
	 * Get all records queued into cursor ordered by name
	 *
	 * @return      Cursor containing queued up records
	 */
	public Cursor queueAll_SortBy_NAME(){
		String[] columns = new String[]{KEY_ID, KEY_NAME, 
				KEY_DATE, KEY_TIME, KEY_TRIP_TIME, KEY_TRIP_DISTANCE, KEY_START_TIME, 
				KEY_END_TIME, KEY_AVERAGE_SPEED, KEY_START_LAT, KEY_START_LON, KEY_END_LAT, KEY_END_LON, KEY_SPEEDS, KEY_LOCATIONS, KEY_TIMES};
		Cursor cursor = sqLiteDatabase.query(MYDATABASE_TABLE, columns, 
				null, null, null, null, KEY_NAME);
		cursor.moveToFirst();

		return cursor;
	}

	/**
	 * Get all records queued into cursor ordered by data
	 *
	 * @return      Cursor containing queued up records
	 */
	public Cursor queueAll_SortBy_DATE(){
		String[] columns = new String[]{KEY_ID, KEY_NAME, 
				KEY_DATE, KEY_TIME, KEY_TRIP_TIME, KEY_TRIP_DISTANCE, KEY_START_TIME, 
				KEY_END_TIME, KEY_AVERAGE_SPEED, KEY_START_LAT, KEY_START_LON, KEY_END_LAT, KEY_END_LON, KEY_SPEEDS, KEY_LOCATIONS, KEY_TIMES};
		Cursor cursor = sqLiteDatabase.query(MYDATABASE_TABLE, columns, 
				null, null, null, null, KEY_DATE);

		return cursor;
	}


}
