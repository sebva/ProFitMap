package ch.hearc.profitmap.gui.training.fragments.live;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ch.hearc.profitmap.R;
import ch.hearc.profitmap.gui.training.fragments.SummaryFragment;

public class LiveStatsFragment extends SummaryFragment
{
	public static final String ARG_SECTION_NUMBER = "section_number";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_live_training_dummy, container, false);
		TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
		dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
		return rootView;
	}
}
