package ch.hearc.profitmap;

import java.io.Serializable;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapSectionFragment extends Fragment {

	private SupportMapFragment fragment;

	private MapElements mapElements;

	public MapSectionFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.i("MSF", "onC");

		mapElements = ActiveMapElements.getInstance().getMapElements();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("MSF", "onCreateV");

		return inflater.inflate(R.layout.fragment_live_training_map, container,
				false);
	}

	@Override
	public void onResume() {
		Log.i("MSF", "onRes before");

		if (mapElements != null) {
			if (mapElements.map == null) {
				Log.i("MSF", "onRes inif");
				mapElements.map = fragment.getMap();
				mapElements.m = mapElements.map.addMarker(mapElements.mo);
				setupFakeGPS();
			}
			
			else
			{
				mapElements.map = fragment.getMap();
				mapElements.m = mapElements.map.addMarker(mapElements.mo);
				mapElements.pl = mapElements.map.addPolyline(mapElements.plo);
			}
		}
		super.onResume();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i("MSF", "onActivityC");
		super.onActivityCreated(savedInstanceState);
		FragmentManager fm = getChildFragmentManager();
		fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
		if (fragment == null) {
			fragment = SupportMapFragment.newInstance();
			fm.beginTransaction().replace(R.id.map, fragment).commit();
		}
	}

	public LatLng randomLatLng() {

		float minX = 0.0f;
		float maxX = 40.0f;

		Random rand = new Random();

		float finalX = rand.nextFloat() * (maxX - minX) + minX;
		float finalY = rand.nextFloat() * (maxX - minX) + minX;

		return new LatLng(finalX, finalY);
	}

	@SuppressLint("NewApi")
	public void fakeMapClick(LatLng l, LocationManager lm) {
		Log.i("fake click at", l.latitude + " ; " + l.longitude);

		Location loc = new Location("Test");
		loc.setLatitude(l.latitude);
		loc.setLongitude(l.longitude);
		loc.setAltitude(0);
		loc.setAccuracy(1);
		loc.setTime(System.currentTimeMillis());
		loc.setElapsedRealtimeNanos(1000);
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

	private void setupFakeGPS() {
		final LocationManager lm;
		FakeLocationListener ll;
		lm = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		ll = new FakeLocationListener();
		if (lm.getProvider("Test") == null) {
			lm.addTestProvider("Test", false, false, false, false, false,
					false, false, 0, 1);
		}
		lm.setTestProviderEnabled("Test", true);
		lm.requestLocationUpdates("Test", 0, 0, ll);

		new Thread(new Runnable() {

			@Override
			public void run() {
				clickMapSleep(lm);
			}
		}).start();

		mapElements.map
				.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
					@SuppressLint("NewApi")
					@Override
					public void onMapClick(LatLng l) {
						Location loc = new Location("Test");
						loc.setLatitude(l.latitude);
						loc.setLongitude(l.longitude);
						loc.setAltitude(0);
						loc.setAccuracy(1);
						loc.setTime(System.currentTimeMillis());
						loc.setElapsedRealtimeNanos(1000);
						lm.setTestProviderLocation("Test", loc);
					}
				});
	}

	private class FakeLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {

			// Called when a new location is found by the network location
			// provider.
			Log.i("lat", location.getLatitude() + "");
			Log.i("long", location.getLongitude() + "");
			// map.addMarker(new MarkerOptions().position(new
			// LatLng(location.getLatitude(), location.getLongitude())));
			mapElements.plo.add(new LatLng(location.getLatitude(), location
					.getLongitude()));
			Log.i("size", "" + mapElements.plo.getPoints().size() + ":"
					+ mapElements.plo.hashCode());
			if (mapElements.pl != null) {
				mapElements.pl.remove();
			}

			mapElements.pl = mapElements.map.addPolyline(mapElements.plo);
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
}
