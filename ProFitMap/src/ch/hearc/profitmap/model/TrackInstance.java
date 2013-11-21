package ch.hearc.profitmap.model;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.location.Location;

public class TrackInstance
{
	private Date timestampStart;
	private Date timestampEnd;
	private int rating;
	private double totalPauseTime;
	private int numberOfPauses;
	private Collection<GeoImage> images;
	private GeoImage thumbnail;
	private List<Location> locations;
	private Statistics statistics;

	public TrackInstance()
	{
		locations = new LinkedList<Location>();
		statistics = new Statistics(this);
		timestampStart = new Date();
	}
	
	public static TrackInstance fromGpx(String gpx)
	{
		return null;
	}
	
	public String toGpx()
	{
		return null;
	}
	
	public List<Location> getLocations()
	{
		return locations;
	}
	
	public void addWaypoint(Location location)
	{
		locations.add(location);
	}
	
	public void endInstance()
	{
		timestampEnd = new Date();
	}

	public int getRating()
	{
		return rating;
	}

	public void setRating(int rating)
	{
		this.rating = rating;
	}

	public GeoImage getThumbnail()
	{
		return thumbnail;
	}

	public void setThumbnail(GeoImage thumbnail)
	{
		this.thumbnail = thumbnail;
	}

	public Date getTimestampStart()
	{
		return timestampStart;
	}

	public Date getTimestampEnd()
	{
		return timestampEnd;
	}

	public double getTotalPauseTime()
	{
		return totalPauseTime;
	}

	public int getNumberOfPauses()
	{
		return numberOfPauses;
	}

	public Collection<GeoImage> getImages()
	{
		return images;
	}

	public Statistics getStatistics()
	{
		return statistics;
	}
	
	
}
