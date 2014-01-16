package ch.hearc.profitmap.gui.training.fragments;

import java.io.File;
import java.io.FileInputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ch.hearc.profitmap.R;
import ch.hearc.profitmap.gui.ActiveMapElements;
import ch.hearc.profitmap.gui.MapElements;
import ch.hearc.profitmap.gui.TrackDetailActivity;
import ch.hearc.profitmap.gui.training.LiveTrainingActivity;
import ch.hearc.profitmap.gui.training.interfaces.TrackInstanceProvider;
import ch.hearc.profitmap.model.GeoImage;
import ch.hearc.profitmap.model.TrackInstance;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment {

	protected SupportMapFragment fragment;

	protected MapElements mapElements;

	protected Activity parentActivity;

	protected TrackInstance trackInstance;

	public MapFragment() {

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		parentActivity = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("MF", "Created");

		mapElements = ActiveMapElements.getInstance().getMapElements();
		//mapElements.clearMap();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_live_training_map, container,
				false);
	}

	@Override
	public void onResume() {
		Log.i("MSF", "onRes before");
		trackInstance = ((TrackInstanceProvider) parentActivity).getTrackInstance();
		/*if (parentActivity instanceof LiveTrainingActivity) {
			LiveTrainingActivity lta = (LiveTrainingActivity) parentActivity;
			trackInstance = lta.getTrackInstance();
		} else if (parentActivity instanceof TrackDetailActivity) {
			TrackDetailActivity tda = (TrackDetailActivity) parentActivity;
			trackInstance = tda.getTrackInstance();
		}*/
		if (mapElements != null) {
			if (mapElements.map == null) {
				Log.i("MSF", "map is null");
				mapElements.map = fragment.getMap();
				setupMap();
				addWaypoints();
				mapElements.setMapLoadedListener();
			}

			else {
				Log.i("MSF", "map is not null");
				mapElements.map = fragment.getMap();
				setupMap();
				addWaypoints();
				mapElements.showMarkers();
				mapElements.showPolyline();
				mapElements.setMapLoadedListener();
			}
		}
		else
			Log.e("MF", "mapElements is null");
		super.onResume();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		FragmentManager fm = getChildFragmentManager();
		fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
		if (fragment == null) {
			fragment = SupportMapFragment.newInstance();
			fm.beginTransaction().replace(R.id.map, fragment).commit();
		}
	}

	public void setupMap() {
		mapElements.clearMap();
		mapElements.map.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				// TODO : dropbox image viewer
				if (marker.getTitle() != null) {
					Log.i("testM", marker.getTitle());
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setDataAndType(
							Uri.parse("file://" + marker.getTitle()), "image/*");
					startActivity(intent);
				}
				return true;
			}
		});
	}

	public void endTraining() {
		LocationManager lm = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		Location l = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		LatLng endPosition = new LatLng(l.getLatitude(), l.getLongitude());
		mapElements.end(endPosition);
	}

	public boolean addPicMarkerToLocation(Location loc, String dropBoxPath) {
		if (mapElements != null) {
			/*Log.i("picMarker", "add");
			MarkerOptions mo = new MarkerOptions().position(new LatLng(loc
					.getLatitude(), loc.getLongitude()));
			mo.title(dropBoxPath);

			mo.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.ic_action_photo));*/

			//mapElements.map.addMarker(mo);
			// mapElements.moList.add(mo);
			mapElements.addPictureMarker(loc, dropBoxPath);
			return true;
		} else
			return false;
	}

	public void setTrackInstance(TrackInstance trackInstance) {
		this.trackInstance = trackInstance;
	}

	protected void addWaypoints() {
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		if (trackInstance != null) {
			if (trackInstance.getWaypoints().size() != 0) {
				for (Location l : trackInstance.getWaypoints()) {
					// Log.i("mapF", "Adding waypoint");
					mapElements.start(new LatLng(l.getLatitude(), l
							.getLongitude()));
					mapElements.addPointAndRefreshPolyline(new LatLng(l
							.getLatitude(), l.getLongitude()));
					builder.include(new LatLng(l.getLatitude(), l
							.getLongitude()));
				}
				Location l = trackInstance.getWaypoints().get(
						trackInstance.getWaypoints().size() - 1);
				mapElements.end(new LatLng(l.getLatitude(), l.getLongitude()));
				builder.include(new LatLng(l.getLatitude(), l.getLongitude()));
			}

			LatLngBounds bounds = builder.build();
			int padding = 25; // offset from edges of the map in pixels
			CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
			mapElements.setInitialCameraUpdate(cu);
			
			for (GeoImage geoImage : trackInstance.getImages()) {
				mapElements.addPictureMarker(geoImage.getLocation(), geoImage.getImagePath());
			}
		}
	}
}
