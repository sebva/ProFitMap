package ch.hearc.profitmap.model;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.util.Log;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxAccountManager.AccountListener;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxDatastore.SyncStatusListener;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;

public class DropboxManager implements AccountListener, SyncStatusListener
{
	private static DropboxManager instance = null;
	private DbxAccountManager mDbxAcctMgr = null;
	private DbxDatastore mStore;
	private DbxFileSystem mFs;
	
	private static final String TRACKS_TABLE = "tracks";
	private static final String TAG = "DropboxManager";
	
	private Set<DropboxReadyListener> listeners;

	public interface DropboxReadyListener
	{
		public void onDropboxReady();
	}
	
	private DropboxManager()
	{
		listeners = new HashSet<DropboxReadyListener>();
	}

	public static synchronized DropboxManager getInstance()
	{
		if (instance == null)
			instance = new DropboxManager();

		return instance;
	}
	
	public DbxTable getTable(int tracksId)
	{
		return mStore.getTable(TRACKS_TABLE + tracksId);
	}
	
	public DbxFileSystem getFilesystem()
	{
		return mFs;
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
		else
			open(mDbxAcctMgr.getLinkedAccount());
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
	
	private void open(DbxAccount dbxAccount)
	{
		Log.d(TAG, "Linked to Dropbox");
		try
		{
			mStore = DbxDatastore.openDefault(dbxAccount);
			mStore.addSyncStatusListener(this);

			mFs = DbxFileSystem.forAccount(dbxAccount);
			
			for(DropboxReadyListener listener : listeners)
				if(listener != null)
					listener.onDropboxReady();
		}
		catch (DbxException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onLinkedAccountChange(DbxAccountManager dbxAccountManager, DbxAccount dbxAccount)
	{
		if (dbxAccountManager.hasLinkedAccount())
			open(dbxAccount);
	}

	@Override
	public void onDatastoreStatusChange(DbxDatastore dbxDatastore)
	{
		if (dbxDatastore.getSyncStatus().hasIncoming)
		{
			try
			{
				Map<String, Set<DbxRecord>> changes = mStore.sync();
				
			}
			catch (DbxException e)
			{
				// Handle exception
			}
		}
	}
	
	public synchronized void addListener(DropboxReadyListener listener)
	{
		listeners.add(listener);
	}
	
	public synchronized void removeListener(DropboxReadyListener listener)
	{
		listeners.remove(listener);
	}
}
