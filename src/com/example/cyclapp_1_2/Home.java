package com.example.cyclapp_1_2;

import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Home extends Activity implements OnClickListener {


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);


		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Home.this);
		String namePref = prefs.getString("name", "");
		if (namePref != "") {
			TextView welcomeBanner;
//			welcomeBanner = (TextView)findViewById(R.id.home_title);
//			welcomeBanner.setText("Welcome " + namePref);
		}

		// Listeners for Buttons
		View satNat = findViewById(R.id.sat_nav_button);
		satNat.getBackground().setColorFilter(new LightingColorFilter(Color.GRAY, Color.GRAY));
		satNat.setOnClickListener(Home.this);
		View pastRides = findViewById(R.id.view_past_rides_button);
		pastRides.getBackground().setColorFilter(new LightingColorFilter(Color.DKGRAY, Color.GRAY));
		pastRides.setOnClickListener(Home.this);
		View about = findViewById(R.id.about_button);
		about.getBackground().setColorFilter(new LightingColorFilter(Color.GRAY, Color.DKGRAY));
		about.setOnClickListener(Home.this);
		View exit = findViewById(R.id.exit_button);
		exit.getBackground().setColorFilter(new LightingColorFilter(Color.DKGRAY, Color.DKGRAY));
		exit.setOnClickListener(Home.this);
//		View test = findViewById(R.id.test_button);
//		test.setOnClickListener(Home.this);

	}
	

	public void onClick(View v) {
		switch (v.getId()) {
//		case R.id.test_button:
//			Intent t = new Intent(this, Test.class);
//			startActivity(t);
//			break;
		case R.id.sat_nav_button:
			LocationManager alm = (LocationManager)this.getSystemService( Context.LOCATION_SERVICE );
			if( alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER ) ) {
				//GPS Enabled
				Intent st = new Intent(this, Navigation.class);
				startActivity(st);
			} else {
				AlertDialog.Builder myDialog
				= new AlertDialog.Builder(Home.this);

				myDialog.setTitle("Warning!");

				TextView dialogTxt_id = new TextView(Home.this);
				LayoutParams dialogTxt_idLayoutParams
				= new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				dialogTxt_id.setLayoutParams(dialogTxt_idLayoutParams);
				dialogTxt_id.setText(String.valueOf(" You must enable GPS on your device."));


				//				final EditText dialogName_id = new EditText(Home.this);
				//				@SuppressWarnings("deprecation")
				//				LayoutParams dialogName_idLayoutParams
				//				= new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				//				dialogName_id.setLayoutParams(dialogName_idLayoutParams);


				LinearLayout layout = new LinearLayout(Home.this);
				layout.setOrientation(LinearLayout.VERTICAL);
				//		layout.addView(dialogTxt_id);

				layout.addView(dialogTxt_id);
				myDialog.setView(layout);

				myDialog.setNegativeButton("Settings", new DialogInterface.OnClickListener() {
					// do something when the button is clicked
					public void onClick(DialogInterface arg0, int arg1) {
						Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(intent);
					}
				});
				myDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
					// do something when the button is clicked
					public void onClick(DialogInterface arg0, int arg1) {

					}
				});

				myDialog.show();

			}

			//			Intent st = new Intent(this, Navigation.class);
			//			startActivity(st);
			break;
		case R.id.view_past_rides_button:
			Intent h = new Intent(this, History.class);
			startActivity(h);
			break;
		case R.id.about_button:
			Intent a = new Intent(this, About.class);
			startActivity(a);
			break;
		case R.id.exit_button:
			new AlertDialog.Builder(this)
			.setMessage("Are you sure you want to exit?")
			.setCancelable(false)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					Home.this.finish();
				}
			})
			.setNegativeButton("No", null)
			.show();
			break;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivity(new Intent(Home.this, Preferences.class));
			return true;
		case R.id.exit:
			new AlertDialog.Builder(this)
			.setMessage("Are you sure you want to exit?")
			.setCancelable(false)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					//CustomTabActivity.this.finish();
					Home.this.finish();
				}
			})
			.setNegativeButton("No", null)
			.show();
			//this.finish();
			return true;
		}
		return false;
	}
}


