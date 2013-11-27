package ch.hearc.profitmap.gui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListAdapter;
import ch.hearc.profitmap.R;
import ch.hearc.profitmap.model.Track;
import ch.hearc.profitmap.model.Tracks;

public class TrackListTilesFragment extends Fragment
{

	public static final String ARG_SPORT_NUMBER = "sport_number";
	private GridView mGridView;
	
	// Only 1 of the 2 following attributes has to be not null
	private Tracks currentTracks = null;
	private Track currentTrack = null;

	public TrackListTilesFragment()
	{
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_trackinstance_grid, container, false);
		int i = getArguments().getInt(ARG_SPORT_NUMBER);
		
		mGridView = (GridView) rootView.findViewById(R.id.trackinstance_grid);
		showTracks(Tracks.getInstance(i));
		
		return rootView;
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
	            
	            if(currentTracks.getTrack(position).isSingleInstance())
	            {
	            	Intent intent = new Intent(getActivity(), TrackDetailActivity.class);
	            	startActivity(intent);
	            }
	            else
	            	showTrack(currentTracks.getTrack(position));
	        }
	    });
	}
	
	private void showTrack(Track track)
	{
		currentTrack = track;
		
		ListAdapter adapter = track.getAdapter(getActivity());
		mGridView.setAdapter(adapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            
	            Intent intent = new Intent(getActivity(), TrackDetailActivity.class);
	            startActivity(intent);
	        }
	    });
	}
	
	public void setSport(int id)
	{
		showTracks(Tracks.getInstance(id));
	}

	public void setSortMode(String sortMode)
	{

	}

}
