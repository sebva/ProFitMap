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
	private long totalPauseTime;
	private int numberOfPauses;
	private Collection<GeoImage> images;
	private GeoImage thumbnail;
	private List<Location> waypoints;
	private Statistics statistics;

	public TrackInstance()
	{
		waypoints = new LinkedList<Location>();
		statistics = new Statistics(this);
		timestampStart = new Date();
		timestampEnd = new Date();
	}
	
	public List<Location> getWaypoints()
	{
		return waypoints;
	}
	
	public void addWaypoint(Location location)
	{
		waypoints.add(location);
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

	public long getTotalPauseTime()
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

	public void setTimestampEnd(Date timestampEnd)
	{
		this.timestampEnd = timestampEnd;
	}
	
	
}
