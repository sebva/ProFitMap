package ch.hearc.profitmap.model;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

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

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFields;
import com.dropbox.sync.android.DbxList;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;
import com.dropbox.sync.android.DbxTable.QueryResult;

public class Statistics {
	/**
	 * Length in meters
	 */
	private double length;
	private double ascent;
	private double descent;
	private double averageSpeed;
	private float maxSpeed;
	private double effortKm;
	/**
	 * Duration in seconds
	 */
	private long duration;
	private TrackInstance trackInstance;
	private transient Location lastLocation = null;

	public enum TypeStatistics {
		LIVE, END, SUMMARY, LIVE_GHOST;

		public int[] getShownStats() {
			switch (this) {
			/*
			 * case LIVE_GHOST: break;
			 */
			case END:
				return new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11 };
			case LIVE:
				return new int[] { 0, 1, 2, 3, 4, 6, 7, 8, 9, 11, 12 };
			default:
			case SUMMARY:
				return new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
			}
		}
	}

	public Statistics(TrackInstance track) {
		this.trackInstance = track;
	}

	@SuppressLint("NewApi")
	public void computeStatistics() {
		length = ascent = descent = averageSpeed = maxSpeed = duration = 0;
		if (trackInstance.getWaypoints().size() == 0)
			return;

		Location previous = trackInstance.getWaypoints().get(0);
		for (Location l : trackInstance.getWaypoints()) {
			length += previous.distanceTo(l);
			if(l.hasAltitude() && previous.hasAltitude())
			{
				double deniv = l.getAltitude() - previous.getAltitude();
				if (deniv >= 0)
					ascent += deniv;
				else
					descent -= deniv;
			}
			
			float speed = l.getSpeed();
			if (speed > maxSpeed)
				maxSpeed = speed;

			previous = l;
		}
		
		// Better precision, but only since Jelly Bean
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && previous.getElapsedRealtimeNanos() != 0l)
			duration = (previous.getElapsedRealtimeNanos() - trackInstance
					.getWaypoints().get(0).getElapsedRealtimeNanos()) / 1000000000l;
		else
			duration = (previous.getTime() - trackInstance.getWaypoints()
					.get(0).getTime()) / 1000l;
		
		averageSpeed = length / (double) duration;
		effortKm = (length + ascent * 10.0 + descent * 2.0) / 1000.0;
	}

	@SuppressLint("NewApi")
	void addLocation(Location l) {
		if (lastLocation != null) {
			length += lastLocation.distanceTo(l);
			if(l.hasAltitude() && lastLocation.hasAltitude())
			{
				double deniv = l.getAltitude() - lastLocation.getAltitude();
				if (deniv >= 0)
					ascent += deniv;
				else
					descent -= deniv;
			}

			if (l.getSpeed() > maxSpeed)
				maxSpeed = l.getSpeed();

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && l.getElapsedRealtimeNanos() != 0l)
				duration += (l.getElapsedRealtimeNanos() - lastLocation
						.getElapsedRealtimeNanos()) / 1000000000l;
			else
				duration += (l.getTime() - lastLocation.getTime()) / 1000l;

		} else
			maxSpeed = l.getSpeed();

		averageSpeed = length / (double) duration;
		effortKm = (length + ascent * 10.0 + descent * 2.0) / 1000.0;
		lastLocation = l;
	}

	private Pair<Integer, String> getStatisticForPosition(int position) {
		NumberFormat format = DecimalFormat.getNumberInstance();
		switch (position) {
		default:
		case 0:
			format.setMaximumFractionDigits(3);
			return new Pair<Integer, String>(R.string.track_length,
					format.format(length / 1000.0) + " km");
		case 1:
			return new Pair<Integer, String>(R.string.track_duration,
					DateUtils.formatElapsedTime(duration - trackInstance.getTotalPauseTime()));
		case 2:
			return new Pair<Integer, String>(R.string.track_ascent,
					Math.round(ascent) + " m");
		case 3:
			return new Pair<Integer, String>(R.string.track_descent,
					Math.round(descent) + " m");
		case 4:
			format.setMaximumFractionDigits(1);
			return new Pair<Integer, String>(R.string.track_average_speed,
					format.format(averageSpeed * 3.6) + " km/h");
		case 5:
			return new Pair<Integer, String>(R.string.track_end_time,
					DateFormat.getDateTimeInstance().format(
							trackInstance.getTimestampEnd()));
		case 6:
			return new Pair<Integer, String>(R.string.track_start_time,
					DateFormat.getDateTimeInstance().format(
							trackInstance.getTimestampStart()));
		case 7:
			format.setMaximumFractionDigits(1);
			return new Pair<Integer, String>(R.string.track_max_speed,
					format.format(maxSpeed * 3.6) + " km/h");
		case 8:
			return new Pair<Integer, String>(R.string.track_number_of_pauses,
					String.valueOf(trackInstance.getNumberOfPauses()));
		case 9:
			return new Pair<Integer, String>(R.string.track_total_pause_time,
					DateUtils.formatElapsedTime(trackInstance
							.getTotalPauseTime()));
		case 10:
			return new Pair<Integer, String>(R.string.rating,
					trackInstance.getRating() + " / 5");
		case 11:
			format.setMaximumFractionDigits(3);
			return new Pair<Integer, String>(R.string.track_km_effort,
					format.format(effortKm) + " km");
		case 12:
			format.setMaximumFractionDigits(1);
			return new Pair<Integer, String>(R.string.track_current_speed,
					format.format((lastLocation != null) ? lastLocation.getSpeed() * 3.6 : 0.0) + " km/h");
		}
	}

	public ListAdapter getAdapter(final Context c, final TypeStatistics typeStatistics, final int sportId)
	{
		DbxTable statsOrderTable = DropboxManager.getInstance().getStatsOrderTable();
		final List<Integer> userOrder;
		try
		{
			QueryResult result = statsOrderTable.query(new DbxFields().set("sport", sportId));
			if (result.hasResults())
			{
				DbxRecord record = result.iterator().next();
				DbxList list = record.getList("order");
				if(list.size() != 13) // Upgrade number when new tiles are added
				{
					userOrder = null;
					record.deleteRecord();
				}
				else
				{
					userOrder = new ArrayList<Integer>(list.size());
					for (int i = 0; i < list.size(); i++)
						userOrder.add((int) list.getLong(i));
				}
			}
			else
				userOrder = null;

			final int[] shownStats = typeStatistics.getShownStats();
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

					int stat = (userOrder == null) ? shownStats[position] : userOrder.get(shownStats[position]);
					Pair<Integer, String> pair = getStatisticForPosition(stat);
					TextView value = (TextView) v.findViewById(R.id.stat_tile_value_text);
					value.setText(pair.second);
					TextView detail = (TextView) v.findViewById(R.id.stat_tile_detail_text);
					detail.setText(pair.first);
					return v;
				}

				@Override
				public long getItemId(int position)
				{
					return position;
				}

				@Override
				public Object getItem(int position)
				{
					return null;
				}

				@Override
				public int getCount()
				{
					return shownStats.length;
				}
			};
		}
		catch (DbxException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String toString() {
		return "Statistics [length=" + length + ", ascent=" + ascent
				+ ", descent=" + descent + ", averageSpeed=" + averageSpeed
				+ ", maxSpeed=" + maxSpeed + ", effortKm=" + effortKm
				+ ", duration=" + duration + ", trackInstance=" + trackInstance
				+ "]";
	}

}
