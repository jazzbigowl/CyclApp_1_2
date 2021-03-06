/*
 * Author: Jeremy Bouchat
 * Year: 2013
 * Project Report: https://www.dropbox.com/s/8ba5y8kax3lqhz5/Report.docx
 */
package com.example.cyclapp_1_2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class SQLiteAdapter {

	public static final String MYDATABASE_NAME = "CYCLAPP_1_2_DATABASE";
	public static final String MYDATABASE_TABLE = "RIDE_HISTORY_TABLE";
	public static final int MYDATABASE_VERSION = 1;
	public static final String KEY_ID = "_id";
	public static final String KEY_NAME = "TheName";
	public static final String KEY_DATE = "TheDate";
	public static final String KEY_TIME = "TheTime";
	public static final String KEY_TRIP_TIME = "TheTimeoftheTrip";
	public static final String KEY_TRIP_DISTANCE = "TheDistanceoftheTrip";
	public static final String KEY_START_TIME = "TheTimeattheStart";
	public static final String KEY_END_TIME = "TheTimeattheEnd";
	public static final String KEY_AVERAGE_SPEED = "TheAverageSpeed";
	public static final String KEY_START_LAT = "StartLatitude";
	public static final String KEY_START_LON = "StartLongitude";
	public static final String KEY_END_LAT = "EndLatitude";
	public static final String KEY_END_LON = "EndLongitude";
	public static final String KEY_SPEEDS = "AllSpeeds";
	public static final String KEY_LOCATIONS = "AllLocations";
	public static final String KEY_TIMES = "AllTimes";

	//create table MY_DATABASE (ID integer primary key, Content text not null);
	private static final String SCRIPT_CREATE_DATABASE =
			"create table " + MYDATABASE_TABLE + " ("
					+ KEY_ID + " integer primary key autoincrement, "
					+ KEY_NAME + " text not null, "
					+ KEY_DATE + " text not null, "
					+ KEY_TIME + " text not null, "
					+ KEY_TRIP_TIME + " text not null, "
					+ KEY_TRIP_DISTANCE + " text not null, "
					+ KEY_START_TIME + " text not null, "
					+ KEY_END_TIME + " text not null, "
					+ KEY_START_LAT + " text not null, "
					+ KEY_START_LON + " text not null, "
					+ KEY_END_LAT + " text not null, "
					+ KEY_END_LON + " text not null, "
					+ KEY_SPEEDS + " text not null, "
					+ KEY_LOCATIONS + " text not null, "
					+ KEY_TIMES + " text not null, "
					+ KEY_AVERAGE_SPEED + " text not null);";

	public SQLiteHelper sqLiteHelper;
	public SQLiteDatabase sqLiteDatabase;

	public Context context;

	/**
	 * Class Constructor
	 *
	 * @param  c  Context passed into super
	 */
	public SQLiteAdapter(Context c){
		context = c;
	}

	/**
	 * Closes SQLiteHelper
	 */
	public void close(){
		sqLiteHelper.close();
	}

	/**
	 * Delete database
	 *
	 */
	public int deleteAll(){//delete all records
		return sqLiteDatabase.delete(MYDATABASE_TABLE, null, null);
	}

	/**
	 * Delete one record in database
	 *
	 * @param  id  The id number of the record to delete
	 */
	public void delete_byID(int id){//delete one record
		sqLiteDatabase.delete(MYDATABASE_TABLE, KEY_ID+"="+id, null);
	}

	public class SQLiteHelper extends SQLiteOpenHelper {

		public SQLiteHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(SCRIPT_CREATE_DATABASE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	} 
}