/*
 * Copyright 2013 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package ch.hearc.profitmap;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import ch.hearc.profitmap.gui.StartTrainingDialogFragment;
import ch.hearc.profitmap.gui.TrackListTilesFragment;
import ch.hearc.profitmap.gui.settings.SettingsActivity;
import ch.hearc.profitmap.model.DropboxManager;

public class TrackListActivity extends Activity
{
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private DropboxManager mDropboxManager;
	private MatrixCursor mSportsCursor;
	private static final String[] mColumns = { "_id", "image", "text" };
	private static final int DROPBOX_LINK_CALLBACK = 0;

	private String[] mSports;
	private String[] mSportsImages;

	private int mCurrentIndex = 0;
	private TrackListTilesFragment mTrackListFragment;
	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track_list);

		mDropboxManager = DropboxManager.getInstance();
		mSportsCursor = new MatrixCursor(mColumns, 4);
		mSports = getResources().getStringArray(R.array.sports_array);
		mSportsImages = getResources().getStringArray(R.array.sports_images);
		for (int i = 0; i < mSports.length; i++)
		{
			Object[] row = new Object[3];
			row[0] = i;
			row[1] = getSportImageIdentifier(i);
			row[2] = mSports[i];

			mSportsCursor.addRow(row);
		}

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new SimpleCursorAdapter(this, R.layout.drawer_list_item, mSportsCursor, new String[] { "image", "text" }, new int[] {
				R.id.imageView, R.id.textView }, 0));

		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		String[] sports = getResources().getStringArray(R.array.sort_modes);
		SpinnerAdapter mSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sports);
		actionBar.setListNavigationCallbacks(mSpinnerAdapter, new OnNavigationListener()
		{

			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId)
			{
				// TODO Auto-generated method stub
				return false;
			}
		});

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		)
		{
			public void onDrawerClosed(View view)
			{
				onDrawerOpenClose(false);
				ActivityCompat.invalidateOptionsMenu(TrackListActivity.this); // creates call to onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView)
			{
				onDrawerOpenClose(true);
				ActivityCompat.invalidateOptionsMenu(TrackListActivity.this); // creates call to onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null)
		{
			selectItem(0);
		}
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		
		if(!mDropboxManager.isDropboxLinked())
		{
			mDropboxManager.linkToDropbox(this, DROPBOX_LINK_CALLBACK);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == DROPBOX_LINK_CALLBACK)
		{
			Toast.makeText(this, (resultCode == Activity.RESULT_OK) ? getString(R.string.dropbox_connected) : getString(R.string.dropbox_error), Toast.LENGTH_LONG).show();
		}
		else
			super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void onDrawerOpenClose(boolean isOpen)
	{
		ActionBar actionBar = getActionBar();
		if(isOpen)
		{
			actionBar.setIcon(R.drawable.ic_launcher);
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		}
		else
		{
			actionBar.setIcon(getSportImageIdentifier(mCurrentIndex));
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.track_list, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.action_start_training:
				DialogFragment newFragment = new StartTrainingDialogFragment();
		        newFragment.show(getFragmentManager(), "missiles");
				break;
			case R.id.action_settings:
				startActivity(new Intent(this, SettingsActivity.class));
				break;
			case R.id.action_unlink_dropbox:
				mDropboxManager.unlinkDropbox();
				mDropboxManager.linkToDropbox(this, DROPBOX_LINK_CALLBACK);
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		return mDrawerToggle.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed()
	{
		if(mTrackListFragment.onBackPressed())
			super.onBackPressed();
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements ListView.OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			selectItem(position);
			Log.i("Drawer", view.getClass().getName());
		}
	}

	private void selectItem(int position)
	{
		mCurrentIndex = position;
		if (mTrackListFragment == null)
		{
			mTrackListFragment = new TrackListTilesFragment();
			Bundle args = new Bundle();
			args.putInt(TrackListTilesFragment.ARG_SPORT_NUMBER, position);
			mTrackListFragment.setArguments(args);

			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.content_frame, mTrackListFragment).commit();
		}
		else
			mTrackListFragment.setSport(position);

		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		getActionBar().setIcon(getSportImageIdentifier(mCurrentIndex));
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	private int getSportImageIdentifier(int position)
	{
		return getResources().getIdentifier(mSportsImages[position], "drawable", TrackListActivity.class.getPackage().getName());
	}

	@Override
	public void setTitle(CharSequence title)
	{
		getActionBar().setTitle(title);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
		onDrawerOpenClose(mDrawerLayout.isDrawerOpen(GravityCompat.START));
		Log.d(getClass().getSimpleName(), "Rotate");
	}
}