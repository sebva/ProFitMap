package ch.hearc.profitmap.gui;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.util.Log;
import ch.hearc.profitmap.R;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapElements {

	public GoogleMap map;
	public static final LatLng NEUCH_LOC = new LatLng(47.0045047, 6.957424);
	public MarkerOptions mo = new MarkerOptions().position(NEUCH_LOC)
			.snippet("Tits!").title("Prout!");
	// public Map<Marker,MarkerOptions> markerMap;
	public List<MarkerOptions> moList;

	public PolylineOptions plo = new PolylineOptions().geodesic(true).color(
			Color.parseColor("#AA66CC"));
	public Polyline pl;

	private boolean isStarted = false;

	public MapElements() {
		/*
		 * map.setOnCameraChangeListener(new OnCameraChangeListener() {
		 * 
		 * @Override public void onCameraChange(CameraPosition position) {
		 * 
		 * 
		 * } });
		 */
		moList = new ArrayList<MarkerOptions>();
		Log.i("mapE", hashCode()+"");

	}

	public void showMarkers() {
		for (MarkerOptions mo : moList) {
			map.addMarker(mo);
		}
	}

	public void showPolyline() {
		pl = map.addPolyline(plo);
	}

	public void addPointAndRefreshPolyline(LatLng loc) {
		plo.add(loc);
		if (pl != null) {
			pl.remove();
		}
		pl = map.addPolyline(plo);
		Log.i("MapElements", "Added point " + loc.latitude + ", "
				+ loc.longitude);
	}

	public void start(LatLng startPosition) {
		if (!isStarted) {
			BitmapDescriptor icon = BitmapDescriptorFactory
					.fromResource(R.drawable.ic_start_flag);

			MarkerOptions moS = new MarkerOptions().icon(icon).position(
					startPosition);
			map.addMarker(moS);
			moList.add(moS);
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(startPosition,
					18));

			isStarted = true;
		}
	}

	public void end(LatLng startPosition) {
		
		if (isStarted) {
			BitmapDescriptor icon = BitmapDescriptorFactory
					.fromResource(R.drawable.ic_end_flag);

			MarkerOptions moS = new MarkerOptions().icon(icon).position(
					startPosition);
			map.addMarker(moS);
			moList.add(moS);
			
			isStarted = false;
		}
	}

	public void clearMap()
	{
		plo = new PolylineOptions().geodesic(true).color(
				Color.parseColor("#AA66CC"));
		pl = map.addPolyline(plo);
		map.clear();
		Log.i("mapE", hashCode()+"");
	}
}
