package ch.hearc.profitmap.gui.training.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import ch.hearc.profitmap.R;
import ch.hearc.profitmap.model.Statistics;
import ch.hearc.profitmap.model.Statistics.TypeStatistics;
public class SummaryFragment extends Fragment
{
	public interface StatisticsProvider
	{
		public Statistics getStatistics();
		public TypeStatistics getTypeStatistics();
		public int getSportId();
	}

	private Statistics mStatistics;
	protected GridView mGridView;

	public SummaryFragment()
	{
		// Empty constructor required for fragment subclasses
	}
	
	public void setStatistics(Statistics statistics, TypeStatistics typeStatistics, int sportId)
	{
		mStatistics = statistics;
		mGridView.setAdapter(mStatistics.getAdapter(getActivity(), typeStatistics, sportId));
		mStatistics.computeStatistics();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_grid, container, false);
		
		mGridView = (GridView) rootView.findViewById(R.id.trackinstance_grid);
		Statistics statistics = ((StatisticsProvider)getActivity()).getStatistics();
		TypeStatistics typeStatistics = ((StatisticsProvider)getActivity()).getTypeStatistics();
		int sportId = ((StatisticsProvider)getActivity()).getSportId();
		if(statistics != null)
			setStatistics(statistics, typeStatistics, sportId);
		
		return rootView;
	}

}
