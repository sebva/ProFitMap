package ch.hearc.profitmap.gui.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import ch.hearc.profitmap.R;

import com.mobeta.android.dslv.DragSortListView;

public class StatsOrderActivity extends Activity
{
	private DragSortListView dslv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats_order);
		// Show the Up button in the action bar.
		setupActionBar();
		
		dslv = (DragSortListView) findViewById(R.id.dslv);
		dslv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[]{"Item 1", "Item 2", "Item 3"}));
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
		
		ListAdapter adapter = dslv.getAdapter();
		for(int i = 0; i < adapter.getCount(); i++)
			Log.d("DSLV", (String) adapter.getItem(i));
	}
}
