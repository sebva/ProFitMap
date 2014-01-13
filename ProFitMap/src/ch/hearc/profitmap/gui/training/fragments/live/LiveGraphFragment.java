package ch.hearc.profitmap.gui.training.fragments.live;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ch.hearc.profitmap.R;
import ch.hearc.profitmap.gui.training.fragments.GraphFragment;

import com.jjoe64.graphview.GraphView.GraphViewData;

public class LiveGraphFragment extends GraphFragment
{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		inflater.inflate(R.layout.fragment_graph,	container, false);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public void refreshGraphs() {

		GraphViewData last10Altitude[] = new GraphViewData[] {
			new GraphViewData(1,0d),
			new GraphViewData(1,0d),
			new GraphViewData(1,0d),
			new GraphViewData(1,0d),
			new GraphViewData(1,0d),
			new GraphViewData(1,0d),
			new GraphViewData(1,0d),
			new GraphViewData(1,0d),
			new GraphViewData(1,0d),
			new GraphViewData(1,0d)
		};
		GraphViewData last10Speed[] = new GraphViewData[] {
			new GraphViewData(1,0d),
			new GraphViewData(1,0d),
			new GraphViewData(1,0d),
			new GraphViewData(1,0d),
			new GraphViewData(1,0d),
			new GraphViewData(1,0d),
			new GraphViewData(1,0d),
			new GraphViewData(1,0d),
			new GraphViewData(1,0d),
			new GraphViewData(1,0d)
		};
		
		insertLastSpeedsAndAltitudes(last10Altitude, last10Speed);

		speedSeries.resetData(last10Speed);
		altitudeSeries.resetData(last10Altitude);
	}

	private void insertLastSpeedsAndAltitudes(GraphViewData[] last10Altitude,
			GraphViewData[] last10Speed) {
		int totalPoints = mTrackInstance.getWaypoints().size();
		int firstPoint = ((totalPoints-10 < 0) ? 0 : totalPoints-10);

		for (int i = firstPoint,j=0; i < totalPoints; i++,j++) {
			last10Altitude[j] = new GraphViewData(mTrackInstance.getWaypoints().get(i).getTime(),mTrackInstance.getWaypoints().get(i).getAltitude());
			last10Speed[j] = new GraphViewData(mTrackInstance.getWaypoints().get(i).getTime(),mTrackInstance.getWaypoints().get(i).getSpeed());
		}
	}

}
