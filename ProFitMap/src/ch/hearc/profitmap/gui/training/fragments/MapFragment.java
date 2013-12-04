package ch.hearc.profitmap.gui.training.fragments;

import java.io.File;
import java.io.FileInputStream;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ch.hearc.profitmap.R;
import ch.hearc.profitmap.gui.MapElements;
import ch.hearc.profitmap.gui.ActiveMapElements;
import ch.hearc.profitmap.gui.training.LiveTrainingActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment
{

	protected SupportMapFragment fragment;

	protected MapElements mapElements;

	protected Activity parentActivity;

	public MapFragment()
	{

	}

	@Override
	public void onAttach(Activity activity)
	{
		parentActivity = activity;
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.i("MSF", "onC");

		mapElements = ActiveMapElements.getInstance().getMapElements();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Log.i("MSF", "onCreateV");

		return inflater.inflate(R.layout.fragment_live_training_map, container, false);
	}

	@Override
	public void onResume() {
		Log.i("MSF", "onRes before");

		if (mapElements != null) {
			if (mapElements.map == null) {
				mapElements.map = fragment.getMap();
				setupMap();
			}

			else {
				mapElements.map = fragment.getMap();
				mapElements.showMarkers();
				mapElements.showPolyline();
			}
		}
		super.onResume();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		Log.i("MSF", "onActivityC");
		super.onActivityCreated(savedInstanceState);
		FragmentManager fm = getChildFragmentManager();
		fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
		if (fragment == null)
		{
			fragment = SupportMapFragment.newInstance();
			fm.beginTransaction().replace(R.id.map, fragment).commit();
		}
	}

	public void setupMap() {
		mapElements.map.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
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


	public void endTraining()
	{
		LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		Location l = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		LatLng endPosition = new LatLng(l.getLatitude(), l.getLongitude());
		mapElements.end(endPosition);
	}

	public boolean addPicMarkerToLocation(Location loc, String filePath, int orientation)
	{
		if (mapElements != null)
		{
			MarkerOptions mo = new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude()));
			mo.title(filePath);

			Log.i("fp", filePath);

			/*
			 * Bitmap micon = BitmapFactory.decodeFile(filePath); Matrix matrix = new Matrix(); matrix.postRotate(270); Bitmap rotated =
			 * Bitmap.createBitmap(micon, 0, 0, micon.getWidth(), micon.getHeight(), matrix, true); micon.recycle(); int coeff = rotated.getWidth()/150;
			 * 
			 * Bitmap scaled = Bitmap.createScaledBitmap(rotated, rotated.getWidth()/coeff, rotated.getHeight()/coeff,false);
			 */

			Log.i("orientation", orientation + " ");
			Bitmap scaled = decodeFile(new File(filePath));

			mo.icon(BitmapDescriptorFactory.fromBitmap(scaled));

			mapElements.map.addMarker(mo);
			mapElements.moList.add(mo);
			return true;
		}
		else
			return false;
	}

	// decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File f)
	{
		try
		{
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// The new size we want to scale to
			final int REQUIRED_SIZE = 70;

			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
				scale *= 2;

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			Matrix matrix = new Matrix();
			matrix.postRotate(270);
			Bitmap scaled = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
			Bitmap rotated = Bitmap.createBitmap(scaled, 0, 0, scaled.getWidth(), scaled.getHeight(), matrix, true);

			return rotated;
		}
		catch (Exception e)
		{
		}
		return null;
	}
}
