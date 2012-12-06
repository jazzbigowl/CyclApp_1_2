package com.example.cyclapp_1_2;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class Map extends Navigation {
	private MapView myMap;
	private MapController controller;
	private PositionOverlay myPositionOverlay;

	int time = 0;
	int hours = 0;
	int minutes = 0;
	int seconds = 0;
	protected Handler taskHandler = new Handler();

	double curLat, curLon, oldLat, oldLon;
	double curLocationTime = 0, oldLocationTime = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		Bundle extras = getIntent().getExtras();
		if (extras.getBoolean("setTimer")) {
			time = extras.getInt("myTimer");
			setTimer();
		}
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		initMapView();
		initMyLocation();

		// Add the MyPositionOverlay
		myPositionOverlay = new PositionOverlay();
		List<Overlay> overlays = myMap.getOverlays();
		overlays.add(myPositionOverlay);
		myMap.postInvalidate();
	}


	private void initMapView() {
		myMap = (MapView) findViewById(R.id.map);
		controller = myMap.getController();
		myMap.setSatellite(true);
		myMap.setBuiltInZoomControls(true);

	}

	private void initMyLocation() {

		final MyLocationOverlay overlay = new MyLocationOverlay(this, myMap);
		overlay.enableMyLocation();
		// overlay.enableCompass(); // does not work in emulator
		overlay.runOnFirstFix(new Runnable() {
			public void run() {
				// Zoom in to current location
				controller.setZoom(20);
				controller.animateTo(overlay.getMyLocation());
			}
		});
		myMap.getOverlays().add(overlay);	

		if (oldLocationTime != 0) {
			oldLocationTime = curLocationTime;
			curLocationTime = System.currentTimeMillis();
			oldLat = curLat;
			oldLon = curLon;
			curLat = overlay.getMyLocation().getLatitudeE6()  / 1E6;
			curLon = overlay.getMyLocation().getLongitudeE6() / 1E6;
		}

		double speed = calculateSpeed();
		TextView t = new TextView(this); 
		t = (TextView)findViewById(R.id.MapSpeedText); 
		t.setText(speed + " mph");
	}
	
	private void receiveLocation(Location location) {
		GeoPoint point = new GeoPoint((int)(location.getLatitude() * 1E6), (int)(location.getLongitude() * 1E6));
		controller.animateTo(point);
	}

	private void setTimer() {
		final long elapse = 1000;
		Runnable t = new Runnable() {
			public void run()
			{
				runNextTimedTask();
				taskHandler.postDelayed( this, elapse );
			}
		};
		taskHandler.postDelayed( t, elapse );

	}

	private void runNextTimedTask() {
		// run my task.
		time += 1;		
		TextView t = new TextView(this); 
		t = (TextView)findViewById(R.id.MapTimeText); 
		hours = time / 3600;
		minutes = (time % 3600) / 60;
		seconds = time % 60;
		String timeString = "";
		if (hours < 10) {
			timeString += "0" + hours + ":";
		} else {
			timeString += hours + ":";
		}
		if (minutes < 10) {
			timeString += "0" + minutes + ":";
		} else {
			timeString += minutes + ":";
		}
		if (seconds < 10) {
			timeString += "0" + seconds;
		} else {
			timeString += seconds;
		}
		t.setText(timeString);
	}
	
	
}
