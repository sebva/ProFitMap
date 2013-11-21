package ch.hearc.profitmap.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import ch.hearc.profitmap.R;

public class Tracks
{
	public interface TrackListUpdateListener
	{
		public void onTrackListUpdated(Set<Track> tracks);
	}

	private static final String TAG = "Tracks";
	private List<Track> tracks;
	private static Map<Integer, Tracks> instances = null;
	
	static
	{
		instances = new HashMap<Integer, Tracks>();
	}

	private Tracks()
	{
		this.tracks = new ArrayList<Track>();
		Track track1 = new Track();
		Track track2 = new Track();
		TrackInstance[] trackInstances = new TrackInstance[3];
			
		for(int i = 0; i < 3; i++)
		{
			trackInstances[i] = new TrackInstance();
			for(int j = 0; j < 100; j++)
			{
				trackInstances[i].addWaypoint(new Location("My awesome provider"));
			}
		}
		
		track1.addTrackInstance(trackInstances[0]);
		track1.addTrackInstance(trackInstances[1]);
		track2.addTrackInstance(trackInstances[2]);
		track1.setName("Track n°1");
		track2.setName("Track with Ghost");
		
		addTrack(track1);
		addTrack(track2);
	}

	public static synchronized Tracks getInstance(int sport)
	{
		if(instances.containsKey(sport))
			return instances.get(sport);
		else
		{
			Tracks tracks = new Tracks();
			instances.put(sport, tracks);
			return tracks;
		}
	}

	
	public void addTrack(Track track)
	{
		tracks.add(track);
	}
	
	public List<Track> getTracks()
	{
		return tracks;
	}
	
	public ListAdapter getAdapter(final Context c)
	{
		return new BaseAdapter()
		{
			@Override
			public int getCount()
			{
				return tracks.size();
			}

			@Override
			public Object getItem(int position)
			{
				return tracks.get(position);
			}

			@Override
			public long getItemId(int position)
			{
				return tracks.get(position).hashCode();
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				LayoutInflater inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				Track track = tracks.get(position);
				View v;
		        if (convertView == null) {  // if it's not recycled, initialize some attributes
		            v = inflater.inflate(R.layout.tile, null);
		        } else {
		            v = convertView;
		        }
		        
		        TextView tv = (TextView)v.findViewById(R.id.textView);
		        tv.setText(track.getName());
		        
		        TextView count = (TextView)v.findViewById(R.id.count);
		        if(!track.isSingleInstance())
		        {
		        	count.setVisibility(View.VISIBLE);
		        	count.setText(String.valueOf(track.getTrackInstances().size()));
		        }
		        else
		        	count.setVisibility(View.INVISIBLE);
		        return v;
			}
		};
	}

	public Track getTrack(int position)
	{
		return tracks.get(position);
	}

}
