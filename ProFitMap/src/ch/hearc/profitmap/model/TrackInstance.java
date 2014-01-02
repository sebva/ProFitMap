package ch.hearc.profitmap.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.location.Location;
import android.util.Log;

public class TrackInstance
{
	private Date timestampStart;
	private Date timestampEnd;
	private int rating;
	private long totalPauseTime;
	private int numberOfPauses;
	private List<GeoImage> images;
	private String thumbnail;
	private List<Location> waypoints;
	private transient Statistics statistics;

	public TrackInstance()
	{
		waypoints = new LinkedList<Location>();
		images = new ArrayList<GeoImage>();
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
		statistics.addLocation(location);
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

	public String getThumbnail()
	{
		return thumbnail;
	}

	public void setThumbnail(String thumbnail)
	{
		Log.i("thumb", "Thumb = " + thumbnail);
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

	public List<GeoImage> getImages()
	{
		return images;
	}
	
	public void addImage(GeoImage image)
	{
		images.add(image);
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
