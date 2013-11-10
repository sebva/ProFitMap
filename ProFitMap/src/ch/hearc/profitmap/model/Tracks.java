package ch.hearc.profitmap.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxAccountManager.AccountListener;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxDatastore.SyncStatusListener;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxFileSystem.PathListener;
import com.dropbox.sync.android.DbxPath;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;

public class Tracks extends BaseAdapter implements AccountListener, PathListener, SyncStatusListener
{

	private static final String TRACKS_TABLE = "tracks";

	public interface TrackListUpdateListener
	{
		public void onTrackListUpdated(Set<Track> tracks);
	}

	private static final String TAG = "Tracks";
	private List<Track> tracks;
	private static Tracks instance = null;
	private DbxAccountManager mDbxAcctMgr = null;
	private DbxDatastore mStore;
	private DbxFileSystem mFs;
	private DbxTable mTracksTable;

	private Tracks()
	{
	}

	public static synchronized Tracks getInstance()
	{
		if (instance == null)
			instance = new Tracks();

		return instance;
	}

	public void linkToDropbox(Activity activity, int callbackTag)
	{
		if (mDbxAcctMgr == null)
		{
			mDbxAcctMgr = DbxAccountManager.getInstance(activity.getApplicationContext(), "q2sr7uxe7l3b38n", "4byz83rhp7lt0ou");
			mDbxAcctMgr.addListener(this);
		}
		if (!mDbxAcctMgr.hasLinkedAccount())
			mDbxAcctMgr.startLink(activity, callbackTag);
	}

	/**
	 * Unlink Dropbox account. Remember to immediately call linkToDropbox.
	 */
	public void unlinkDropbox()
	{
		mDbxAcctMgr.unlink();
	}

	public boolean isDropboxLinked()
	{
		if (mDbxAcctMgr == null)
			return false;

		return mDbxAcctMgr.hasLinkedAccount();
	}

	public void addTrack(Track track)
	{
		tracks.add(track);
	}
	
	public List<Track> getTracks()
	{
		return tracks;
	}

	@Override
	public void onLinkedAccountChange(DbxAccountManager dbxAccountManager, DbxAccount dbxAccount)
	{
		if (dbxAccountManager.hasLinkedAccount())
		{
			Log.d(TAG, "Linked to Dropbox");
			try
			{
				mStore = DbxDatastore.openDefault(dbxAccount);
				mStore.addSyncStatusListener(this);
				mTracksTable = mStore.getTable(TRACKS_TABLE);

				mFs = DbxFileSystem.forAccount(dbxAccount);
				mFs.addPathListener(this, DbxPath.ROOT, Mode.PATH_OR_DESCENDANT);
			}
			catch (DbxException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onPathChange(DbxFileSystem dbxFs, DbxPath dbxPath, Mode dbxMode)
	{
		// Called on UI thread !

	}

	@Override
	public void onDatastoreStatusChange(DbxDatastore dbxDatastore)
	{
		if (dbxDatastore.getSyncStatus().hasIncoming)
		{
			try
			{
				Map<String, Set<DbxRecord>> changes = mStore.sync();
				for (DbxRecord change : changes.get(TRACKS_TABLE))
				{
					//if(change.isDeleted())
				}
			}
			catch (DbxException e)
			{
				// Handle exception
			}
		}
	}

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
		return tracks.get(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		return null;
	}

}
