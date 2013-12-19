package ch.hearc.profitmap.model;

import android.location.Location;

public class GeoImage
{
	private String imagePath;
	private Location location;
	
	public GeoImage(String imagePath, Location location)
	{
		this.imagePath = imagePath;
		this.location = location;
	}

	public String getImagePath()
	{
		return imagePath;
	}

	public void setImagePath(String imagePath)
	{
		this.imagePath = imagePath;
	}

	public Location getLocation()
	{
		return location;
	}

	public void setLocation(Location location)
	{
		this.location = location;
	}
	
	
}
