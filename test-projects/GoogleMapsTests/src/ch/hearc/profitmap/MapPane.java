package ch.hearc.profitmap;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class MapPane extends Activity {

	private GoogleMap map;
	private PolylineOptions plo = new PolylineOptions().geodesic(true).color(
			Color.parseColor("#AA66CC"));
	private Polyline pl;

	private static final LatLng NEUCH_LOC = new LatLng(47.0045047, 6.957424);
	public static final int RESULT_GALLERY = 0;
	private MarkerOptions mo = new MarkerOptions().position(NEUCH_LOC)
			.snippet("Tits!").title("Prout!");
	private Marker m;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		 try {
		     MapsInitializer.initialize(this);
		 } catch (GooglePlayServicesNotAvailableException e) {
		     e.printStackTrace();
		 }
		setContentView(R.layout.activity_main);
//		while ( ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
//				.getMap() == null)
//		{
//		
//		}
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		//map.moveCamera(CameraUpdateFactory.newLatLngZoom(NEUCH_LOC, 18));
		/*
		 * map.addMarker(new
		 * MarkerOptions().position(NEUCH_LOC).title("Neuch\' Bitch!")
		 * .icon(BitmapDescriptorFactory
		 * .fromResource(R.drawable.common_signin_btn_icon_light))
		 * .snippet("ProutTits!"));
		 */
		m = map.addMarker(mo);

		map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_GALLERY);
				return false;
			}
		});
		
		map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
			
			@Override
			public void onMapLongClick(LatLng point) {
				if (m != null)
				{
					m.remove();
				}
				mo.position(point);
				m = map.addMarker(mo);
			}
		});

		setupFakeGPS();
		setupGPS();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_GALLERY && data != null
				&& data.getData() != null) {
			Uri _uri = data.getData();

			// User had pick an image.
			Cursor cursor = getContentResolver()
					.query(_uri,
							new String[] { android.provider.MediaStore.Images.ImageColumns.DATA },
							null, null, null);
			cursor.moveToFirst();

			// Link to the image
			final String imageFilePath = cursor.getString(0);

			Bitmap micon = BitmapFactory.decodeFile(imageFilePath);
			
			if (m != null)
			{
				m.remove();
			}
			
			mo.icon(BitmapDescriptorFactory.fromBitmap(micon));
			
			m = map.addMarker(mo);
			
			Log.i(_uri.toString(), imageFilePath);
			cursor.close();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void setupFakeGPS() {
		final LocationManager lm;
		FakeLocationListener ll;
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		ll = new FakeLocationListener();
		if (lm.getProvider("Test") == null) {
			lm.addTestProvider("Test", false, false, false, false, false,
					false, false, 0, 1);
		}
		lm.setTestProviderEnabled("Test", true);
		lm.requestLocationUpdates("Test", 0, 0, ll);

		map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
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

	private void setupGPS() {// Acquire a reference to the system Location
								// Manager
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new RealLocationListener();

		// Register the listener with the Location Manager to receive location
		// updates
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
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
			plo.add(new LatLng(location.getLatitude(), location.getLongitude()));
			if (pl != null) {
				pl.remove();
			}

			pl = map.addPolyline(plo);
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

	private class RealLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {

			// Called when a new location is found by the network location
			// provider.
			Log.i("lat", location.getLatitude() + "");
			Log.i("long", location.getLongitude() + "");
			// map.addMarker(new MarkerOptions().position(new
			// LatLng(location.getLatitude(), location.getLongitude())));
			plo.add(new LatLng(location.getLatitude(), location.getLongitude()));
			if (pl != null) {
				pl.remove();
			}

			pl = map.addPolyline(plo);
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