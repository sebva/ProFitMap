package ch.hearc.profitmap.gui.training;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ch.hearc.profitmap.R;
import ch.hearc.profitmap.gui.training.fragments.MapFragment;

public class LiveTrainingActivity extends FragmentActivity implements ActionBar.TabListener
{

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will keep every loaded fragment in memory. If this becomes too memory intensive, it
	 * may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	public boolean isCreated = false;
	public boolean isPaused = false;

	private MapFragment msf;

	private Uri mCapturedImageURI;

	private Menu menu;
	
	@Override
	public void onBackPressed()
	{
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		RunningTaskInfo activity = am.getRunningTasks(1).get(0);
		if(!activity.topActivity.getPackageName().startsWith("ch.hearc.profitmap"))
			super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_startrec:
				isPaused = false;
				switchStartPauseVisibility();
				break;
			case R.id.action_stoprec:
				startActivity(new Intent(this, EndTrainingActivity.class));
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

	private void takePic()
	{

		String fileName = "temp.jpg";
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, fileName);
		mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

		startActivityForResult(cameraIntent, 2);
	}

	private void switchStartPauseVisibility()
	{
		MenuItem pItem = menu.findItem(R.id.action_pause);
		MenuItem sItem = menu.findItem(R.id.action_startrec);

		sItem.setVisible(!sItem.isVisible());
		pItem.setVisible(!pItem.isVisible());
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent intent)
	{
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location l = lm.getLastKnownLocation("Test"); // Fake GPS provider

		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = getApplicationContext().getContentResolver().query(mCapturedImageURI, projection, null, null, null);
		int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String capturedImageFilePath = cursor.getString(column_index_data);
		msf.addPicMarkerToLocation(l, capturedImageFilePath);

		Log.i("Result i :", "" + l.toString() + capturedImageFilePath);
		super.onActivityResult(arg0, arg1, intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.i("onC", "creat");
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_live_training);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position)
			{
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++)
		{
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		this.menu = menu;
		getMenuInflater().inflate(R.menu.live_training, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter
	{

		public SectionsPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = null;
			if (position == 1)
			{
				fragment = new MapFragment();
				msf = (MapFragment) fragment;
			}
			else
				fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt("pos", position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount()
		{
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			Locale l = Locale.getDefault();
			switch (position)
			{
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

	/**
	 * A dummy fragment representing a section of the app, but that simply displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment
	{
		/**
		 * The fragment argument representing the section number for this fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment()
		{
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			View rootView = inflater.inflate(R.layout.fragment_live_training_dummy, container, false);
			TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
			return rootView;
		}
	}

}
