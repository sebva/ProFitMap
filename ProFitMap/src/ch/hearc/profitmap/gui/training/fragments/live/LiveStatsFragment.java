package ch.hearc.profitmap.gui.training.fragments.live;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import ch.hearc.profitmap.R;
import ch.hearc.profitmap.gui.training.fragments.SummaryFragment;
import ch.hearc.profitmap.gui.training.fragments.SummaryFragment.StatisticsProvider;
import ch.hearc.profitmap.model.Statistics;

public class LiveStatsFragment extends SummaryFragment
{
	public static final String ARG_SECTION_NUMBER = "section_number";
	
	private Statistics mStatistics;
		

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public void refreshPanel() {
		Log.i("SF", "refreshing");
		mGridView.postInvalidate();
		/*BaseAdapter ba = (BaseAdapter)mGridView.getAdapter();
		ba.notifyDataSetChanged();*/
		mGridView.invalidateViews();
	}

}
