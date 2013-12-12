package ch.hearc.profitmap.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import ch.hearc.profitmap.R;
import ch.hearc.profitmap.model.DropboxManager.DropboxReadyListener;

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxList;
import com.dropbox.sync.android.DbxPath;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;
import com.dropbox.sync.android.DbxTable.QueryResult;
import com.google.gson.Gson;

public class Tracks implements DropboxReadyListener
{
	public interface TrackListUpdateListener
	{
		public void onTrackListUpdated(Set<Track> tracks);
	}

	private static final String TAG = "Tracks";
	private List<Track> tracks;
	private static Map<Integer, Tracks> instances = null;
	private DropboxManager mDropbox;
	private DbxTable mDbxTable;
	private DbxFileSystem mDbxFs;
	private int mSportId;
	
	static
	{
		instances = new HashMap<Integer, Tracks>();
	}

	private Tracks(int sportId)
	{
		this.mSportId = sportId;
		
		tracks = new ArrayList<Track>();
		mDropbox = DropboxManager.getInstance();
		mDropbox.addListener(this);
	}

	public static synchronized Tracks getInstance(int sport)
	{
		if(instances.containsKey(sport))
			return instances.get(sport);
		else
		{
			Tracks tracks = new Tracks(sport);
			instances.put(sport, tracks);
			return tracks;
		}
	}

	
	public void addTrack(Track track)
	{
		tracks.add(track);
	}
	
	public List<Track> getTracks()
	{
		return tracks;
	}
	
	public ListAdapter getAdapter(final Context c)
	{
		return new BaseAdapter()
		{
			@Override
			public int getCount()
			{
				return tracks.size();
			}

			@Override
			public Object getItem(int position)
			{
				return tracks.get(position);
			}

			@Override
			public long getItemId(int position)
			{
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				LayoutInflater inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				Track track = tracks.get(position);
				View v;
		        if (convertView == null) {  // if it's not recycled, initialize some attributes
		            v = inflater.inflate(R.layout.tile, null);
		        } else {
		            v = convertView;
		        }
		        
		        TextView tv = (TextView)v.findViewById(R.id.textView);
		        tv.setText(track.getName());
		        
		        TextView count = (TextView)v.findViewById(R.id.count);
		        if(!track.isSingleInstance())
		        {
		        	count.setVisibility(View.VISIBLE);
		        	count.setText(String.valueOf(track.getTrackInstances().size()));
		        }
		        else
		        	count.setVisibility(View.INVISIBLE);
		        return v;
			}
		};
	}

	public Track getTrack(int position)
	{
		return tracks.get(position);
	}

	@Override
	public void onDropboxReady()
	{
		mDbxTable = mDropbox.getTable(mSportId);
		mDbxFs = mDropbox.getFilesystem();
		Gson gson = new Gson();
		
		try
		{
			QueryResult queryResult = mDbxTable.query();
			for(DbxRecord record : queryResult)
			{
				Track track = new Track();
				track.setName(record.getString("name"));
				
				DbxList list = record.getList("instances");
				for(int i = 0; i < list.size(); i++)
				{
					DbxFile file = mDbxFs.open(new DbxPath(list.getString(i)));
					TrackInstance trackInstance = gson.fromJson(file.readString(), TrackInstance.class);
					track.trackInstances.add(trackInstance);
				}
				
				tracks.add(track);
			}
		}
		catch (DbxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

}
