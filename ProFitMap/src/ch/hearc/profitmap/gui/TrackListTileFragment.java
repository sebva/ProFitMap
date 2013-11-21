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
import ch.hearc.profitmap.model.Tracks;

public class TrackListTileFragment extends Fragment
{

	public static final String ARG_SPORT_NUMBER = "sport_number";
	private ListAdapter mAdapter;
	private GridView mGridView;

	public TrackListTileFragment()
	{
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_trackinstance_grid, container, false);
		int i = getArguments().getInt(ARG_SPORT_NUMBER);
		
		mGridView = (GridView) rootView.findViewById(R.id.trackinstance_grid);
		setSport(i);
		
		mGridView.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            Intent intent = new Intent(getActivity(), TrackDetailActivity.class);
	            // TODO: Implement track instance differientiation
	            startActivity(intent);
	        }
	    });

		return rootView;
	}

	public void setSport(int sportId)
	{
		mAdapter = Tracks.getInstance(sportId).getAdapter(getActivity());
		mGridView.setAdapter(mAdapter);
	}

	public void setSortMode(String sortMode)
	{

	}

}
