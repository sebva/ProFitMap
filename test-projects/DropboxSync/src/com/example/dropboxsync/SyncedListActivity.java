package com.example.dropboxsync;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccount.Listener;
import com.dropbox.sync.android.DbxAccountInfo;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;

public class SyncedListActivity extends ListActivity implements DbxDatastore.SyncStatusListener {
	
	private class Row {

		private String id;
		private String text;
		
		public Row(String id, String text) {
			this.text = text;
			this.id = id;
		}
		
		public View getView()
		{
			TextView tv = new TextView(SyncedListActivity.this);
			tv.setTextSize(18);
			tv.setText(text);
			tv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					rows.remove(Row.this);
					adapter.notifyDataSetChanged();
					try {
						mTable.get(id).deleteRecord();
					} catch (DbxException e) {
						e.printStackTrace();
					}
				}
			});
			return tv;
		}

		public String getId() {
			return id;
		}
		
	}
	
	private static final int DROPBOX_LINK_CALLBACK = 0;
	private DbxAccountManager mDbxAcctMgr;
	private DbxDatastore mStore;
	private DbxTable mTable;
	private List<Row> rows;
	private BaseAdapter adapter;
	private int counter = 0;
	
	public SyncedListActivity() {
		rows = new ArrayList<Row>();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(), "q2sr7uxe7l3b38n", "4byz83rhp7lt0ou");		
		
		adapter = new BaseAdapter() {
			
			@Override
			public View getView(int position, View view, ViewGroup viewGroup) {
				return rows.get(position).getView();
			}
			
			@Override
			public long getItemId(int position) {
				return rows.get(position).getId().hashCode();
			}
			
			@Override
			public Object getItem(int position) {
				return rows.get(position);
			}
			
			@Override
			public int getCount() {
				return rows.size();
			}
		};
		setListAdapter(adapter);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(!mDbxAcctMgr.hasLinkedAccount())
			return;
		
		try {
			mStore = DbxDatastore.openDefault(mDbxAcctMgr.getLinkedAccount());
			mTable = mStore.getTable("list");
			onDatastoreStatusChange(mStore);
			mStore.addSyncStatusListener(this);
		} catch (DbxException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(mStore == null)
			return;		
		
		mStore.removeSyncStatusListener(this);
        mStore.close();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == DROPBOX_LINK_CALLBACK)
		{
			if(resultCode == Activity.RESULT_OK)
			{
				DbxAccount linkedAccount = mDbxAcctMgr.getLinkedAccount();
				linkedAccount.addListener(new Listener() {
					
					@Override
					public void onAccountChange(DbxAccount account) {
						DbxAccountInfo info = account.getAccountInfo();
						if(info != null)
							Toast.makeText(SyncedListActivity.this, "Welcome " + info.displayName, Toast.LENGTH_LONG).show();
						account.removeListener(this);
					}
				});
			}
			else
				Toast.makeText(this, "Link failed !", Toast.LENGTH_LONG).show();
		}
		else
			super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.synced_list, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(item.getItemId() == R.id.action_link)
		{
			mDbxAcctMgr.startLink(this, DROPBOX_LINK_CALLBACK);
			return true;
		}
		else if(item.getItemId() == R.id.action_unlink)
		{
			mDbxAcctMgr.unlink();
			this.finish();
			return true;
		}
		else if(item.getItemId() == R.id.add_row)
		{
			Builder ad = new AlertDialog.Builder(this);
			ad.setTitle("Row name");
			ad.setMessage("Enter a row name");
			
			// Set an EditText view to get user input 
			final EditText input = new EditText(this);
			ad.setView(input);
			
			ad.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(input.getText().toString().length() > 0)
					{
						String text = input.getText().toString();
						DbxRecord record = mTable.insert().set("id", ++counter).set("text", text);
						rows.add(new Row(record.getId(), text));
						
						adapter.notifyDataSetChanged();
					}
				}
			});
			ad.setNegativeButton(android.R.string.cancel, null);
			ad.show();
			return true;
		}
		else
			return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDatastoreStatusChange(DbxDatastore datastore) {
		rows.clear();
		try {
			datastore.sync();
			Iterator<DbxRecord> iterator = mTable.query().iterator();
			while(iterator.hasNext()) {
				DbxRecord record = iterator.next();
				rows.add(new Row(record.getId(), record.getString("text")));
			}
			adapter.notifyDataSetChanged();
		} catch (DbxException e) {
			e.printStackTrace();
		}
	}

}
