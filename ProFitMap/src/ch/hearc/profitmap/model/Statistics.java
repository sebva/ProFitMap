package ch.hearc.profitmap.model;

import java.text.DateFormat;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import ch.hearc.profitmap.R;

public class Statistics
{
	/**
	 * Length in meters
	 */
	private double length;
	private double ascent;
	private double descent;
	private double averageSpeed;
	private double maxSpeed;
	/**
	 * Duration in seconds
	 */
	private long duration;
	private TrackInstance trackInstance;
	

	public Statistics(TrackInstance track)
	{
		this.trackInstance = track;
	}

	private void computeStatistics()
	{

	}
	
	private Pair<Integer, String> getStatisticForPosition(int position)
	{
		switch(position)
		{
			default:
			case 0:
				return new Pair<Integer, String>(R.string.track_length, length / 1000.0 + " km");
			case 1:
				return new Pair<Integer, String>(R.string.track_duration, DateUtils.formatElapsedTime(duration));
			case 2:
				return new Pair<Integer, String>(R.string.track_ascent, ascent + " m");
			case 3:
				return new Pair<Integer, String>(R.string.track_descent, descent + " m");
			case 4:
				return new Pair<Integer, String>(R.string.track_average_speed, averageSpeed * 3.6 + " km/h");
			case 5:
				return new Pair<Integer, String>(R.string.track_end_time, DateFormat.getDateTimeInstance().format(trackInstance.getTimestampEnd()));
			case 6:
				return new Pair<Integer, String>(R.string.track_start_time, DateFormat.getDateTimeInstance().format(trackInstance.getTimestampStart()));
			case 7:
				return new Pair<Integer, String>(R.string.track_max_speed, maxSpeed * 3.6 + " km/h");
			case 8:
				return new Pair<Integer, String>(R.string.track_number_of_pauses, String.valueOf(trackInstance.getNumberOfPauses()));
			case 9:
				return new Pair<Integer, String>(R.string.track_total_pause_time, DateUtils.formatElapsedTime(trackInstance.getTotalPauseTime()));
			case 10:
				return new Pair<Integer, String>(R.string.rating, trackInstance.getRating() + " / 5");
		}
	}

	public ListAdapter getAdapter(final Context c)
	{
		return new BaseAdapter()
		{

			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				View v;
				if (convertView == null)
					// if it's not recycled, initialize some attributes
					v = inflater.inflate(R.layout.stat_tile, null);
				else
					v = convertView;

				Pair<Integer, String> pair = getStatisticForPosition(position);
				TextView value = (TextView) v.findViewById(R.id.stat_tile_value_text);
				value.setText(pair.second);
				TextView detail = (TextView) v.findViewById(R.id.stat_tile_detail_text);
				detail.setText(pair.first);
				return v;
			}

			@Override
			public long getItemId(int position)
			{
				return 0;
			}

			@Override
			public Object getItem(int position)
			{
				return null;
			}

			@Override
			public int getCount()
			{
				return 11;
			}
		};
	}

}
