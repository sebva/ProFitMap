package ch.hearc.profitmap.model;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;

public class Track
{
	private String name;
	private double length;
	private List<TrackInstance> trackInstances;

	public Track()
	{
		trackInstances = new ArrayList<TrackInstance>();
	}
	
	public void addTrackInstance(TrackInstance trackInstance)
	{
		trackInstances.add(trackInstance);
	}

	public String getName()
	{
		return name;
	}
	
	public boolean isSingleInstance()
	{
		return trackInstances.size() == 1;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public double getLength()
	{
		if(trackInstances.size() == 0)
			return 0;
		
		List<Location> locations = trackInstances.get(0).getLocations();
		Location current = locations.get(0);
		double length = 0;
		for (Location location : locations)
		{
			length += current.distanceTo(location);
			current = location;
		}
		
		return length;
	}

	public List<TrackInstance> getTrackInstances()
	{
		return trackInstances;
	}

	public TrackInstance getTrackInstance(int position)
	{
		return trackInstances.get(position);
	}
	
	
}
