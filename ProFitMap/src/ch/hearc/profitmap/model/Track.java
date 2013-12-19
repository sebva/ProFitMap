package ch.hearc.profitmap.model;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import ch.hearc.profitmap.R;

public class Track
{
	private String name;
	private double length;
	protected List<TrackInstance> trackInstances;
	private Tracks mTracks;

	public Track()
	{
		trackInstances = new ArrayList<TrackInstance>();
	}
	
	public void addTrackInstance(TrackInstance trackInstance)
	{
		trackInstances.add(trackInstance);
		if(mTracks != null)
			mTracks.saveTrackInstanceToDropbox(this, trackInstance);
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
		
		List<Location> locations = trackInstances.get(0).getWaypoints();
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
	
	public ListAdapter getAdapter(final Context c)
	{
		return new BaseAdapter()
		{
			@Override
			public int getCount()
			{
				return trackInstances.size();
			}

			@Override
			public Object getItem(int position)
			{
				return trackInstances.get(position);
			}

			@Override
			public long getItemId(int position)
			{
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				LayoutInflater inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				TrackInstance trackInstance = trackInstances.get(position);
				
				View v;
		        if (convertView == null) {  // if it's not recycled, initialize some attributes
		            v = inflater.inflate(R.layout.tile, null);
		        } else {
		            v = convertView;
		        }
		        
		        TextView tv = (TextView)v.findViewById(R.id.textView);
		        tv.setText(DateFormat.getDateTimeInstance().format(trackInstance.getTimestampStart()));
		        ImageView iv = (ImageView) v.findViewById(R.id.imageView);
		        
		        TextView count = (TextView)v.findViewById(R.id.count);
		        count.setVisibility(View.INVISIBLE);
		        return v;
			}
		};
	}

	void setTracks(Tracks t)
	{
		mTracks = t;
	}
}
