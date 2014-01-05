package ch.hearc.profitmap.gui.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import ch.hearc.profitmap.R;
import ch.hearc.profitmap.model.DropboxManager;

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFields;
import com.dropbox.sync.android.DbxList;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;
import com.dropbox.sync.android.DbxTable.QueryResult;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

public class StatsOrderActivity extends Activity
{
	private DragSortListView listView;
	private ArrayAdapter<String> adapter;
	private DropboxManager dropbox;
	private String[] tiles_array;
	private int sportId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats_order);
		// Show the Up button in the action bar.
		setupActionBar();
		
		tiles_array = getResources().getStringArray(R.array.tiles_array);
		
		sportId = getIntent().getIntExtra("sportId", 0);
		
		getActionBar().setIcon(getResources().getIdentifier(getResources().getStringArray(R.array.sports_images)[sportId], "drawable", "ch.hearc.profitmap"));
		
		dropbox = DropboxManager.getInstance();
		DbxTable table = dropbox.getStatsOrderTable();
		
		ArrayList<String> list;
		
		try
		{
			QueryResult result = table.query(new DbxFields().set("sport", sportId));
			if(result.count() > 0)
			{
				DbxRecord record = result.asList().get(0);
				list = new ArrayList<String>(tiles_array.length);
				DbxList dbxList = record.getList("order");
				for(int i = 0; i < dbxList.size(); i++)
					list.add(tiles_array[(int) dbxList.getLong(i)]);
			}
			else
				list = new ArrayList<String>(Arrays.asList(tiles_array));
		}
		catch (DbxException e)
		{
			e.printStackTrace();
			list = new ArrayList<String>(Arrays.asList(tiles_array));
		}
		
		listView = (DragSortListView) findViewById(R.id.dslv);
	    
	    adapter = new ArrayAdapter<String>(this, R.layout.stats_order_list_item, R.id.textView, list);
	    listView.setAdapter(adapter);
	    listView.setDropListener(new DragSortListView.DropListener()
	    {
	        @Override
	        public void drop(int from, int to)
	        {
	            if (from != to)
	            {
	                String item = adapter.getItem(from);
	                adapter.remove(item);
	                adapter.insert(item, to);
	            }
	        }
	    });
		listView.setRemoveListener(new DragSortListView.RemoveListener()
		{
			@Override
			public void remove(int which)
			{
				adapter.remove(adapter.getItem(which));
			}
		});

	    DragSortController controller = new DragSortController(listView);
	    controller.setDragHandleId(R.id.imageView);
	    controller.setRemoveEnabled(false);
	    controller.setSortEnabled(true);
	    controller.setDragInitMode(DragSortController.ON_DRAG);

	    listView.setFloatViewManager(controller);
	    listView.setOnTouchListener(controller);
	    listView.setDragEnabled(true);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar()
	{

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stats_order, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				// This ID represents the Home or Up button. In the case of this
				// activity, the Up button is shown. Use NavUtils to allow users
				// to navigate up one level in the application structure. For
				// more details, see the Navigation pattern on Android Design:
				//
				// http://developer.android.com/design/patterns/navigation.html#up-vs-back
				//
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		
		DbxTable dbxTable = dropbox.getStatsOrderTable();
		try
		{
			QueryResult result = dbxTable.query(new DbxFields().set("sport", sportId));
			if(result.hasResults())
			{
				Iterator<DbxRecord> iterator = result.iterator();
				while(iterator.hasNext())
					iterator.next().deleteRecord();
			}
		}
		catch (DbxException e)
		{
			e.printStackTrace();
		}
		
		DbxList list = new DbxList();
		for(int i = 0; i < adapter.getCount(); i++)
		{
			int id = -1;
			String item = adapter.getItem(i);
			while(tiles_array[++id] != item);
			list.add(id);
		}
		dbxTable.insert().set("sport", sportId).set("order", list);
	}
}
