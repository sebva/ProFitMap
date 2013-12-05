package ch.hearc.profitmap.gui.training.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import ch.hearc.profitmap.R;
import ch.hearc.profitmap.gui.TrackDetailActivity;
import ch.hearc.profitmap.model.Statistics;

public class SummaryFragment extends Fragment
{
	private Statistics mStatistics;
	private GridView mGridView;

	public SummaryFragment()
	{
		// Empty constructor required for fragment subclasses
	}
	
	public void setStatistics(Statistics statistics)
	{
		mStatistics = statistics;
		mGridView.setAdapter(mStatistics.getAdapter(getActivity()));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_grid, container, false);
		
		mGridView = (GridView) rootView.findViewById(R.id.trackinstance_grid);
		mGridView.setAdapter(((TrackDetailActivity) getActivity()).getStatistics().getAdapter(getActivity()));
		
		return rootView;
	}

}
