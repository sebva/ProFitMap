package ch.hearc.profitmap.model;

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
import com.dropbox.sync.android.DbxFileSystem.PathListener;
import com.dropbox.sync.android.DbxPath;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;

public class DropboxManager implements AccountListener, PathListener, SyncStatusListener
{
	private static DropboxManager instance = null;
	private DbxAccountManager mDbxAcctMgr = null;
	private DbxDatastore mStore;
	private DbxFileSystem mFs;
	private DbxTable mTracksTable;
	
	private static final String TRACKS_TABLE = "tracks";
	private static final String TAG = "DropboxManager";

	private DropboxManager()
	{
	}

	public static synchronized DropboxManager getInstance()
	{
		if (instance == null)
			instance = new DropboxManager();

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
					// if(change.isDeleted())
				}
			}
			catch (DbxException e)
			{
				// Handle exception
			}
		}
	}
}
