package com.example.cyclapp_1_2;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

public class LocationDevice  extends Activity {

	Location curLocation, oldLocation, startLocation, endLocation;
	double curLocationTime = 0, oldLocationTime = 0, startLocationTime = 0, endLocationTime = 0; // in milliseconds
	ArrayList<Location> Locations = new ArrayList<Location>();
	
	double speed = 0;
	ArrayList<Double> Speeds = new ArrayList<Double>();
	
	protected Handler taskHandler = new Handler();
	protected Boolean isRunning = false; 
	protected Boolean isPaused = false;




	public LocationDevice(Navigation nav) {

		LocationManager locationManager;
		String svcName = Context.LOCATION_SERVICE;
		locationManager = (LocationManager)getSystemService(svcName);

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setSpeedRequired(false);
		criteria.setCostAllowed(true);
		String provider = locationManager.getBestProvider(criteria, true);

		Location curLocation = locationManager.getLastKnownLocation(provider);

		updateWithNewLocation(curLocation);

		locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
	}

	
	private void updateWithNewLocation(Location location) {
		if (location != null) {
			oldLocationTime = curLocationTime;
			curLocationTime = System.currentTimeMillis();
			oldLocation = curLocation;
			curLocation = location;
			if ((isRunning) && (!isPaused)) {
				Locations.add(location);
			}
		}
		speed = calculateSpeed();
	}


	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}
		public void onProviderDisabled(String provider) {
		}
		public void onProviderEnabled(String provider) {
		}
		public void onStatusChanged(String provider, int status, 
				Bundle extras) {
		}
	};

	
	
	
	


	// Method for calculating Distance
	private double calculateDistance(double oldLat, double oldLon, double curLat, double curLon) {
		// Using Pythagoras
		double lat = 69.1 * Math.abs(curLat - oldLat);
		double lon = 69.1 * Math.abs(curLon - oldLon) * Math.cos(curLat/57.3);
		double distance = Math.sqrt(Math.pow(lat, 2) + Math.pow(lon, 2));
		return distance;


		// Method for calculating Distance using Haverside
		// returns distance in miles
		// http://introcs.cs.princeton.edu/java/12types/GreatCircle.java.html
		//		double a = Math.pow(Math.sin((oldLat - curLat) / 2), 2) + Math.cos(curLat) * Math.cos(oldLat) * Math.pow(Math.sin((oldLon - curLon)/2), 2);
		//		// great circle distance in radians
		//		double angle2 = 2 * Math.asin(Math.min(1, Math.sqrt(a)));
		//		// convert back to degrees
		//		angle2 = Math.toDegrees(angle2);
		//		// each degree on a great circle of Earth is 60 nautical miles
		//		double distance2 = 60 * angle2;
		//		return distance2;
	}

	// Method for calculating speed
	public double calculateSpeed() {
		if (oldLocationTime == 0) {
			return 0;
		} else {
			// TimeDiff in hours
			double TimeDiff   = ((curLocationTime - oldLocationTime) / (1000*60*60)); //hours
			double Distance = calculateDistance(oldLocation.getLatitude(), oldLocation.getLongitude(), curLocation.getLatitude(), curLocation.getLongitude());
			double speed = (Distance / TimeDiff);
			return speed;
		}
	}

	/**
	 * @return the curLocation
	 */
	public Location getCurLocation() {
		return curLocation;
	}

	/**
	 * @param curLocation the curLocation to set
	 */
	public void setCurLocation(Location curLocation) {
		this.curLocation = curLocation;
	}

	/**
	 * @return the oldLocation
	 */
	public Location getOldLocation() {
		return oldLocation;
	}

	/**
	 * @param oldLocation the oldLocation to set
	 */
	public void setOldLocation(Location oldLocation) {
		this.oldLocation = oldLocation;
	}

	/**
	 * @return the startLocation
	 */
	public Location getStartLocation() {
		return startLocation;
	}

	/**
	 * @param startLocation the startLocation to set
	 */
	public void setStartLocation(Location startLocation) {
		this.startLocation = startLocation;
	}

	/**
	 * @return the endLocation
	 */
	public Location getEndLocation() {
		return endLocation;
	}

	/**
	 * @param endLocation the endLocation to set
	 */
	public void setEndLocation(Location endLocation) {
		this.endLocation = endLocation;
	}

	/**
	 * @return the curLocationTime
	 */
	public double getCurLocationTime() {
		return curLocationTime;
	}

	/**
	 * @param curLocationTime the curLocationTime to set
	 */
	public void setCurLocationTime(double curLocationTime) {
		this.curLocationTime = curLocationTime;
	}

	/**
	 * @return the oldLocationTime
	 */
	public double getOldLocationTime() {
		return oldLocationTime;
	}

	/**
	 * @param oldLocationTime the oldLocationTime to set
	 */
	public void setOldLocationTime(double oldLocationTime) {
		this.oldLocationTime = oldLocationTime;
	}

	/**
	 * @return the startLocationTime
	 */
	public double getStartLocationTime() {
		return startLocationTime;
	}

	/**
	 * @param startLocationTime the startLocationTime to set
	 */
	public void setStartLocationTime(double startLocationTime) {
		this.startLocationTime = startLocationTime;
	}

	/**
	 * @return the endLocationTime
	 */
	public double getEndLocationTime() {
		return endLocationTime;
	}

	/**
	 * @param endLocationTime the endLocationTime to set
	 */
	public void setEndLocationTime(double endLocationTime) {
		this.endLocationTime = endLocationTime;
	}

	/**
	 * @return the locations
	 */
	public ArrayList<Location> getLocations() {
		return Locations;
	}

	/**
	 * @param locations the locations to set
	 */
	public void setLocations(ArrayList<Location> locations) {
		Locations = locations;
	}

	/**
	 * @return the speed
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * @param speed the speed to set
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
	}

	/**
	 * @return the speeds
	 */
	public ArrayList<Double> getSpeeds() {
		return Speeds;
	}

	/**
	 * @param speeds the speeds to set
	 */
	public void setSpeeds(ArrayList<Double> speeds) {
		Speeds = speeds;
	}

	/**
	 * @return the taskHandler
	 */
	public Handler getTaskHandler() {
		return taskHandler;
	}

	/**
	 * @param taskHandler the taskHandler to set
	 */
	public void setTaskHandler(Handler taskHandler) {
		this.taskHandler = taskHandler;
	}

	/**
	 * @return the isRunning
	 */
	public Boolean getIsRunning() {
		return isRunning;
	}

	/**
	 * @param isRunning the isRunning to set
	 */
	public void setIsRunning(Boolean isRunning) {
		this.isRunning = isRunning;
	}

	/**
	 * @return the isPaused
	 */
	public Boolean getIsPaused() {
		return isPaused;
	}

	/**
	 * @param isPaused the isPaused to set
	 */
	public void setIsPaused(Boolean isPaused) {
		this.isPaused = isPaused;
	}

	/**
	 * @return the locationListener
	 */
	public LocationListener getLocationListener() {
		return locationListener;
	}
	
	

}
