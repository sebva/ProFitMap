package ch.hearc.profitmap.gui.training;

import java.util.Date;
import java.util.Locale;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import ch.hearc.profitmap.R;
import ch.hearc.profitmap.gui.training.fragments.GraphFragment;
import ch.hearc.profitmap.gui.training.fragments.MapFragment;
import ch.hearc.profitmap.gui.training.fragments.SummaryFragment;
import ch.hearc.profitmap.gui.training.fragments.SummaryFragment.StatisticsProvider;
import ch.hearc.profitmap.gui.training.fragments.live.LiveGraphFragment;
import ch.hearc.profitmap.gui.training.fragments.live.LiveMapFragment;
import ch.hearc.profitmap.gui.training.fragments.live.LiveStatsFragment;
import ch.hearc.profitmap.gui.training.interfaces.TrackInstanceProvider;
import ch.hearc.profitmap.model.DropboxManager;
import ch.hearc.profitmap.model.GeoImage;
import ch.hearc.profitmap.model.Statistics;
import ch.hearc.profitmap.model.Statistics.TypeStatistics;
import ch.hearc.profitmap.model.TrackInstance;
import ch.hearc.profitmap.model.Tracks;

public class LiveTrainingActivity extends FragmentActivity implements
		ActionBar.TabListener, StatisticsProvider, TrackInstanceProvider {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	public boolean isCreated = false;
	public boolean isPaused = false;

	private MapFragment liveMapFragment;
	private SummaryFragment liveStatsFragment;
	private GraphFragment liveGraphFragment;

	private Uri mCapturedImageURI;

	private Menu menu;

	private TrackInstance trackInstance;

	private int mSport;

	private int mTrackId;
	
	private int mGhostTrackInstanceId;

	private boolean mHasGhost;
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("sport", mSport);
		outState.putInt("trackId", mTrackId);
		if (liveMapFragment != null)
			liveMapFragment.endTraining();
	}

	@Override
	public void onBackPressed() {
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		RunningTaskInfo activity = am.getRunningTasks(1).get(0);
		Log.d("LTA", activity.baseActivity.getPackageName());
		if (!activity.baseActivity.getPackageName().startsWith(
				"ch.hearc.profitmap"))
			super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_startrec:
			isPaused = false;
			switchStartPauseVisibility();
			break;
		case R.id.action_stoprec:
			liveMapFragment.endTraining();
			Intent intent = new Intent(this, EndTrainingActivity.class);
			trackInstance.setTimestampEnd(new Date());
			Tracks.currentTrackInstance = trackInstance;
			intent.putExtra("sport", mSport);
			intent.putExtra("trackId", mTrackId);
			startActivity(intent);
			break;
		case R.id.action_pause:
			isPaused = true;
			switchStartPauseVisibility();
			break;
		case R.id.action_takepic:
			takePic();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void takePic() {

		String fileName = "temp.jpg";
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, fileName);
		mCapturedImageURI = getContentResolver().insert(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

		Intent cameraIntent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

		startActivityForResult(cameraIntent, 2);
	}

	private void switchStartPauseVisibility() {
		MenuItem pItem = menu.findItem(R.id.action_pause);
		MenuItem sItem = menu.findItem(R.id.action_startrec);

		sItem.setVisible(!sItem.isVisible());
		pItem.setVisible(!pItem.isVisible());
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent intent) {
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location l = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER); // Fake
																				// GPS
																				// provider
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = getApplicationContext().getContentResolver().query(
				mCapturedImageURI, projection, null, null, null);
		int column_index_data = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String capturedImageFilePath = cursor.getString(column_index_data);

		GeoImage geoImage = new GeoImage(DropboxManager.getInstance()
				.copyPictureToDropbox(this, mCapturedImageURI), l);

		trackInstance.addImage(geoImage);

		int orientation = 0; // TODO : change to correct orientation
		liveMapFragment.addPicMarkerToLocation(l, capturedImageFilePath,
				orientation);

		Log.i("Result i :", "" + l.toString() + capturedImageFilePath + " "
				+ mCapturedImageURI);
		super.onActivityResult(arg0, arg1, intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			Log.i("onC", "No bundle");
			mSport = getIntent().getIntExtra("sport", 0);
			mTrackId = getIntent().getIntExtra("trackId", -1);
			
			mHasGhost = getIntent().getBooleanExtra("hasGhost", false);
			mGhostTrackInstanceId = getIntent().getIntExtra("ghostTrackInstanceId", 0);
			
			trackInstance = new TrackInstance();
			Tracks.currentTrackInstance = trackInstance;
		} else {
			Log.i(getClass().getSimpleName(), "Restoring bundle");

			mSport = savedInstanceState.getInt("sport", 0);
			mTrackId = savedInstanceState.getInt("trackId", -1);
			
			mHasGhost = getIntent().getBooleanExtra("hasGhost", false);
			mGhostTrackInstanceId = getIntent().getIntExtra("ghostTrackInstanceId", 0);
			trackInstance = Tracks.currentTrackInstance;
		}

		setContentView(R.layout.activity_live_training);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the
		// three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title
			// defined by
			// the adapter. Also specify this Activity object, which
			// implements
			// the TabListener interface, as the callback (listener) for
			// when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		mViewPager.setCurrentItem(1);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.menu = menu;
		getMenuInflater().inflate(R.menu.live_training, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			Fragment fragment = (Fragment) super.instantiateItem(container,
					position);
			if (fragment instanceof LiveStatsFragment)
				liveStatsFragment = (SummaryFragment) fragment;
			else if (fragment instanceof LiveMapFragment)
				liveMapFragment = (MapFragment) fragment;
			else if (fragment instanceof LiveGraphFragment)
				liveGraphFragment = (GraphFragment) fragment;
			return fragment;
		}

		@Override
		public Fragment getItem(int position) {

			Fragment fragment = null;
			if (position == 1) {
				fragment = new LiveMapFragment();
				liveMapFragment = (MapFragment) fragment;
			} else if (position == 0) {
				fragment = new LiveStatsFragment();
				liveStatsFragment = (SummaryFragment) fragment;
			} else {
				fragment = new LiveGraphFragment();
				liveGraphFragment = (LiveGraphFragment) fragment;
			}
			fragment.setRetainInstance(true);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.summary_section).toUpperCase(l);
			case 1:
				return getString(R.string.map_section).toUpperCase(l);
			case 2:
				return getString(R.string.graph_section).toUpperCase(l);
			}
			return null;
		}
	}

	/*
	 * public static class DummySectionFragment extends Fragment { public static
	 * final String ARG_SECTION_NUMBER = "section_number";
	 * 
	 * public DummySectionFragment() { }
	 * 
	 * @Override public View onCreateView(LayoutInflater inflater, ViewGroup
	 * container, Bundle savedInstanceState) { View rootView = inflater.inflate(
	 * R.layout.fragment_live_training_dummy, container, false); TextView
	 * dummyTextView = (TextView) rootView .findViewById(R.id.section_label);
	 * dummyTextView.setText(Integer.toString(getArguments().getInt(
	 * ARG_SECTION_NUMBER))); return rootView; } }
	 */

	public TrackInstance getTrackInstance() {
		return trackInstance;
	}
	
	public boolean getHasGhost()
	{
		return mHasGhost;
	}
	
	public int getGhostTrackInstanceId()
	{
		return mGhostTrackInstanceId;
	}
	
	public int getTrackId()
	{
		return mTrackId;
	}

	@Override
	public Statistics getStatistics() {
		if (trackInstance != null)
			return trackInstance.getStatistics();
		else
			return null;
	}

	@Override
	public TypeStatistics getTypeStatistics() {
		return TypeStatistics.LIVE;
	}

	public void refreshStatsPanel() {
		LiveStatsFragment lsf = (LiveStatsFragment) liveStatsFragment;

		if (lsf != null)
			lsf.refreshPanel();
		else
			Log.i("LTA", "lsf null");
	}

	public void refreshGraphPanel() {
		LiveGraphFragment lgf = (LiveGraphFragment) liveGraphFragment;

		if (lgf != null)
			lgf.refreshGraphs();
		else
			Log.i("LTA", "lgf null");
	}

	public int getSportId() {
		return mSport;
	}

}
