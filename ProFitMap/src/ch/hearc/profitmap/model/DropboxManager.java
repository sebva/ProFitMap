package ch.hearc.profitmap.model;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxAccountManager.AccountListener;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxDatastore.SyncStatusListener;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxFileSystem.PathListener;
import com.dropbox.sync.android.DbxPath;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;

public class DropboxManager implements AccountListener, SyncStatusListener, PathListener
{

	private static DropboxManager instance = null;
	private DbxAccountManager mDbxAcctMgr = null;
	private DbxDatastore mStore;
	private DbxFileSystem mFs;
	
	private static final String TRACKS_TABLE = "tracks";
	private static final String TAG = "DropboxManager";
	
	private Set<DropboxListener> listeners;
	private Set<DropboxLinkedListener> linkedListeners;
	
	public interface DropboxLinkedListener
	{
		public void onAccountLinked();
	}
	public interface DropboxListener
	{
		public void onDropboxChange();
	}
	
	private DropboxManager()
	{
		listeners = new HashSet<DropboxListener>();
		linkedListeners = new HashSet<DropboxLinkedListener>();
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
		Thread thread = new Thread()
		{
			@Override
			public void run()
			{
				mDbxAcctMgr.unlink();
			}
		};
		thread.start();
		try
		{
			thread.join();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
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
			mFs.addPathListener(this, new DbxPath(DbxPath.ROOT + "tracks"), Mode.PATH_OR_CHILD);
			
			notifyLinkedListeners();
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
		Log.i(TAG, "onDataStoreStatusChange()");
		try
		{
			mStore.sync();
			notifyListeners();
		}
		catch (DbxException e)
		{
			// Handle exception
		}
	}

	private synchronized void notifyListeners()
	{
		Log.i(TAG, "notifyListeners()");
		for(DropboxListener listener : listeners)
			if(listener != null)
				listener.onDropboxChange();
	}
	
	private synchronized void notifyLinkedListeners()
	{
		Log.i(TAG, "notifyLinkedListeners()");
		for(DropboxLinkedListener listener : linkedListeners)
			if(listener != null)
				listener.onAccountLinked();
	}
	
	public synchronized void addListener(DropboxListener listener)
	{
		listeners.add(listener);
		if(mDbxAcctMgr != null && mDbxAcctMgr.hasLinkedAccount())
			listener.onDropboxChange();
	}
	
	public synchronized void addLinkedListener(DropboxLinkedListener listener)
	{
		linkedListeners.add(listener);
		if(mDbxAcctMgr != null && mDbxAcctMgr.hasLinkedAccount())
			listener.onAccountLinked();
	}
	
	public synchronized void removeLinkedListener(DropboxLinkedListener listener)
	{
		linkedListeners.remove(listener);
	}
	
	public synchronized void removeListener(DropboxListener listener)
	{
		listeners.remove(listener);
	}

	public String copyPictureToDropbox(Context context, Uri srcUri)
	{
		DbxFile file = null;
		try
		{
			DbxPath rootPath = new DbxPath(DbxPath.ROOT, "pictures");
			try
			{
				mFs.createFolder(rootPath);
			}
			catch(DbxException.Exists e)
			{}
			
			List<String> pathSegments = srcUri.getPathSegments();
			String dstPath = pathSegments.get(pathSegments.size() - 1);
			file = mFs.create(new DbxPath(rootPath, dstPath));
			
			final int bufSize = 2048;
			byte[] buffer = new byte[bufSize];
			InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
			FileOutputStream writeStream = file.getWriteStream();
			while(inputStream.available() > 0)
			{
				inputStream.read(buffer);
				writeStream.write(buffer);
			}
			inputStream.close();
			writeStream.close();
			
			String path = file.getPath().toString();
			file.close();
			return path;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void onPathChange(DbxFileSystem fs, DbxPath path, Mode mode)
	{
		Log.i(TAG, "onPathChange()");
		notifyListeners();
	}
	
	/**
	 * Useful for debug purposes
	 */
	@SuppressWarnings("unused")
	private void emptyDatastore() throws Exception
	{
		for(DbxTable table : mStore.getTables())
			for(DbxRecord record : table.query())
				record.deleteRecord();
		
		mStore.sync();
	}
}
