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
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class History extends Activity {

	EditText inputContent1, inputContent2;
	Button buttonAdd, buttonDeleteAll;
	Button buttonSortDefault, buttonSort1;

	private SQLiteAdapter mySQLiteAdapter;
	ListView listContent;

	SimpleCursorAdapter cursorAdapter;
	Cursor cursor;


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
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
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);

		inputContent1 = (EditText)findViewById(R.id.content1);
		inputContent2 = (EditText)findViewById(R.id.content2);
		buttonAdd = (Button)findViewById(R.id.add);
		buttonDeleteAll = (Button)findViewById(R.id.deleteall);

		listContent = (ListView)findViewById(R.id.contentlist);

		mySQLiteAdapter = new SQLiteAdapter(this);
		mySQLiteAdapter.openToWrite();

		cursor = mySQLiteAdapter.queueAll();
		String[] from = new String[]{SQLiteAdapter.KEY_ID, SQLiteAdapter.KEY_CONTENT1, SQLiteAdapter.KEY_CONTENT2};
		int[] to = new int[]{R.id.id, R.id.text1, R.id.text2};
		cursorAdapter =
				new SimpleCursorAdapter(this, R.layout.row, cursor, from, to);
		listContent.setAdapter(cursorAdapter);
		listContent.setOnItemClickListener(listContentOnItemClickListener);

		buttonAdd.setOnClickListener(buttonAddOnClickListener);
		buttonDeleteAll.setOnClickListener(buttonDeleteAllOnClickListener);

		buttonSortDefault = (Button)findViewById(R.id.sortbyDefault);
		buttonSort1 =  (Button)findViewById(R.id.sortby1);

		buttonSortDefault.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				cursor = mySQLiteAdapter.queueAll();
				String[] from = new String[]{SQLiteAdapter.KEY_ID, SQLiteAdapter.KEY_CONTENT1, SQLiteAdapter.KEY_CONTENT2};
				int[] to = new int[]{R.id.id, R.id.text1, R.id.text2};
				cursorAdapter =
						new SimpleCursorAdapter(History.this, R.layout.row, cursor, from, to);
				listContent.setAdapter(cursorAdapter);
				updateList();
			}});

		buttonSort1.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				cursor = mySQLiteAdapter.queueAll_SortBy_CONTENT2();
				String[] from = new String[]{SQLiteAdapter.KEY_ID, SQLiteAdapter.KEY_CONTENT1, SQLiteAdapter.KEY_CONTENT2};
				int[] to = new int[]{R.id.id, R.id.text1, R.id.text2};
				cursorAdapter =
						new SimpleCursorAdapter(History.this, R.layout.row, cursor, from, to);
				listContent.setAdapter(cursorAdapter);
				updateList();
			}});

	}

	Button.OnClickListener buttonAddOnClickListener
	= new Button.OnClickListener(){

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			String data1 = inputContent1.getText().toString();
			String data2 = inputContent2.getText().toString();
			mySQLiteAdapter.insert(data1, data2);
			updateList();
		}

	};

	Button.OnClickListener buttonDeleteAllOnClickListener
	= new Button.OnClickListener(){

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			mySQLiteAdapter.deleteAll();
			updateList();
		}

	};

	private ListView.OnItemClickListener listContentOnItemClickListener
	= new ListView.OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub

			Cursor cursor = (Cursor) parent.getItemAtPosition(position);
			final int item_id = cursor.getInt(cursor.getColumnIndex(SQLiteAdapter.KEY_ID));
			String item_content1 = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_CONTENT1));
			String item_content2 = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_CONTENT2));
			String item_date = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_DATE));
			String item_distance = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_TRIP_DISTANCE));
			String item_speed = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_AVERAGE_SPEED));
			String item_start = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_START_TIME));
			String item_end = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_END_TIME));
			String item_time = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_TRIP_TIME));

			AlertDialog.Builder myDialog
			= new AlertDialog.Builder(History.this);

			myDialog.setTitle("Delete/Edit?");

			TextView dialogTxt_id = new TextView(History.this);
			LayoutParams dialogTxt_idLayoutParams
			= new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			dialogTxt_id.setLayoutParams(dialogTxt_idLayoutParams);
			dialogTxt_id.setText("#" + String.valueOf(item_id));

			TextView dialogC1_id = new TextView(History.this);
			LayoutParams dialogC1_idLayoutParams
			= new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			dialogC1_id.setLayoutParams(dialogC1_idLayoutParams);
			dialogC1_id.setText(item_date);

			TextView dialogC2_id = new TextView(History.this);
			LayoutParams dialogC2_idLayoutParams
			= new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			dialogC2_id.setLayoutParams(dialogC2_idLayoutParams);
			dialogC2_id.setText(item_distance);

			TextView dialogC3_id = new TextView(History.this);
			LayoutParams dialogC3_idLayoutParams
			= new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			dialogC3_id.setLayoutParams(dialogC3_idLayoutParams);
			dialogC3_id.setText(item_speed);

			TextView dialogC4_id = new TextView(History.this);
			LayoutParams dialogC4_idLayoutParams
			= new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			dialogC4_id.setLayoutParams(dialogC4_idLayoutParams);
			dialogC4_id.setText(item_start);

			TextView dialogC5_id = new TextView(History.this);
			LayoutParams dialogC5_idLayoutParams
			= new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			dialogC5_id.setLayoutParams(dialogC5_idLayoutParams);
			dialogC5_id.setText(item_end);

			TextView dialogC6_id = new TextView(History.this);
			LayoutParams dialogC6_idLayoutParams
			= new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			dialogC6_id.setLayoutParams(dialogC6_idLayoutParams);
			dialogC6_id.setText(item_time);


			final EditText dialogC7_id = new EditText(History.this);
			LayoutParams dialogC7_idLayoutParams
			= new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			dialogC7_id.setLayoutParams(dialogC7_idLayoutParams);
			dialogC7_id.setText(item_content1);

			final EditText dialogC8_id = new EditText(History.this);
			LayoutParams dialogC8_idLayoutParams
			= new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			dialogC8_id.setLayoutParams(dialogC8_idLayoutParams);
			dialogC8_id.setText(item_content2);

			LinearLayout layout = new LinearLayout(History.this);
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.addView(dialogTxt_id);
			layout.addView(dialogC1_id);
			layout.addView(dialogC2_id);
			layout.addView(dialogC3_id);
			layout.addView(dialogC4_id);
			layout.addView(dialogC5_id);
			layout.addView(dialogC6_id);
			layout.addView(dialogC7_id);
			layout.addView(dialogC8_id);
			myDialog.setView(layout);

			myDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
				// do something when the button is clicked
				public void onClick(DialogInterface arg0, int arg1) {
					mySQLiteAdapter.delete_byID(item_id);
					updateList();
				}
			});

			myDialog.setNeutralButton("Update", new DialogInterface.OnClickListener() {
				// do something when the button is clicked
				public void onClick(DialogInterface arg0, int arg1) {
					String value1 = dialogC7_id.getText().toString();
					String value2 = dialogC8_id.getText().toString();
					mySQLiteAdapter.update_byID(item_id, value1, value2);
					updateList();
				}
			});

			myDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				// do something when the button is clicked
				public void onClick(DialogInterface arg0, int arg1) {

				}
			});

			myDialog.show();


		}};

		@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
			mySQLiteAdapter.close();
		}

		private void updateList(){
			cursor.requery();
		}

}
