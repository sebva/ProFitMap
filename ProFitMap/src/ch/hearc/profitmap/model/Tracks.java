package ch.hearc.profitmap.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import ch.hearc.profitmap.R;
import ch.hearc.profitmap.gui.training.fragments.live.LiveMapFragment;

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
		Track track3 = new Track();
		Track track4 = new Track();
		TrackInstance[] trackInstances = new TrackInstance[5];
			
		for(int i = 0; i < 5; i++)
		{
			trackInstances[i] = new TrackInstance();
			for(int j = 0; j < 10; j++)
			{
				Location l = new Location("My awesome provider");
				LatLng randomLatLng = LiveMapFragment.randomLatLng(); 
				l.setLatitude(47.0045047 + Math.sin(j)*j/2000);
				l.setLongitude(6.957424 + Math.cos(j)*j/2000);
				trackInstances[i].addWaypoint(l);
			}
		}
		
		track1.addTrackInstance(trackInstances[0]);
		track1.addTrackInstance(trackInstances[1]);
		track2.addTrackInstance(trackInstances[2]);
		
		track3.addTrackInstance(trackInstances[0]);
		track3.addTrackInstance(trackInstances[1]);
		track3.addTrackInstance(trackInstances[2]);
		track3.addTrackInstance(trackInstances[3]);
		track3.addTrackInstance(trackInstances[4]);
		
		track4.addTrackInstance(trackInstances[0]);
		track4.addTrackInstance(trackInstances[1]);
		track4.addTrackInstance(trackInstances[2]);
		
		track1.setName("Track n°1");
		track2.setName("Track with Ghost");
		track3.setName("Everest climbing");
		track4.setName("Going to Vaucher's house");
		
		addTrack(track1);
		addTrack(track2);
		addTrack(track3);
		addTrack(track4);
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
				return position;
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
