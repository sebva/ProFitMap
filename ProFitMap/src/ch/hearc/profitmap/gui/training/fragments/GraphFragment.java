package ch.hearc.profitmap.gui.training.fragments;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.LinearLayout;
import ch.hearc.profitmap.R;
import ch.hearc.profitmap.gui.training.interfaces.TrackInstanceProvider;
import ch.hearc.profitmap.model.TrackInstance;

import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

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
		View rootView = inflater.inflate(R.layout.fragment_graph, container,
				false);

		System.out.println("oCV Graph");

		mTrackInstance = ((TrackInstanceProvider) getActivity())
				.getTrackInstance();

		createAndAddGraphsToFragment(rootView);

		return rootView;
	}

	protected void createAndAddGraphsToFragment(View rootView) {
		altitudeSeries = new GraphViewSeries(new GraphViewData[] {});

		speedSeries = new GraphViewSeries(new GraphViewData[] {});

		for (int i = 0; i < mTrackInstance.getWaypoints().size(); i++) {
			Location location = mTrackInstance.getWaypoints().get(i);
			altitudeSeries.appendData(new GraphViewData(location.getTime(),
					location.getAltitude()), true, 1000);
			speedSeries.appendData(new GraphViewData(location.getTime(),
					location.getSpeed() * 3.6), true, 1000);
		}

		altitudeGraphView = new LineGraphView(rootView.getContext() // context
				, getResources().getString(R.string.altitude_graph_title) // heading
		);
		speedGraphView = new LineGraphView(rootView.getContext() // context
				, getResources().getString(R.string.speed_graph_title) // heading
		);

		speedGraphView.addSeries(speedSeries); // data
		altitudeGraphView.addSeries(altitudeSeries); // data

		speedGraphView.setCustomLabelFormatter(speedGraphLabelsFormatter());
		speedGraphView.getGraphViewStyle().setNumVerticalLabels(5);
		speedGraphView.getGraphViewStyle().setNumHorizontalLabels(3);

		altitudeGraphView
				.setCustomLabelFormatter(altitudeGraphLabelFormatter());
		altitudeGraphView.getGraphViewStyle().setNumVerticalLabels(5);
		altitudeGraphView.getGraphViewStyle().setNumHorizontalLabels(3);
		((LineGraphView) altitudeGraphView).setDrawBackground(true);

		/*
		 * Useful funcs // set view port, start=2, size=10
		 * graphView.setViewPort(2, 10); graphView.setScalable(true); // set
		 * manual Y axis bounds graphView.setManualYAxisBounds(2, -1);
		 */

		LinearLayout layout1 = (LinearLayout) rootView
				.findViewById(R.id.graph1);
		LinearLayout layout2 = (LinearLayout) rootView
				.findViewById(R.id.graph2);
		layout1.addView(altitudeGraphView);
		layout2.addView(speedGraphView);
	}

	private CustomLabelFormatter speedGraphLabelsFormatter() {
		final SimpleDateFormat dateFormat = new SimpleDateFormat("kk:mm:ss", Locale.FRANCE);

		return new CustomLabelFormatter() {
			@Override
			public String formatLabel(double value, boolean isValueX) {
				if (!isValueX) {
					return (double)Math.round(value * 100) / 100 + "km/h";
				} else {

					Date d = new Date((long) value);
					return dateFormat.format(d);
				} 
			}
		};
	}

	private CustomLabelFormatter altitudeGraphLabelFormatter() {
		final SimpleDateFormat dateFormat = new SimpleDateFormat("kk:mm:ss", Locale.FRANCE);

		return new CustomLabelFormatter() {
			@Override
			public String formatLabel(double value, boolean isValueX) {
				if (!isValueX) {
					return (double)Math.round(value * 100) / 100 + "m";
				} else {

					Date d = new Date((long) value);
					return dateFormat.format(d);
				} 
			}
		};
	}
}