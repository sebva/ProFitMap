package ch.hearc.profitmap.gui;

import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.Toast;
import ch.hearc.profitmap.R;
import ch.hearc.profitmap.TrackListActivity;
import ch.hearc.profitmap.model.Track;
import ch.hearc.profitmap.model.TrackInstance;
import ch.hearc.profitmap.model.Tracks;
import ch.hearc.profitmap.model.Tracks.TrackListUpdateListener;


public class TrackListTilesFragment extends Fragment implements TrackListUpdateListener
{

	public static final String ARG_SPORT_NUMBER = "sport_number";
	private GridView mGridView;
	
	// Only 1 of the 2 following attributes has to be not null
	private Tracks currentTracks = null;
	private Track currentTrack = null;
	private int mSport;

	public TrackListTilesFragment()
	{
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_grid, container, false);
		mSport = getArguments().getInt(ARG_SPORT_NUMBER);
		
		mGridView = (GridView) rootView.findViewById(R.id.trackinstance_grid);
		Tracks tracks = Tracks.getInstance(mSport);
		setSortMode(0);
		showTracks(tracks);
		tracks.setListener(this);
		
		return rootView;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		mGridView.invalidateViews();
	}
	
	/**
	 * Called by the activity
	 * @return true if the normal behaviour should be executed
	 */
	public boolean onBackPressed()
	{
		if(currentTrack == null)
			return true;
		else
		{
			showTracks(currentTracks);
			return false;
		}
		
	}

	private void showTracks(Tracks tracks)
	{
		currentTrack = null;
		currentTracks = tracks;
		
		ListAdapter adapter = tracks.getAdapter(getActivity());
		mGridView.setAdapter(adapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            boolean isTrackReady = true;
	            Track track = currentTracks.getTrack(position);
	            if(track != null)
	            {
	            	for(TrackInstance trackInstance : track.getTrackInstances())
	            		if(trackInstance == null || trackInstance.getTimestampStart() == null)
	            		{
	            			isTrackReady = false;
	            			break;
	            		}
	            }
	            else
	            	isTrackReady = false;
	            
	            if(isTrackReady)
	            {
					if(track.isSingleInstance())
		            {
		            	Intent intent = new Intent(getActivity(), TrackDetailActivity.class);
		            	intent.putExtra("trackId", position);
		            	intent.putExtra("trackInstanceId", 0);
		            	intent.putExtra("sport", mSport);
		            	startActivity(intent);
		            }
		            else
		            	showTrack(track,position);
	            }
	            else
		            Toast.makeText(getActivity(), R.string.track_not_ready, Toast.LENGTH_LONG).show();
	        }
	    });
	}
	
	private void showTrack(Track track, final int position)
	{
		((TrackListActivity)getActivity()).hideSortModes();
		currentTrack = track;
		
		ListAdapter adapter = track.getAdapter(getActivity());
		mGridView.setAdapter(adapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position2, long id) {
	            
	            Intent intent = new Intent(getActivity(), TrackDetailActivity.class);
	            intent.putExtra("trackInstanceId", position2);
	            intent.putExtra("trackId", position);
	            intent.putExtra("sport", mSport);
	            startActivity(intent);
	        }
	    });
	}
	
	public void setSport(int id)
	{
		mSport = id;
		showTracks(Tracks.getInstance(mSport));
	}

	public void setSortMode(int sortMode)
	{
		if(currentTracks != null)
		{
			Comparator<Track> comparator;
			switch(sortMode)
			{
				default:
				case 0: // By name
					comparator = new Comparator<Track>()
					{
						@Override
						public int compare(Track lhs, Track rhs)
						{
							return lhs.getName().compareToIgnoreCase(rhs.getName());
						}
					};
					break;
				case 1: // By date
					comparator = new Comparator<Track>()
					{
						@Override
						public int compare(Track lhs, Track rhs)
						{
							List<TrackInstance> lhsi = lhs.getTrackInstances();
							List<TrackInstance> rhsi = lhs.getTrackInstances();
							return lhsi.get(lhsi.size() -1).getTimestampEnd().compareTo(rhsi.get(rhsi.size() -1).getTimestampEnd());
						}
					};
					break;
				case 2: // By distance
					final Location location = ((LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE)).getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
					comparator = new Comparator<Track>()
					{
						@Override
						public int compare(Track lhs, Track rhs)
						{
							Location lhsLoc = lhs.getTrackInstance(0).getWaypoints().get(0);
							Location rhsLoc = rhs.getTrackInstance(0).getWaypoints().get(0);
							return Float.compare(lhsLoc.distanceTo(location), rhsLoc.distanceTo(location));
						}
					};
					break;
			}
			currentTracks.sortTracks(comparator);
		}
	}

	@Override
	public void onTrackListUpdated()
	{
		mGridView.invalidateViews();
	}

}
