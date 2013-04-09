package com.example.cyclapp_1_2;

import java.util.List;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.WindowManager;

public class Map extends MapActivity {
	private MapView myMap;
	private MapController controller;
	private PositionOverlay myPositionOverlay;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

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
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Map.this);
		String listPrefs = prefs.getString("listMapPref", "Please select a measurement in settings.");
		if (listPrefs.equals("Terrain is selected")) {
			myMap.setSatellite(false);
		} else if (listPrefs.equals("Satelite is selected")) {
			myMap.setSatellite(true);
		}
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
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
}
