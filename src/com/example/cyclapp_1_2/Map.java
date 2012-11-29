package com.example.cyclapp_1_2;

import java.util.List;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import android.os.Bundle;

public class Map extends MapActivity {
	private MapView myMap;
	private MapController controller;
	 private PositionOverlay myPositionOverlay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		initMapView();
		initMyLocation();

		// Add the MyPositionOverlay
		myPositionOverlay = new PositionOverlay();
		List<Overlay> overlays = myMap.getOverlays();
		overlays.add(myPositionOverlay);
		myMap.postInvalidate();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// Required by MapActivity
		return false;
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
	}
}
