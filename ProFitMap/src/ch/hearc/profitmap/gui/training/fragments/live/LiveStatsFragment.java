package ch.hearc.profitmap.gui.training.fragments.live;

import android.util.Log;
import ch.hearc.profitmap.gui.training.fragments.SummaryFragment;

public class LiveStatsFragment extends SummaryFragment
{

	public void refreshPanel() {
		Log.i("SF", "refreshing");
		mGridView.postInvalidate();
		/*BaseAdapter ba = (BaseAdapter)mGridView.getAdapter();
		ba.notifyDataSetChanged();*/
		mGridView.invalidateViews();
	}

}
