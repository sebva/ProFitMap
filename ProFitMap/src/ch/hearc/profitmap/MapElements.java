package ch.hearc.profitmap;

import java.io.Serializable;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapElements implements Serializable {

	public GoogleMap map;
	public static final LatLng NEUCH_LOC = new LatLng(47.0045047, 6.957424);
	public MarkerOptions mo = new MarkerOptions().position(NEUCH_LOC)
			.snippet("Tits!").title("Prout!");
	public Marker m;

	public PolylineOptions plo = new PolylineOptions().geodesic(true).color(
			Color.parseColor("#AA66CC"));
	public Polyline pl;
	
	public MapElements()
	{
		
	}
	
}
