package ch.hearc.profitmap.model;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Build;
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
	private double effortKm;
	/**
	 * Duration in seconds
	 */
	private long duration;
	private TrackInstance trackInstance;
	

	public Statistics(TrackInstance track)
	{
		this.trackInstance = track;
	}

	@SuppressLint("NewApi")
	public void computeStatistics()
	{
		length = ascent = descent = averageSpeed = maxSpeed = duration = 0;
		if(trackInstance.getWaypoints().size() == 0)
			return;
		
		Location previous = trackInstance.getWaypoints().get(0);
		for(Location l : trackInstance.getWaypoints())
		{
			length += previous.distanceTo(l);
			double deniv = l.getAltitude() - previous.getAltitude();
			if(deniv >= 0)
				ascent += deniv;
			else
				descent -= deniv;
			
			float speed = l.getSpeed();
			if(speed > maxSpeed)
				maxSpeed = speed;
			
			previous = l;
		}
		
		// Better precision, but only since Jelly Bean
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
			duration = (previous.getElapsedRealtimeNanos() - trackInstance.getWaypoints().get(0).getElapsedRealtimeNanos()) / 1000000l;
		else
			duration = (previous.getTime() - trackInstance.getWaypoints().get(0).getTime()) / 1000l;
		
		effortKm = (length + ascent * 10.0 + descent * 2.0) / 1000.0;
	}
	
	private Pair<Integer, String> getStatisticForPosition(int position)
	{
		NumberFormat format = DecimalFormat.getNumberInstance();
		switch(position)
		{
			default:
			case 0:
				format.setMaximumIntegerDigits(3);
				return new Pair<Integer, String>(R.string.track_length, format.format(length / 1000.0) + " km");
			case 1:
				return new Pair<Integer, String>(R.string.track_duration, DateUtils.formatElapsedTime(duration));
			case 2:
				return new Pair<Integer, String>(R.string.track_ascent, Math.round(ascent) + " m");
			case 3:
				return new Pair<Integer, String>(R.string.track_descent, Math.round(descent) + " m");
			case 4:
				format.setMaximumIntegerDigits(1);
				return new Pair<Integer, String>(R.string.track_average_speed, format.format(averageSpeed * 3.6) + " km/h");
			case 5:
				return new Pair<Integer, String>(R.string.track_end_time, DateFormat.getDateTimeInstance().format(trackInstance.getTimestampEnd()));
			case 6:
				return new Pair<Integer, String>(R.string.track_start_time, DateFormat.getDateTimeInstance().format(trackInstance.getTimestampStart()));
			case 7:
				format.setMaximumIntegerDigits(1);
				return new Pair<Integer, String>(R.string.track_max_speed, format.format(maxSpeed * 3.6) + " km/h");
			case 8:
				return new Pair<Integer, String>(R.string.track_number_of_pauses, String.valueOf(trackInstance.getNumberOfPauses()));
			case 9:
				return new Pair<Integer, String>(R.string.track_total_pause_time, DateUtils.formatElapsedTime(trackInstance.getTotalPauseTime()));
			case 10:
				return new Pair<Integer, String>(R.string.rating, trackInstance.getRating() + " / 5");
			case 11:
				format.setMaximumIntegerDigits(3);
				return new Pair<Integer, String>(R.string.track_km_effort, format.format(effortKm) + " km");
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
				return 12;
			}
		};
	}

}
