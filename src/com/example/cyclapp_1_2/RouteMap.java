/*
 * Author: Jeremy Bouchat
 * Year: 2013
 * Project Report: https://www.dropbox.com/s/8ba5y8kax3lqhz5/Report.docx
 */
package com.example.cyclapp_1_2;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class RouteMap extends MapActivity {
	private MapView mapView;
	private MapController controller;

	double startLat, startLon, endLat, endLon;
	int key;
	ArrayList<Location> locationsArray = new ArrayList<Location>();
	String stringLocations;
	private SQLiteAdapterReader mySQLiteAdapterReader;
	Cursor cursor;

	//Route stuff
	private Projection projection; 


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.route_map);
		Bundle extras = getIntent().getExtras();

		key = extras.getInt("key");
		
		// initialise database reader
		mySQLiteAdapterReader = new SQLiteAdapterReader(this);
		mySQLiteAdapterReader.openToRead();

		cursor = mySQLiteAdapterReader.queueOne(key);
		cursor.moveToFirst();

		// get needed location variables
		startLat = Double.parseDouble(cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_START_LAT)));
		startLon = Double.parseDouble(cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_START_LON)));
		endLat = Double.parseDouble(cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_END_LAT)));
		endLon = Double.parseDouble(cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_END_LON)));
		stringLocations = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_LOCATIONS));
		locationsArray = splitLocations(stringLocations);

		//initiate map according to user preferences
		mapView = (MapView) findViewById(R.id.routeMap);
		controller = mapView.getController();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(RouteMap.this);
		String listPrefs = prefs.getString("listMapPref", "Please select a measurement in settings.");
		if (listPrefs.equals("Terrain is selected")) {
			mapView.setSatellite(false);
		} else if (listPrefs.equals("Satelite is selected")) {
			mapView.setSatellite(true);
		}
		mapView.setBuiltInZoomControls(true);

		// draw green and red flag
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawableStart = this.getResources().getDrawable(R.drawable.start);
		Drawable drawableEnd = this.getResources().getDrawable(R.drawable.end);
		HelloItemizedOverlay itemizedoverlayStart = new HelloItemizedOverlay(drawableStart, this);
		HelloItemizedOverlay itemizedoverlayEnd = new HelloItemizedOverlay(drawableEnd, this);
		GeoPoint pointStart = new GeoPoint((int)(startLat* 1e6),(int)(startLon* 1e6));
		OverlayItem overlayitem = new OverlayItem(pointStart, "Start", Double.toString(startLat) + " " + Double.toString(startLon));
		GeoPoint pointEnd = new GeoPoint((int)(endLat* 1e6),(int)(endLon* 1e6));
		OverlayItem overlayitem2 = new OverlayItem(pointEnd, "End", Double.toString(endLat) + " " + Double.toString(endLon));
		itemizedoverlayStart.addOverlay(overlayitem);
		itemizedoverlayEnd.addOverlay(overlayitem2);
		mapOverlays.add(itemizedoverlayStart);
		mapOverlays.add(itemizedoverlayEnd);

		mapOverlays = mapView.getOverlays();        
		projection = mapView.getProjection();
		mapOverlays.add(new MyOverlay(locationsArray)); 	

		Integer newStartLat = (int) (startLat * 1E6);
		Integer newEndLat = (int) (endLat * 1E6);
		Integer newStartLon = (int) (startLon * 1E6);
		Integer newEndLon = (int) (endLon * 1E6);

		// focus map on route
		controller.zoomToSpan(Math.abs(newStartLat - newEndLat), Math.abs(newStartLon - newEndLon));
		controller.animateTo(new GeoPoint(((int)((startLat + endLat) * 1E6))/2, ((int)((startLon + endLon) * 1E6))/2 )); 
	}





	private ArrayList<Location> splitLocations(String source) { // split location string, and store into arraylist
		ArrayList<Location> locs = new ArrayList<Location>();
		Pattern p = Pattern.compile("\\b([0-9]+\\.[0-9]+)|(-[0-9]+\\.[0-9]+)\\b");
		Matcher m = p.matcher(source);
		List<String> result = new ArrayList<String>();
		while (m.find()) {
			result.add(m.group());
		}
		for (int i = 0; i < result.size() - 1; i = i + 2) {
			Location loc = new Location("nothing");
			loc.setLatitude(Double.parseDouble(result.get(i)));
			loc.setLongitude(Double.parseDouble(result.get(i + 1)));
			locs.add(loc);
		}
		return locs;
	}


	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}



	class MyOverlay extends Overlay{ //overlay class for displaying route
		ArrayList<Location> theseLocations;
		public MyOverlay(ArrayList<Location> locs){
			theseLocations = locs;
		}   

		public void draw(Canvas canvas, MapView mapv, boolean shadow){
			super.draw(canvas, mapv, shadow);

			Paint   mPaint = new Paint();
			mPaint.setDither(true);
			mPaint.setColor(Color.RED);
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			mPaint.setStrokeJoin(Paint.Join.ROUND);
			mPaint.setStrokeCap(Paint.Cap.ROUND);
			mPaint.setStrokeWidth(2);

			// draw each little red line on map
			for (int i = 0; i < theseLocations.size() - 1; i++) {
				GeoPoint gP1 = new GeoPoint((int) (theseLocations.get(i).getLatitude()* 1e6), (int) (theseLocations.get(i).getLongitude()* 1e6));
				GeoPoint gP2 = new GeoPoint((int) (theseLocations.get(i + 1).getLatitude()* 1e6), (int) (theseLocations.get(i + 1).getLongitude()* 1e6));
				Point p1 = new Point();
				Point p2 = new Point();
				Path path = new Path();
				projection.toPixels(gP1, p1);
				projection.toPixels(gP2, p2);
				path.moveTo(p2.x, p2.y);
				path.lineTo(p1.x,p1.y);

				canvas.drawPath(path, mPaint);
			}
		}
	}
}
