package ch.hearc.profitmap.gui.training;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import ch.hearc.profitmap.R;
import ch.hearc.profitmap.TrackListActivity;
import ch.hearc.profitmap.gui.training.fragments.SummaryFragment.StatisticsProvider;
import ch.hearc.profitmap.model.Statistics;
import ch.hearc.profitmap.model.TrackInstance;

public class EndTrainingActivity extends FragmentActivity implements StatisticsProvider
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_end_training);
		// Show the Up button in the action bar.
		setupActionBar();
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
		getMenuInflater().inflate(R.menu.end_training, menu);
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
			case R.id.action_dismiss_training:
				new AlertDialog.Builder(this).setTitle(R.string.dismiss_training).setPositiveButton(R.string.dismiss_training, new OnClickListener()
				{
					
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						startActivity(new Intent(EndTrainingActivity.this, TrackListActivity.class));
						dialog.dismiss();
					}
				}).setNegativeButton(android.R.string.cancel, new OnClickListener()
				{
					
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.cancel();
					}
				}).show();
				return true;
			case R.id.action_save_training:
				startActivity(new Intent(this, TrackListActivity.class));
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public Statistics getStatistics()
	{
		// TODO Return real statistics
		return new Statistics(new TrackInstance());
	}

}
