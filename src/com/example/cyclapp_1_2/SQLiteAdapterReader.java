package com.example.cyclapp_1_2;
import android.content.Context;
import android.database.Cursor;

import com.example.cyclapp_1_2.SQLiteAdapter;


public class SQLiteAdapterReader extends SQLiteAdapter {

	public SQLiteAdapterReader(Context c) {
		super(c);
	}
	
	public SQLiteAdapter openToRead() throws android.database.SQLException {
		sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, null, MYDATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getReadableDatabase();
		return this; 
	}

	
	public Cursor queueAll(){
		String[] columns = new String[]{KEY_ID, KEY_NAME, 
				KEY_DATE, KEY_TRIP_TIME, KEY_TRIP_DISTANCE, KEY_START_TIME, 
				KEY_END_TIME, KEY_AVERAGE_SPEED, KEY_START_LAT, KEY_START_LON, KEY_END_LAT, KEY_END_LON, KEY_SPEEDS, KEY_LOCATIONS};
		Cursor cursor = sqLiteDatabase.query(MYDATABASE_TABLE, columns,
				null, null, null, null, null);
		cursor.moveToFirst();

		return cursor;
	}
	
	public Cursor queueOne(Integer id){
		Cursor cursor = sqLiteDatabase.rawQuery("select * from " + MYDATABASE_TABLE + " where " + 
				KEY_ID + "=" + id  , null);
		return cursor;
	}

	public Cursor queueAll_SortBy_NAME(){
		String[] columns = new String[]{KEY_ID, KEY_NAME, 
				KEY_DATE, KEY_TRIP_TIME, KEY_TRIP_DISTANCE, KEY_START_TIME, 
				KEY_END_TIME, KEY_AVERAGE_SPEED, KEY_START_LAT, KEY_START_LON, KEY_END_LAT, KEY_END_LON, KEY_SPEEDS, KEY_LOCATIONS};
		Cursor cursor = sqLiteDatabase.query(MYDATABASE_TABLE, columns, 
				null, null, null, null, KEY_NAME);
		cursor.moveToFirst();

		return cursor;
	}

	public Cursor queueAll_SortBy_DATE(){
		String[] columns = new String[]{KEY_ID, KEY_NAME, 
				KEY_DATE, KEY_TRIP_TIME, KEY_TRIP_DISTANCE, KEY_START_TIME, 
				KEY_END_TIME, KEY_AVERAGE_SPEED, KEY_START_LAT, KEY_START_LON, KEY_END_LAT, KEY_END_LON, KEY_SPEEDS, KEY_LOCATIONS};
		Cursor cursor = sqLiteDatabase.query(MYDATABASE_TABLE, columns, 
				null, null, null, null, KEY_DATE);

		return cursor;
	}


}
