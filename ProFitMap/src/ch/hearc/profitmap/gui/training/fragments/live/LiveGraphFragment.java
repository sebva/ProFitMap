package ch.hearc.profitmap.gui.training.fragments.live;

import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphView.GraphViewData;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ch.hearc.profitmap.R;
import ch.hearc.profitmap.gui.training.fragments.GraphFragment;

public class LiveGraphFragment extends GraphFragment {

	private View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_live_training_graph,
				container, false);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public void refreshGraphs() {
		System.out.println("ref graph");

		GraphViewData last5Altitude[] = new GraphViewData[] {
			new GraphViewData(1,0d),
			new GraphViewData(1,0d),
			new GraphViewData(1,0d),
			new GraphViewData(1,0d),
			new GraphViewData(1,0d),
		};
		GraphViewData last5Speed[] = new GraphViewData[] {
			new GraphViewData(1,0d),
			new GraphViewData(1,0d),
			new GraphViewData(1,0d),
			new GraphViewData(1,0d),
			new GraphViewData(1,0d),
		};
		
		insertLastSpeedsAndAltitudes(last5Altitude, last5Speed);

		speedSeries.resetData(last5Speed);
		altitudeSeries.resetData(last5Altitude);
	}

	private void insertLastSpeedsAndAltitudes(GraphViewData[] last5Altitude,
			GraphViewData[] last5Speed) {
		int totalPoints = mTrackInstance.getWaypoints().size();
		int firstPoint = ((totalPoints-5 < 0) ? 0 : totalPoints-5);

		for (int i = firstPoint,j=0; i < totalPoints; i++,j++) {
			last5Altitude[j] = new GraphViewData(i,mTrackInstance.getWaypoints().get(i).getAltitude());
			last5Speed[j] = new GraphViewData(i,mTrackInstance.getWaypoints().get(i).getSpeed());
		}
	}

}
