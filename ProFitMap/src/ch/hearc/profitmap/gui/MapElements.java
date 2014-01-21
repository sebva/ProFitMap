package ch.hearc.profitmap.gui;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import ch.hearc.profitmap.R;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

	public List<MarkerOptions> moList = new ArrayList<MarkerOptions>();
	private List<Marker> persistentMarkerList = new ArrayList<Marker>();

	public PolylineOptions plo = new PolylineOptions().geodesic(true).color(
			Color.parseColor("#AA66CC"));
	public Polyline pl;

	private boolean isStarted = false;

	private Polyline ghostTrackPl;
	private PolylineOptions ghostTrackPlo = new PolylineOptions()
			.geodesic(true).color(Color.parseColor("#FFBB33"));;

	private Marker cLocMarker;

	private Marker ghostLocMarker;

	private Polyline ghostDifferencePl;
	private PolylineOptions ghostDifferencePlo = new PolylineOptions()
			.geodesic(true).color(Color.parseColor("#FFBB33"));
	private CameraUpdate initialCu;

	public MapElements() {

		Log.i("mapE", hashCode() + "create");

	}

	public void showMarkers() {
		Log.i("mapE", "showing m" + moList.size());
		for (MarkerOptions mo : moList) {
			if (mo != null) {
				map.addMarker(mo);
			}
		}
		/*
		 * for (Marker mo : persistentMarkerList) { if (mo != null) {
		 * MarkerOptions nmo = new MarkerOptions(); if
		 * ("currentposition".equals(mo.getTitle())) {
		 * nmo.title("currentposition"
		 * ).position(mo.getPosition()).icon(BitmapDescriptorFactory
		 * .fromResource(R.drawable.ic_current_pos_indicator)); } else {
		 * nmo.title
		 * ("ghostposition").position(mo.getPosition()).icon(BitmapDescriptorFactory
		 * .fromResource(R.drawable.ic_action_ghost)); } map.addMarker(nmo); } }
		 */
	}

	public void showPolyline() {
		pl = map.addPolyline(plo);
		ghostDifferencePl = map.addPolyline(ghostDifferencePlo);
	}

	public void setMapLoadedListener() {

		map.setOnMapLoadedCallback(new OnMapLoadedCallback() {

			@Override
			public void onMapLoaded() {
				if (initialCu != null)
					map.moveCamera(initialCu);
			}
		});
	}

	public void addPointAndRefreshPolyline(LatLng loc) {
		plo.add(loc);
		if (pl != null) {
			pl.remove();
		}
		pl = map.addPolyline(plo);

		/*
		 * Log.i("MapElements", "Added point " + loc.latitude + ", " +
		 * loc.longitude);
		 */
	}

	public boolean start(LatLng startPosition) {
		if (!isStarted) {
			Log.i("mapE", "Starting map setup");
			BitmapDescriptor icon = BitmapDescriptorFactory
					.fromResource(R.drawable.ic_start_flag);

			MarkerOptions moS = new MarkerOptions().icon(icon).position(
					startPosition);
			map.addMarker(moS);
			moList.add(moS);

			isStarted = true;
			return true;
		}
		return false;
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
			Log.i("mapE", "Ending map setup");
		}
	}

	public void clearMap() {
		if (map != null)
			map.clear();
		plo = new PolylineOptions().geodesic(true).color(
				Color.parseColor("#AA66CC"));
		// pl = map.addPolyline(plo);
		//showMarkers();
		moList.clear();
		Log.i("mapE", hashCode() + "clear");
	}

	public void ghostAddPointAndRefreshPolyline(LatLng loc) {
		ghostTrackPlo.add(loc);
		if (ghostTrackPl != null) {
			ghostTrackPl.remove();
		}
		ghostTrackPl = map.addPolyline(ghostTrackPlo);
		/*
		 * Log.i("MapElements", "Added point " + loc.latitude + ", " +
		 * loc.longitude);
		 */

	}

	public void ghostEnd(LatLng startPosition) {
		BitmapDescriptor icon = BitmapDescriptorFactory
				.fromResource(R.drawable.ic_end_flag);

		MarkerOptions moS = new MarkerOptions().icon(icon).position(
				startPosition);
		map.addMarker(moS);
		moList.add(moS);
	}

	public void drawPolylineDifferenceWithGhost(Location currentLocation,
			Location closestGhostLocation, boolean isAhead) {

		ghostDifferencePlo = null;
		if (isAhead) {
			ghostDifferencePlo = new PolylineOptions().geodesic(true).color(
					Color.parseColor("#99CC00"));
		} else {
			ghostDifferencePlo = new PolylineOptions().geodesic(true).color(
					Color.parseColor("#FF4444"));
		}

		ghostDifferencePlo.add(new LatLng(currentLocation.getLatitude(),
				currentLocation.getLongitude()));
		ghostDifferencePlo.add(new LatLng(closestGhostLocation.getLatitude(),
				closestGhostLocation.getLongitude()));

		if (ghostDifferencePl != null) {
			ghostDifferencePl.remove();
		}
		ghostDifferencePl = map.addPolyline(ghostDifferencePlo);

		MarkerOptions cmos = new MarkerOptions().icon(
				BitmapDescriptorFactory
						.fromResource(R.drawable.ic_action_ghost)).position(
				new LatLng(closestGhostLocation.getLatitude(),
						closestGhostLocation.getLongitude()));

		if (ghostLocMarker != null) {
			//persistentMarkerList.remove(ghostLocMarker);
			ghostLocMarker.remove();
		}
		ghostLocMarker = map.addMarker(cmos);
		//persistentMarkerList.add(ghostLocMarker);
		Log.i("mapE", "adding ghostMarker : " + moList.size());
	}

	public void drawCurrentPositionIndicator(Location currentLocation) {
		MarkerOptions cmos = new MarkerOptions().icon(
				BitmapDescriptorFactory
						.fromResource(R.drawable.ic_current_pos_indicator))
				.position(
						new LatLng(currentLocation.getLatitude(),
								currentLocation.getLongitude()));

		if (cLocMarker != null) {
			persistentMarkerList.remove(cLocMarker);
			cLocMarker.remove();
		}
		cLocMarker = map.addMarker(cmos);
		// persistentMarkerList.add(cLocMarker);

	}

	public void addPictureMarker(Location loc, String dropBoxPath) {
		MarkerOptions mo = new MarkerOptions()
				.position(new LatLng(loc.getLatitude(), loc.getLongitude()))
				.title(dropBoxPath)
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.ic_action_photo));

		// Adding the marker to the marker list. Can't add it to the map directly because of the map is cleared after this method is calld
		moList.add(mo);

	}

	public void setInitialCameraUpdate(CameraUpdate cu) {
		this.initialCu = cu;
	}
}
