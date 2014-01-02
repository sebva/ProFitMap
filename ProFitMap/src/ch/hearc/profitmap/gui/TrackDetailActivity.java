package ch.hearc.profitmap.gui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;
import java.util.zip.GZIPOutputStream;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.location.Location;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import ch.hearc.profitmap.R;
import ch.hearc.profitmap.gui.settings.SettingsActivity;
import ch.hearc.profitmap.gui.training.LiveTrainingActivity;
import ch.hearc.profitmap.gui.training.fragments.GraphFragment;
import ch.hearc.profitmap.gui.training.fragments.MapFragment;
import ch.hearc.profitmap.gui.training.fragments.SummaryFragment;
import ch.hearc.profitmap.gui.training.fragments.SummaryFragment.StatisticsProvider;
import ch.hearc.profitmap.gui.training.interfaces.TrackInstanceProvider;
import ch.hearc.profitmap.model.Statistics;
import ch.hearc.profitmap.model.Statistics.TypeStatistics;
import ch.hearc.profitmap.model.TrackInstance;
import ch.hearc.profitmap.model.Tracks;

import com.google.gson.Gson;

public class TrackDetailActivity extends FragmentActivity implements ActionBar.TabListener, StatisticsProvider, TrackInstanceProvider,
		CreateNdefMessageCallback, OnNdefPushCompleteCallback
{

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will keep every loaded fragment in memory. If this becomes too memory intensive, it
	 * may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private TrackInstance trackInstance;

	private MapFragment mapFragment;

	/*
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager mViewPager;
	private int mTrackId;
	private int mSport;
	private NfcAdapter mNfcAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Bundle params = getIntent().getExtras();

		mTrackId = params.getInt("trackId");
		mSport = params.getInt("sport");
		int trackInstanceId = params.getInt("trackInstanceId");
		Tracks tracks = Tracks.getInstance(mSport);

		Log.i("TDA", "trackId : " + mTrackId);
		Log.i("TDA", "trackInstanceId : " + trackInstanceId);
		this.trackInstance = tracks.getTrack(mTrackId).getTrackInstance(trackInstanceId);

		for (Location l : trackInstance.getWaypoints())
		{
			Log.i(l.getLatitude() + "", l.getLongitude() + "");
		}
		setContentView(R.layout.activity_track_detail);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// Show the Up button in the action bar.
		actionBar.setDisplayHomeAsUpEnabled(true);

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

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mNfcAdapter != null) // NFC available
		{
			// Register callback to set NDEF message
			mNfcAdapter.setNdefPushMessageCallback(this, this);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.track_detail, menu);
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
			case R.id.action_settings:
				startActivity(new Intent(this, SettingsActivity.class));
				break;
			case R.id.action_ghost:
				Intent intent = new Intent(this, LiveTrainingActivity.class);
				intent.putExtra("sport", mSport);
				intent.putExtra("trackId", mTrackId);
				startActivity(intent);
				break;
		}
		return super.onOptionsItemSelected(item);
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
			switch (position)
			{
				case 0:
					return new SummaryFragment();
				case 1:
					mapFragment = new MapFragment();
					mapFragment.setTrackInstance(trackInstance);
					return mapFragment;
				case 2:
					return new GraphFragment();
			}
			return null;
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

	@Override
	public Statistics getStatistics()
	{
		return trackInstance.getStatistics();
	}

	@Override
	public TypeStatistics getTypeStatistics()
	{
		return TypeStatistics.SUMMARY;
	}

	public TrackInstance getTrackInstance()
	{
		return trackInstance;
	}

	@Override
	public NdefMessage createNdefMessage(NfcEvent event)
	{
		Log.i("NFC", "Sending track via Beam");
		NdefRecord aar = NdefRecord.createApplicationRecord("ch.hearc.profitmap");

		String json = new Gson().toJson(trackInstance);
		
		NdefRecord sportNdef = NdefRecord.createExternal("ch.hearc.profitmap", "sport", intToByteArray(mSport));
		
		try
		{
			NdefRecord trackInstanceNdef = NdefRecord.createExternal("ch.hearc.profitmap", "trackinstance", compress(json));
			return new NdefMessage(trackInstanceNdef, sportNdef, aar);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	// From: http://stackoverflow.com/a/6718707
	public static byte[] compress(String string) throws IOException
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
		GZIPOutputStream gos = new GZIPOutputStream(os);
		gos.write(string.getBytes());
		gos.close();
		byte[] compressed = os.toByteArray();
		os.close();
		return compressed;
	}
	
	// From: http://stackoverflow.com/a/10380460
	public static byte[] intToByteArray(int myInteger)
	{
	    return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(myInteger).array();
	}

	@Override
	public void onNdefPushComplete(NfcEvent event)
	{
		Log.i("NFC", "Track sent via Beam");
		// A handler is needed to send messages to the activity when this
		// callback occurs, because it happens from a binder thread
		new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				switch (msg.what)
				{
					case 1:
						Toast.makeText(getApplicationContext(), R.string.nfc_track_sent, Toast.LENGTH_LONG).show();
						break;
				}
			}
		}.obtainMessage(1).sendToTarget();
	}
}
