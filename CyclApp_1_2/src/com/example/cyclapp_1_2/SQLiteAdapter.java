package com.example.cyclapp_1_2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class SQLiteAdapter {

	public static final String MYDATABASE_NAME = "CYCLAPP_1_2_DATABASE";
	public static final String MYDATABASE_TABLE = "RIDE_HISTORY_TABLE";
	public static final int MYDATABASE_VERSION = 1;
	public static final String KEY_ID = "_id";
	public static final String KEY_CONTENT1 = "somefirstcontent";
	public static final String KEY_CONTENT2 = "somesecondcontent";
	public static final String KEY_DATE = "TheDate";
	public static final String KEY_TRIP_TIME = "TheTimeoftheTrip";
	public static final String KEY_TRIP_DISTANCE = "TheDistanceoftheTrip";
	public static final String KEY_START_TIME = "TheTimeattheStart";
	public static final String KEY_END_TIME = "TheTimeattheEnd";
	public static final String KEY_AVERAGE_SPEED = "TheAverageSpeed";

	//create table MY_DATABASE (ID integer primary key, Content text not null);
	private static final String SCRIPT_CREATE_DATABASE =
			"create table " + MYDATABASE_TABLE + " ("
					+ KEY_ID + " integer primary key autoincrement, "
					+ KEY_CONTENT1 + " text not null, "
					+ KEY_CONTENT2 + " text not null, "
					+ KEY_DATE + " text not null, "
					+ KEY_TRIP_TIME + " text not null, "
					+ KEY_TRIP_DISTANCE + " text not null, "
					+ KEY_START_TIME + " text not null, "
					+ KEY_END_TIME + " text not null, "
					+ KEY_AVERAGE_SPEED + " text not null);";

	private SQLiteHelper sqLiteHelper;
	private SQLiteDatabase sqLiteDatabase;

	private Context context;

	public SQLiteAdapter(Context c){
		context = c;
	}

	public SQLiteAdapter openToRead() throws android.database.SQLException {
		sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, null, MYDATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getReadableDatabase();
		return this; 
	}

	public SQLiteAdapter openToWrite() throws android.database.SQLException {
		sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, null, MYDATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getWritableDatabase();
		return this; 
	}

	public void close(){
		sqLiteHelper.close();
	}

	public long insert(String content1, String content2){

		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_CONTENT1, content1);
		contentValues.put(KEY_CONTENT2, content2);
		contentValues.put(KEY_DATE, "something");
		contentValues.put(KEY_TRIP_TIME, "something");
		contentValues.put(KEY_TRIP_DISTANCE, "something");
		contentValues.put(KEY_START_TIME, "something");
		contentValues.put(KEY_END_TIME, "something");
		contentValues.put(KEY_AVERAGE_SPEED, "something");
		return sqLiteDatabase.insert(MYDATABASE_TABLE, null, contentValues);
	}

	public int deleteAll(){
		return sqLiteDatabase.delete(MYDATABASE_TABLE, null, null);
	}

	public void delete_byID(int id){
		sqLiteDatabase.delete(MYDATABASE_TABLE, KEY_ID+"="+id, null);
	}

	public void update_byID(int id, String v1, String v2){
		ContentValues values = new ContentValues();
		values.put(KEY_CONTENT1, v1);
		values.put(KEY_CONTENT2, v2);
		sqLiteDatabase.update(MYDATABASE_TABLE, values, KEY_ID+"="+id, null);
	}

	public Cursor queueAll(){
		String[] columns = new String[]{KEY_ID, KEY_CONTENT1, KEY_CONTENT2, 
				KEY_DATE, KEY_TRIP_TIME, KEY_TRIP_DISTANCE, KEY_START_TIME, 
				KEY_END_TIME, KEY_AVERAGE_SPEED};
		Cursor cursor = sqLiteDatabase.query(MYDATABASE_TABLE, columns,
				null, null, null, null, null);

		return cursor;
	}

	public Cursor queueAll_SortBy_CONTENT1(){
		String[] columns = new String[]{KEY_ID, KEY_CONTENT1, KEY_CONTENT2, 
				KEY_DATE, KEY_TRIP_TIME, KEY_TRIP_DISTANCE, KEY_START_TIME, 
				KEY_END_TIME, KEY_AVERAGE_SPEED};
		Cursor cursor = sqLiteDatabase.query(MYDATABASE_TABLE, columns, 
				null, null, null, null, KEY_CONTENT1);

		return cursor;
	}

	public Cursor queueAll_SortBy_CONTENT2(){
		String[] columns = new String[]{KEY_ID, KEY_CONTENT1, KEY_CONTENT2, 
				KEY_DATE, KEY_TRIP_TIME, KEY_TRIP_DISTANCE, KEY_START_TIME, 
				KEY_END_TIME, KEY_AVERAGE_SPEED};
		Cursor cursor = sqLiteDatabase.query(MYDATABASE_TABLE, columns, 
				null, null, null, null, KEY_CONTENT2);

		return cursor;
	}

	public class SQLiteHelper extends SQLiteOpenHelper {

		public SQLiteHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(SCRIPT_CREATE_DATABASE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
		}
	} 
}