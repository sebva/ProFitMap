package ch.hearc.profitmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapElements {

	public GoogleMap map;
	public static final LatLng NEUCH_LOC = new LatLng(47.0045047, 6.957424);
	public MarkerOptions mo = new MarkerOptions().position(NEUCH_LOC)
			.snippet("Tits!").title("Prout!");
	//public Map<Marker,MarkerOptions> markerMap;
	public List<MarkerOptions> moList;

	public PolylineOptions plo = new PolylineOptions().geodesic(true).color(
			Color.parseColor("#AA66CC"));
	public Polyline pl;
	
	public MapElements()
	{
		/*map.setOnCameraChangeListener(new OnCameraChangeListener() {
			
			@Override
			public void onCameraChange(CameraPosition position) {

			
			}
		});*/
		moList = new ArrayList<MarkerOptions>();
	}
	
	public void showMarkers()
	{
		for (MarkerOptions mo : moList) {
			map.addMarker(mo);
		}
	}
	public void showPolyline()
	{
		pl = map.addPolyline(plo);
	}
	public void addPointAndRefreshPolyline(LatLng loc)
	{
		plo.add(loc);
		if (pl != null) {
			pl.remove();
		}
		pl = map.addPolyline(plo);
		Log.i("MapElements","Added point " + loc.latitude + ", " + loc.longitude);
	}
}
