package ch.hearc.profitmap.model;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import android.content.Context;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import ch.hearc.profitmap.R;
import ch.hearc.profitmap.gui.DropboxImageView;
import ch.hearc.profitmap.model.DropboxManager.DropboxLinkedListener;
import ch.hearc.profitmap.model.DropboxManager.DropboxListener;

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxList;
import com.dropbox.sync.android.DbxPath;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;
import com.dropbox.sync.android.DbxTable.QueryResult;
import com.google.gson.Gson;

public class Tracks implements DropboxListener, DropboxLinkedListener
{
	public interface TrackListUpdateListener
	{
		public void onTrackListUpdated();
	}

	private Map<String, Track> tracks;
	private static SparseArray<Tracks> instances = null;
	private DropboxManager mDropbox;
	private DbxTable mDbxTable;
	private DbxFileSystem mDbxFs;
	private int mSportId;
	private TrackListUpdateListener listener = null;
	private Semaphore semaphore = new Semaphore(1);
	private List<Track> sortedTracks;
	private Comparator<Track> comparator;
	
	private static final DbxPath kDbxRoot = new DbxPath("/tracks");
	public static TrackInstance currentTrackInstance;
	
	static
	{
		instances = new SparseArray<Tracks>();
	}

	private Tracks(int sportId)
	{
		this.mSportId = sportId;
		
		tracks = new HashMap<String, Track>();
		sortedTracks = new ArrayList<Track>(0);
		mDropbox = DropboxManager.getInstance();
		mDropbox.addLinkedListener(this);
	}

	public static synchronized Tracks getInstance(int sport)
	{
		Tracks tracks = instances.get(sport, null);
		if(tracks == null)
		{
			tracks = new Tracks(sport);
			instances.put(sport, tracks);
		}
		return tracks;
	}
	
	public void sortTracks(Comparator<Track> comparator)
	{
		this.comparator = comparator;
		syncToSorted();
	}
	
	public void addTrack(Track track)
	{
		track.setTracks(this);
		
		DbxRecord record = mDbxTable.insert().set("name", track.getName()).set("instances", new DbxList());
		tracks.put(record.getId(), track);
		track.setDropboxId(record.getId());
		
		syncToSorted();
		
		for(TrackInstance ti : track.getTrackInstances())
			saveTrackInstanceToDropbox(track, ti, record);
	}
	
	public ListAdapter getAdapter(final Context c)
	{
		return new BaseAdapter()
		{
			@Override
			public int getCount()
			{
				return sortedTracks.size();
			}

			@Override
			public Object getItem(int position)
			{
				return sortedTracks.get(position);
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
				Track track = sortedTracks.get(position);
				View v;
		        if (convertView == null) {  // if it's not recycled, initialize some attributes
		            v = inflater.inflate(R.layout.tile, null);
		        } else {
		            v = convertView;
		        }
		        
		        TextView tv = (TextView)v.findViewById(R.id.textView);
		        String trackName = track.getName();
		        if(!"".equals(trackName))
		        	tv.setText(trackName);
		        else
		        	tv.setText(DateFormat.getDateTimeInstance().format(track.getTrackInstance(0).getTimestampStart()));
		        
		        DropboxImageView iv = (DropboxImageView) v.findViewById(R.id.imageView);
		        List<TrackInstance> ti = track.getTrackInstances();
		        if(ti != null)
				{
		        	String path = null;
					for(TrackInstance trackInstance : ti)
						if(trackInstance != null && trackInstance.getThumbnail() != null)
							path = trackInstance.getThumbnail();
					
					if(path != null)
						iv.loadImageFromDropbox(new DbxPath(path), mDbxFs);
				}
		        
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

	public Track getTrack(String id)
	{
		return tracks.get(id);
	}
	
	public Track getTrack(int position)
	{
		return sortedTracks.get(position);
	}
	
	public void setListener(TrackListUpdateListener listener)
	{
		this.listener = listener;
		if(listener != null)
			listener.onTrackListUpdated();
	}

	@Override
	public void onDropboxChange()
	{
		new AsyncTask<Void, Void, Void>()
		{
			
			@Override
			protected Void doInBackground(Void... params)
			{
				try
				{
					semaphore.acquire();
					Gson gson = new Gson();
	
					QueryResult queryResult = null;
					try
					{
						queryResult = mDbxTable.query();
						tracks.clear();
						for (DbxRecord record : queryResult)
						{
							Track track = new Track();
							track.setTracks(Tracks.this);
							track.setName(record.getString("name"));
	
							DbxList list = record.getList("instances");
							for (int i = 0; i < list.size(); i++)
							{
								try
								{
									DbxPath path = new DbxPath(kDbxRoot, list.getString(i));
									TrackInstance trackInstance;
									if (mDbxFs.exists(path))
									{
										DbxFile file = mDbxFs.open(path);
										Reader reader = new InputStreamReader(file.getReadStream());
										trackInstance = gson.fromJson(reader, TrackInstance.class);
										reader.close();
										file.close();
									}
									else
										trackInstance = new TrackInstance();
									
									track.trackInstances.add(trackInstance);
								}
								catch (DbxException e)
								{
									e.printStackTrace();
								}
								catch (IOException e)
								{
									e.printStackTrace();
								}
							}
	
							tracks.put(record.getId(), track);
						}
					}
					catch (DbxException e)
					{
						e.printStackTrace();
					}
					finally
					{
						semaphore.release();
					}
				}
				catch(InterruptedException e)
				{
					
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result)
			{
				syncToSorted();
			}
		}.execute();
	}
	
	private void syncToSorted()
	{
		sortedTracks.clear();
		sortedTracks.addAll(tracks.values());
		Collections.sort(sortedTracks, comparator);
		if(listener != null)
			listener.onTrackListUpdated();
	}
	
	void saveTrackInstanceToDropbox(Track track, TrackInstance trackInstance, String mDropboxId)
	{
		try
		{
			saveTrackInstanceToDropbox(track, trackInstance, mDbxTable.get(mDropboxId));
		}
		catch (DbxException e)
		{
			e.printStackTrace();
		}
	}

	void saveTrackInstanceToDropbox(Track track, TrackInstance trackInstance, DbxRecord record)
	{
		try
		{
			String uuid = UUID.randomUUID().toString();
			DbxFile file = mDbxFs.create(new DbxPath(kDbxRoot, uuid));
			record.getList("instances").add(uuid);
			
			PrintWriter writer = new PrintWriter(file.getWriteStream());
			//LogWriter writer = new LogWriter("Gson");
			new Gson().toJson(trackInstance, writer);
			
			writer.close();
			file.close();
			mDropbox.onDatastoreStatusChange(mDbxTable.getDatastore());
		}
		catch (DbxException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onAccountLinked()
	{
		mDbxFs = mDropbox.getFilesystem();
		mDbxTable = mDropbox.getTable(mSportId);
		mDropbox.addListener(this);
	}

}
