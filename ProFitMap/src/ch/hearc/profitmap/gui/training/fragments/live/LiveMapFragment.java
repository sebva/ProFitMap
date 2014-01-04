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

	@Override
	public void onResume() {
		Log.i("LiveMapFragment", "onRes before");

		super.onResume();

		if (parentActivity != null) {
			if (parentActivity.getHasGhost()) {
				Log.i("LMF", "has ghost");
				Log.i("LMF", "gtid" + parentActivity.getGhostTrackInstanceId());

				ghostTrackInstance = Tracks
						.getInstance(parentActivity.getSportId())
						.getTrack(parentActivity.getTrackId())
						.getTrackInstance(
								parentActivity.getGhostTrackInstanceId());

				drawGhostTrack();
			} else {
				System.out.println("no ghost");
			}
		} else
			Log.i("LMF", "no parent activity");

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

	public static LatLng randomLatLng() {

		float minX = 0.0f;
		float maxX = 40.0f;

		Random rand = new Random();

		float finalX = rand.nextFloat() * (maxX - minX) + minX;
		float finalY = rand.nextFloat() * (maxX - minX) + minX;

		return new LatLng(finalX, finalY);
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
			.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
					location.getLatitude(), location.getLongitude()), mapElements.map.getCameraPosition().zoom >= 18 ? mapElements.map.getCameraPosition().zoom : 18));

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
		// Location ghostLocation = findClosestGhostLocationByTime();
		long timeSinceStart = currentLocation.getTime() - startTime;

		// move to init of ghost (once)
		long ghostStartTime = ghostTrackInstance.getWaypoints().get(0)
				.getTime();

		// List<Location> waypointTimeList = new ArrayList<Location>();

		Location closestGhostLocation = null;
		int closestGhostLocationIndex = 0;
		long closestTime = (long) Double.POSITIVE_INFINITY;
		for (Location l : ghostTrackInstance.getWaypoints()) {
			long relTime = l.getTime() - ghostStartTime;
			long diff = Math.abs(relTime - timeSinceStart);
			if (diff < closestTime) {
				closestTime = diff;
				closestGhostLocation = l;
				closestGhostLocationIndex = ghostTrackInstance.getWaypoints()
						.indexOf(closestGhostLocation);
			}
		}

		boolean isAhead = false;
		Location nextLocationFromClosest = closestGhostLocation;

		if (closestGhostLocationIndex + 1 >= ghostTrackInstance.getWaypoints()
				.size() - 1) {
		} else {
			nextLocationFromClosest = ghostTrackInstance.getWaypoints().get(
					closestGhostLocationIndex + 1);
		}

		float results[] = new float[3];
		Location.distanceBetween(currentLocation.getLatitude(),
				currentLocation.getLongitude(),
				nextLocationFromClosest.getLatitude(),
				nextLocationFromClosest.getLongitude(), results);

		float distanceBetweenCurrentAndNextAfterClosest = results[0];

		Log.i("Ghosts",
				"distance between curent and next after closest ghost loc : "
						+ distanceBetweenCurrentAndNextAfterClosest);

		results = new float[3];
		Location.distanceBetween(closestGhostLocation.getLatitude(),
				closestGhostLocation.getLongitude(),
				nextLocationFromClosest.getLatitude(),
				nextLocationFromClosest.getLongitude(), results);
		float distanceBetweenClosestAndNextAfterClosest = results[0];

		isAhead = (distanceBetweenClosestAndNextAfterClosest > distanceBetweenCurrentAndNextAfterClosest) ? true
				: false;

		Log.i("Ghosts",
				"distance between closest and next after closest ghost loc : "
						+ distanceBetweenClosestAndNextAfterClosest);

		if (closestGhostLocation != null) {
			// mapElements.addTestMarker(closestGhostLocation);
			mapElements.drawPolylineDifferenceWithGhost(currentLocation,
					closestGhostLocation, isAhead);
		}

		Log.i("LMF", "" + closestTime + "" + closestGhostLocation.getTime());
	}

	@SuppressLint("NewApi")
	public void fakeMapClick(LatLng l, LocationManager lm) {
		Log.i("fake click at", l.latitude + " ; " + l.longitude);

		Location loc = new Location("Test");
		loc.setLatitude(l.latitude);
		loc.setLongitude(l.longitude);
		loc.setAltitude(getRndDb());
		loc.setSpeed((float) getRndDb());
		System.out.println(loc.getAltitude() + " : " + loc.getSpeed());
		loc.setAccuracy(1);
		loc.setTime(System.currentTimeMillis());
		loc.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
		lm.setTestProviderLocation("Test", loc);
	}

	public void clickMapSleep(LocationManager lm) {
		int i = 0;
		while (i < 10) {
			SystemClock.sleep(4000);
			fakeMapClick(randomLatLng(), lm);
			i++;
		}
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
				.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
						location.getLatitude(), location.getLongitude()), mapElements.map.getCameraPosition().zoom >= 18 ? mapElements.map.getCameraPosition().zoom : 18));
	}

}
