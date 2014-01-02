package ch.hearc.profitmap.gui.training.fragments.live;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import android.annotation.SuppressLint;
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

public class LiveMapFragment extends MapFragment implements com.google.android.gms.location.LocationListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener
{

	
	List<Location> waypoints = new LinkedList<Location>();

	LocationManager lm = null;

	private LocationClient mLocationClient;
	private LocationRequest mLocationRequest;
	
	private RealLocationListener realLocationListener;

	private FakeLocationListener fakeLocationListener;
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		//lm.removeUpdates(realLocationListener);
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
        mLocationRequest.setFastestInterval(5000);

        // Get an editor

        /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        mLocationClient = new LocationClient(parentActivity, this, this);
        
        mLocationClient.connect();

	}
	@Override
	public void onResume() {
		Log.i("LiveMapFragment", "onRes before");

		super.onResume();
		setupFakeGPS();
		//setupGPS();
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

	private void setupFakeGPS()
	{
		lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		fakeLocationListener = new FakeLocationListener();
		if (lm.getProvider("Test") == null)
		{
			lm.addTestProvider("Test", false, false, false, false, false, false, false, 0, 1);
		}
		lm.setTestProviderEnabled("Test", true);
		lm.requestLocationUpdates("Test", 0, 0, fakeLocationListener);

		
		//new Thread(new Runnable() { @Override public void run() { clickMapSleep(lm); } }).start();
		 

		mapElements.map.setOnMapClickListener(new GoogleMap.OnMapClickListener()
		{
			@SuppressLint("NewApi")
			@Override
			public void onMapClick(LatLng l)
			{
				Location loc = new Location("Test");
				loc.setLatitude(l.latitude);
				loc.setLongitude(l.longitude);
				loc.setAltitude(0);
				loc.setAccuracy(1);
				loc.setTime(System.currentTimeMillis());
				loc.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
				lm.setTestProviderLocation("Test", loc);
			}
		});
	}

	public static LatLng randomLatLng()
	{

		float minX = 0.0f;
		float maxX = 40.0f;

		Random rand = new Random();

		float finalX = rand.nextFloat() * (maxX - minX) + minX;
		float finalY = rand.nextFloat() * (maxX - minX) + minX;

		return new LatLng(finalX, finalY);
	}
	
	private class FakeLocationListener implements LocationListener
	{
		@Override
		public void onLocationChanged(Location location)
		{

			if (!((LiveTrainingActivity) parentActivity).isPaused)
				mapElements.addPointAndRefreshPolyline(new LatLng(location.getLatitude(), location.getLongitude()));
				trackInstance.addWaypoint(location);
				Log.i("Stats", trackInstance.getStatistics().toString());
				((LiveTrainingActivity) parentActivity).refreshStatsPanel();
				((LiveTrainingActivity) parentActivity).addDataToGraph();
		}

		@Override
		public void onProviderDisabled(String provider)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
			// TODO Auto-generated method stub

		}
	}



	@SuppressLint("NewApi")
	public void fakeMapClick(LatLng l, LocationManager lm)
	{
		Log.i("fake click at", l.latitude + " ; " + l.longitude);

		Location loc = new Location("Test");
		loc.setLatitude(l.latitude);
		loc.setLongitude(l.longitude);
		loc.setAltitude(0);
		loc.setAccuracy(1);
		loc.setTime(System.currentTimeMillis());
		loc.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
		lm.setTestProviderLocation("Test", loc);
	}

	public void clickMapSleep(LocationManager lm)
	{
		int i = 0;
		while (i < 10)
		{
			SystemClock.sleep(4000);
			fakeMapClick(randomLatLng(), lm);
			i++;
		}
	}
	private void setupGPS()
	{// Acquire a reference to the system Location
		// Manager
		LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		realLocationListener = new RealLocationListener();

		// Register the listener with the Location Manager to receive location
		// updates
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, realLocationListener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, realLocationListener);
	}

	private class RealLocationListener implements LocationListener
	{
		@Override
		public void onLocationChanged(Location location)
		{

			if (!((LiveTrainingActivity) parentActivity).isPaused && trackInstance != null) {
				mapElements.addPointAndRefreshPolyline(new LatLng(location.getLatitude(), location.getLongitude()));
				trackInstance.addWaypoint(location);
				((LiveTrainingActivity) parentActivity).refreshStatsPanel();
			}
			mapElements.start(new LatLng(location.getLatitude(), location.getLongitude()));

		}

		@Override
		public void onProviderDisabled(String provider)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
			// TODO Auto-generated method stub

		}
	}



	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
        mLocationClient.requestLocationUpdates(mLocationRequest, (com.google.android.gms.location.LocationListener) this);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(Location location) {

		Log.i("locFus", location.getLatitude() + " : " + location.getLongitude());
		if (!((LiveTrainingActivity) parentActivity).isPaused && trackInstance != null) {
			mapElements.addPointAndRefreshPolyline(new LatLng(location.getLatitude(), location.getLongitude()));
			trackInstance.addWaypoint(location);
			((LiveTrainingActivity) parentActivity).refreshStatsPanel();
			((LiveTrainingActivity) parentActivity).addDataToGraph();
		}
		mapElements.start(new LatLng(location.getLatitude(), location.getLongitude()));

	}

		
}
