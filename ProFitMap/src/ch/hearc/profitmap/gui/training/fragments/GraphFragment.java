package ch.hearc.profitmap.gui.training.fragments;

import javax.microedition.khronos.opengles.GL;

import com.jjoe64.graphview.GraphView.*;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import ch.hearc.profitmap.R;
import ch.hearc.profitmap.gui.training.interfaces.TrackInstanceProvider;
import ch.hearc.profitmap.model.TrackInstance;

public class GraphFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";

	protected TrackInstance mTrackInstance;

	protected LineGraphView altitudeGraphView;

	protected LineGraphView speedGraphView;

	protected GraphViewSeries altitudeSeries;

	protected GraphViewSeries speedSeries;

	public GraphFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_live_training_graph,
				container, false);
		
		System.out.println("oCV Graph");

		mTrackInstance = ((TrackInstanceProvider) getActivity())
				.getTrackInstance();

		createAndAddGraphsToFragment(rootView);
		
		return rootView;
	}

	protected void createAndAddGraphsToFragment(View rootView) {
		altitudeSeries = new GraphViewSeries(
				new GraphViewData[] { });

		speedSeries = new GraphViewSeries(
				new GraphViewData[] { });
		
		for (int i=0; i < mTrackInstance.getWaypoints().size() ; i++)
		{
			Location location = mTrackInstance.getWaypoints().get(i);
			altitudeSeries.appendData(new GraphViewData(i+1,location.getAltitude()), true, 1000);
			speedSeries.appendData(new GraphViewData(i+1,location.getSpeed()), true, 1000);
		}

		altitudeGraphView = new LineGraphView(rootView.getContext() // context
				, "Altitudes" // heading
		);
		speedGraphView = new LineGraphView(rootView.getContext() // context
				, "Speed" // heading
		);
		
		speedGraphView.addSeries(speedSeries); // data
		altitudeGraphView.addSeries(altitudeSeries); // data

		LinearLayout layout1 = (LinearLayout) rootView
				.findViewById(R.id.graph1);
		LinearLayout layout2 = (LinearLayout) rootView
				.findViewById(R.id.graph2);
		layout1.addView(altitudeGraphView);
		layout2.addView(speedGraphView);
	}
}