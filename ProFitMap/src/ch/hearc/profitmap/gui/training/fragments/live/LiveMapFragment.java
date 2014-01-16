package ch.hearc.profitmap.gui.training.fragments.live;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import ch.hearc.profitmap.gui.training.LiveTrainingActivity;
import ch.hearc.profitmap.gui.training.fragments.MapFragment;
import ch.hearc.profitmap.model.GeoImage;
import ch.hearc.profitmap.model.TrackInstance;
import ch.hearc.profitmap.model.Tracks;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class LiveMapFragment extends MapFragment implements
		com.google.android.gms.location.LocationListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	LiveTrainingActivity parentActivity;

	List<Location> waypoints = new LinkedList<Location>();

	LocationManager lm = null;

	private LocationClient mLocationClient;
	private LocationRequest mLocationRequest;

	private FakeLocationListener fakeLocationListener;

	private TrackInstance ghostTrackInstance;

	private long startTime;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		parentActivity = (LiveTrainingActivity) activity;
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		// lm.removeUpdates(realLocationListener);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i("LMF", "Destroyed");
		lm.removeUpdates(fakeLocationListener);
		mLocationClient.disconnect();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initiateFusedLocation();
	}

	private void initiateFusedLocation() {
		// Create a new global location parameters object
		mLocationRequest = LocationRequest.create();

		/*
		 * Set the update interval
		 */
		mLocationRequest.setInterval(2000);

		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		// Set the interval ceiling to one minute
		// mLocationRequest.setFastestInterval(5000);

		// Get an editor

		/*
		 * Create a new location client, using the enclosing class to handle
		 * callbacks.
		 */
		mLocationClient = new LocationClient(parentActivity, this, this);

		mLocationClient.connect();

	}

	public void endTraining() {
		Location l = getLastKnownLocation();
		LatLng endPosition = new LatLng(l.getLatitude(), l.getLongitude());
		mapElements.end(endPosition);
	}
	@Override
	public void onResume() {
		Log.i("LiveMapFragment", "onRes before");

		super.onResume();

		if (parentActivity != null) {
			if (parentActivity.getHasGhost()) {

				ghostTrackInstance = Tracks
						.getInstance(parentActivity.getSportId())
						.getTrack(parentActivity.getTrackId())
						.getTrackInstance(
								parentActivity.getGhostTrackInstanceId());

				drawGhostTrack();
			} else {
				// No ghost for this livetrack
			}
		} else
			Log.e("LMF", "Fatal : no parent activity");
		setupFakeGPS();


	}

	private void drawGhostTrack() {
		Log.i("LMF", "Drawing ghost track");
		if (ghostTrackInstance != null) {
			if (ghostTrackInstance.getWaypoints().size() != 0) {
				for (Location l : ghostTrackInstance.getWaypoints()) {
					mapElements.ghostAddPointAndRefreshPolyline(new LatLng(l
							.getLatitude(), l.getLongitude()));
				}
				Location l = ghostTrackInstance.getWaypoints().get(
						ghostTrackInstance.getWaypoints().size() - 1);
				mapElements.ghostEnd(new LatLng(l.getLatitude(), l
						.getLongitude()));
			} else
				Log.i("LMF", "ghost track has 0 locs");
		} else
			Log.i("LMF", "ghost track nulls");
	}

	@Override
	protected void addWaypoints() {
		Log.i("LMF", "addW");
		if (trackInstance != null) {
			Log.i(this.getClass().getSimpleName(),
					"addWaypoints:trackInstance not null");
			if (trackInstance.getWaypoints().size() != 0) {
				for (Location l : trackInstance.getWaypoints()) {
					Log.i("mapF", "Adding waypoint");
					mapElements.start(new LatLng(l.getLatitude(), l
							.getLongitude()));
					mapElements.addPointAndRefreshPolyline(new LatLng(l
							.getLatitude(), l.getLongitude()));
				}
			}
			
			
			for (GeoImage geoImage : trackInstance.getImages()) {
				mapElements.addPictureMarker(geoImage.getLocation(), geoImage.getImagePath());
			}
		}
	};

	private void setupFakeGPS() {
		lm = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		fakeLocationListener = new FakeLocationListener();
		if (lm.getProvider("Test") == null) {
			lm.addTestProvider("Test", false, false, false, false, false,
					false, false, 0, 1);
		}
		lm.setTestProviderEnabled("Test", true);
		lm.requestLocationUpdates("Test", 0, 0, fakeLocationListener);

		// new Thread(new Runnable() { @Override public void run() {
		// clickMapSleep(lm); } }).start();

		mapElements.map
				.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
					@SuppressLint("NewApi")
					@Override
					public void onMapClick(LatLng l) {
						Location loc = new Location("Test");
						loc.setLatitude(l.latitude);
						loc.setLongitude(l.longitude);
						loc.setAltitude(getRndDb());
						loc.setSpeed((float) getRndDb());
						loc.setAccuracy(1);
						loc.setTime(System.currentTimeMillis());
						loc.setElapsedRealtimeNanos(SystemClock
								.elapsedRealtimeNanos());
						lm.setTestProviderLocation("Test", loc);
					}
				});
	}

	private class FakeLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {

			if (!((LiveTrainingActivity) parentActivity).isPaused)
				mapElements.addPointAndRefreshPolyline(new LatLng(location
						.getLatitude(), location.getLongitude()));
			trackInstance.addWaypoint(location);

			((LiveTrainingActivity) parentActivity).refreshStatsPanel();
			((LiveTrainingActivity) parentActivity).refreshGraphPanel();

			// lastLocation = location;

			mapElements.drawCurrentPositionIndicator(location);

			if (parentActivity.getHasGhost())
				computeGhost(location);

			mapElements.map
					.animateCamera(CameraUpdateFactory.newLatLngZoom(
							new LatLng(location.getLatitude(), location
									.getLongitude()),
							mapElements.map.getCameraPosition().zoom >= 18 ? mapElements.map
									.getCameraPosition().zoom : 18));

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}
	}

	public double getRndDb() {
		return Math.ceil(new Random().nextDouble() * 100 + 100);
	}

	public void computeGhost(Location currentLocation) {

		// How long has it been since the very first location ?
		long timeSinceStart = currentLocation.getTime() - startTime;

		// CLOSEST GHOST; BY TIME
		// Where was the ghost, at this point in time, relative to our time
		// since start

		// When did the ghost start ? (UNIX timestamp)
		long ghostStartTime = ghostTrackInstance.getWaypoints().get(0)
				.getTime();

		long closestTime = (long) Double.POSITIVE_INFINITY;
		Location closestGhostLocationByTime = null;
		int closestGhostLocationByTimeIndex = 0;

		for (Location l : ghostTrackInstance.getWaypoints()) {
			long relTime = l.getTime() - ghostStartTime;
			long diff = Math.abs(relTime - timeSinceStart);
			if (diff < closestTime) {
				closestTime = diff;
				closestGhostLocationByTime = l;
				closestGhostLocationByTimeIndex = ghostTrackInstance
						.getWaypoints().indexOf(closestGhostLocationByTime);
			}
		}

		// CLOSEST GHOST; BY DISTANCE
		// Which is the location that is closer to the ghost, in terms of
		// distance.
		double closestDistance = Double.POSITIVE_INFINITY;
		Location closestGhostLocationByDistance = null;
		int closestGhostLocationByDistanceIndex = 0;

		for (Location l : ghostTrackInstance.getWaypoints()) {
			float distance = distanceBetween(currentLocation, l);

			if (distance < closestDistance) {
				closestDistance = distance;
				closestGhostLocationByDistance = l;
				closestGhostLocationByDistanceIndex = ghostTrackInstance
						.getWaypoints().indexOf(closestGhostLocationByDistance);
			}
		}

		// What is the total distance to the ghost, from our location to the
		// ghost's current location,
		// following the ghost's track

		float totalDistanceToGhost = 0;
		for (int i = closestGhostLocationByDistanceIndex; i < closestGhostLocationByTimeIndex; i++) {
			float distanceToGhostArray = distanceBetween(currentLocation,
					ghostTrackInstance.getWaypoints().get(i));
			float distance = distanceToGhostArray;
			totalDistanceToGhost += distance;
		}
		
		boolean isAhead = false;

		// Initializing the next location from closest ghost(by time) to the
		// location of the ghost(by time)
		Location nextLocationFromClosest = closestGhostLocationByTime;

		// Which is the location after the one closest to the ghost(by time) ?
		if (!(closestGhostLocationByTimeIndex + 1 >= ghostTrackInstance
				.getWaypoints().size() - 1)) {
			nextLocationFromClosest = ghostTrackInstance.getWaypoints().get(
					closestGhostLocationByTimeIndex + 1);
		}

		// Computing the distance between :
		// 1. Our current location and the location after the ghost(by time)
		float distanceBetweenCurrentAndNextAfterClosest = distanceBetween(
				currentLocation, nextLocationFromClosest);
		// 2. The location of the ghost(by time) and the location after that
		float distanceBetweenClosestAndNextAfterClosest = distanceBetween(
				closestGhostLocationByTime, nextLocationFromClosest);

		// If we are closer to the location after the ghost(by time), we are
		// ahead. If not, we're behind.
		isAhead = (distanceBetweenClosestAndNextAfterClosest > distanceBetweenCurrentAndNextAfterClosest) ? true
				: false;

		// Drawing the line between us and the ghost (passing isAhead to know
		// the color of the line)
		
		  if (closestGhostLocationByTime != null) {
		  mapElements.drawPolylineDifferenceWithGhost(currentLocation,
		  closestGhostLocationByTime, isAhead); }
		 

		// TODO : Draw the lines between us and the ghost (but following the
		// track)
		/*if (closestGhostLocationByDistanceIndex < closestGhostLocationByTimeIndex) {
			for (int i = closestGhostLocationByDistanceIndex; i < closestGhostLocationByTimeIndex - 1; i++) {
				Location start = ghostTrackInstance.getWaypoints().get(i);
				Location end = ghostTrackInstance.getWaypoints().get(i+1);
				mapElements.drawPolylineDifferenceWithGhost(start,
						end, isAhead);
			}
		} else {
			for (int i = closestGhostLocationByDistanceIndex; i > closestGhostLocationByTimeIndex +	 1; i--) {
				Location start = ghostTrackInstance.getWaypoints().get(i);
				Location end = ghostTrackInstance.getWaypoints().get(i-1);
				mapElements.drawPolylineDifferenceWithGhost(start,
						end, isAhead);
			}
		}*/
	}

	private float distanceBetween(Location start, Location end) {
		float distanceToGhostArray[] = new float[3];
		Location.distanceBetween(start.getLatitude(), start.getLongitude(),
				end.getLatitude(), end.getLongitude(), distanceToGhostArray);
		return distanceToGhostArray[0];
	}


	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mLocationClient.requestLocationUpdates(mLocationRequest,
				(com.google.android.gms.location.LocationListener) this);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location location) {

		Log.i("locFus",
				location.getLatitude() + " : " + location.getLongitude());
		if (!((LiveTrainingActivity) parentActivity).isPaused
				&& trackInstance != null) {
			mapElements.addPointAndRefreshPolyline(new LatLng(location
					.getLatitude(), location.getLongitude()));
			trackInstance.addWaypoint(location);
			System.out.println(location.getAltitude());
			((LiveTrainingActivity) parentActivity).refreshStatsPanel();
			((LiveTrainingActivity) parentActivity).refreshGraphPanel();
		}

		if (mapElements.start(new LatLng(location.getLatitude(), location
				.getLongitude()))) {
			startTime = location.getTime();
		}

		mapElements.drawCurrentPositionIndicator(location);

		if (parentActivity.getHasGhost())
			computeGhost(location);

		mapElements.map
				.animateCamera(CameraUpdateFactory.newLatLngZoom(
						new LatLng(location.getLatitude(), location
								.getLongitude()),
						mapElements.map.getCameraPosition().zoom >= 18 ? mapElements.map
								.getCameraPosition().zoom : 18));
	}

	/*@Override
	public boolean addPicMarkerToLocation(Location loc, String dropBoxPath) {
		if (mapElements != null) {
			Log.i("LMF", "add");
			MarkerOptions mo = new MarkerOptions().position(new LatLng(loc
					.getLatitude(), loc.getLongitude()));
			mo.title(dropBoxPath);

			mo.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.ic_action_photo));

			// mapElements.map.addMarker(mo);
			// mapElements.moList.add(mo);
			mapElements.addPictureMarker(loc, dropBoxPath);
			return true;
		} else
			return false;
	}*/

	public Location getLastKnownLocation()
	{
		return mLocationClient.getLastLocation();
	}
	
}
