package ch.hearc.profitmap.model;

import android.location.Location;
import android.media.Image;

public class GeoImage
{
	private String imagePath;
	private Location location;
	private transient Image thumbnail;
}
