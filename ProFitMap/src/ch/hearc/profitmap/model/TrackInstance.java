package ch.hearc.profitmap.model;

import java.util.Collection;
import java.util.Date;

import android.location.Location;

public class TrackInstance
{

	private Date timestampStart;
	private Date timestampEnd;
	private int rating;
	private double totalPauseTime;
	private int numberOfPauses;
	private TrackInstance trackInstance;
	private TrackInstance ghost;
	private Track track;
	private Collection<GeoImage> images;
	private GeoImage thumbnail;
	private Collection<Location> location;
	private Statistics statistics;

	public Location[] getLocations()
	{
		return null;
	}

}
