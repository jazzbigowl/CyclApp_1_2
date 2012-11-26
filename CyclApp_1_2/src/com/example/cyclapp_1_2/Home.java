package com.example.cyclapp_1_2;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class Home extends Activity implements OnClickListener {


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		// Listeners for Buttons
		View satNat = findViewById(R.id.sat_nav_button);
		satNat.setOnClickListener(Home.this);
		View pastRides = findViewById(R.id.view_past_rides_button);
		pastRides.setOnClickListener(Home.this);
		View about = findViewById(R.id.about_button);
		about.setOnClickListener(Home.this);
		View exit = findViewById(R.id.exit_button);
		exit.setOnClickListener(Home.this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sat_nav_button:
			Intent st = new Intent(this, Navigation.class);
			startActivity(st);
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


