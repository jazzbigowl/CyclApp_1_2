package com.example.cyclapp_1_2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class History extends Activity {

	private SQLiteAdapterReader mySQLiteAdapterReader;
	ListView listContent;

	SimpleCursorAdapter cursorAdapter;
	Cursor cursor;


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.history_settings, menu);
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.sort_date:
			cursor = mySQLiteAdapterReader.queueAll_SortBy_DATE();
			String[] from1 = new String[]{SQLiteAdapter.KEY_DATE, SQLiteAdapter.KEY_NAME, SQLiteAdapter.KEY_TIME};
			int[] to1 = new int[]{R.id.text2, R.id.text1, R.id.text3};
			cursorAdapter =
					new SimpleCursorAdapter(History.this, R.layout.row, cursor, from1, to1);
			listContent.setAdapter(cursorAdapter);
			updateList();
			return true;
		case R.id.sort_name:
			cursor = mySQLiteAdapterReader.queueAll_SortBy_NAME();
			String[] from2 = new String[]{SQLiteAdapter.KEY_NAME, SQLiteAdapter.KEY_DATE, SQLiteAdapter.KEY_TIME};
			int[] to2 = new int[]{R.id.text1, R.id.text2, R.id.text3};
			cursorAdapter =
					new SimpleCursorAdapter(History.this, R.layout.row, cursor, from2, to2);
			listContent.setAdapter(cursorAdapter);
			updateList();
			return true;
		case R.id.refresh:
			updateList();
			return true;
		case R.id.delete_all:
			new AlertDialog.Builder(this)
			.setMessage("Are you sure you want to delete all?")
			.setCancelable(false)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					mySQLiteAdapterReader.deleteAll();
					updateList();
				}
			})
			.setNegativeButton("No", null)
			.show();
			return true;
		case R.id.menu_settings:
			startActivity(new Intent(History.this, Preferences.class));
			return true;
		case R.id.exit:
			new AlertDialog.Builder(this)
			.setMessage("Are you sure you want to exit?")
			.setCancelable(false)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					//CustomTabActivity.this.finish();
					History.this.finish();
				}
			})
			.setNegativeButton("No", null)
			.show();
			//this.finish();
			return true;
		}
		return false;
	}


	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);

		listContent = (ListView)findViewById(R.id.contentlist);

		mySQLiteAdapterReader = new SQLiteAdapterReader(this);
		mySQLiteAdapterReader.openToRead();

		cursor = mySQLiteAdapterReader.queueAll();
		String[] from = new String[]{SQLiteAdapter.KEY_NAME, SQLiteAdapter.KEY_DATE, SQLiteAdapter.KEY_TIME};
		int[] to = new int[]{R.id.text1, R.id.text2, R.id.text3};
		cursorAdapter =	new SimpleCursorAdapter(this, R.layout.row, cursor, from, to);
		listContent.setAdapter(cursorAdapter);
		listContent.setOnItemClickListener(listContentOnItemClickListener);
	}

	private ListView.OnItemClickListener listContentOnItemClickListener
	= new ListView.OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent td = new Intent(History.this, TripDisplay.class);
			Cursor cursor = (Cursor) parent.getItemAtPosition(position);
			final int item_id = cursor.getInt(cursor.getColumnIndex(SQLiteAdapter.KEY_ID));
			td.putExtra("myKey", item_id);
			startActivity(td);
		}};

		@Override
		protected void onDestroy() {
			super.onDestroy();
			mySQLiteAdapterReader.close();
		}

		@SuppressWarnings("deprecation")
		private void updateList(){
			cursor.requery();
		}

}
