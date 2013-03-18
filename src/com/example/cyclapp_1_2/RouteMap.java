package com.example.cyclapp_1_2;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;

public class RouteMap extends MapActivity {
	private MapView mapView;
	private MapController controller;
	private PositionOverlay myPositionOverlay;

	double startLat, startLon, endLat, endLon;
	int key;
	ArrayList<Location> locationsArray = new ArrayList<Location>();
	String stringLocations;
	private SQLiteAdapterReader mySQLiteAdapterReader;
	Cursor cursor;

	//Route stuff
	private List<Overlay> mapOverlays;
	private Projection projection; 


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.route_map);
		Bundle extras = getIntent().getExtras();

		key = extras.getInt("key");
	//	stringLocations = extras.getString("locations");
		mySQLiteAdapterReader = new SQLiteAdapterReader(this);
		mySQLiteAdapterReader.openToRead();

		cursor = mySQLiteAdapterReader.queueOne(key);
		cursor.moveToFirst();

		startLat = Double.parseDouble(cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_START_LAT)));
		startLon = Double.parseDouble(cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_START_LON)));
		endLat = Double.parseDouble(cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_END_LAT)));
		endLon = Double.parseDouble(cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_END_LON)));
		stringLocations = cursor.getString(cursor.getColumnIndex(SQLiteAdapter.KEY_LOCATIONS));
		locationsArray = splitLocations(stringLocations);

		initMapView();

		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.start);
		HelloItemizedOverlay itemizedoverlay = new HelloItemizedOverlay(drawable, this);

		GeoPoint point = new GeoPoint((int)(startLat* 1e6),(int)(startLon* 1e6));
		OverlayItem overlayitem = new OverlayItem(point, "Start", Double.toString(startLat) + " " + Double.toString(startLon));

		GeoPoint point2 = new GeoPoint((int)(endLat* 1e6),(int)(endLon* 1e6));
		OverlayItem overlayitem2 = new OverlayItem(point2, "End", Double.toString(endLat) + " " + Double.toString(endLon));

		itemizedoverlay.addOverlay(overlayitem);
		itemizedoverlay.addOverlay(overlayitem2);
		mapOverlays.add(itemizedoverlay);

		
		
		mapOverlays = mapView.getOverlays();        
		projection = mapView.getProjection();
		mapOverlays.add(new MyOverlay(locationsArray)); 
//		mapOverlays.add(new MyOverlay(locationsArray.get(1), locationsArray.get(2)));

		//Route Stuff
//		for (int i = 0; i < locationsArray.size() -2; i++) {
//			mapOverlays = mapView.getOverlays();        
//			projection = mapView.getProjection();
//			mapOverlays.add(new MyOverlay(locationsArray.get(i), locationsArray.get(i + 1))); 
			
//		}
	}


	private void initMapView() {
		mapView = (MapView) findViewById(R.id.routeMap);
		controller = mapView.getController();
		mapView.setSatellite(false);
		mapView.setBuiltInZoomControls(true);

	}

	public ArrayList<Location> splitLocations(String source) {
		ArrayList<Location> locs = new ArrayList<Location>();
		Pattern p = Pattern.compile("\\b([0-9]+\\.[0-9]+)|(-[0-9]+\\.[0-9]+)\\b");
//		Pattern p = Pattern.compile("\\b(.+)\\,\\b");
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



	class MyOverlay extends Overlay{
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

//			GeoPoint gP1 = new GeoPoint((int) (from.getLatitude()* 1e6), (int) (from.getLongitude()* 1e6));
//			GeoPoint gP2 = new GeoPoint((int) (to.getLatitude()* 1e6), (int) (to.getLongitude()* 1e6));

			
//			GeoPoint gP1 = new GeoPoint((int) (theseLocations.get(0).getLatitude()* 1e6), (int) (theseLocations.get(0).getLongitude()* 1e6));
//			GeoPoint gP2 = new GeoPoint((int) (theseLocations.get(theseLocations.size() - 1).getLatitude()* 1e6), (int) (theseLocations.get(theseLocations.size() - 1).getLongitude()* 1e6));
//			
//			Point p1 = new Point();
//			Point p2 = new Point();
//			Path path = new Path();
//
//			projection.toPixels(gP1, p1);
//			projection.toPixels(gP2, p2);
//
//			path.moveTo(p2.x, p2.y);
//			path.lineTo(p1.x,p1.y);
//
//			canvas.drawPath(path, mPaint);
			
			
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
