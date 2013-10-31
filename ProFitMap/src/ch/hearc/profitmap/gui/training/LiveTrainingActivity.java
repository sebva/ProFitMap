package ch.hearc.profitmap.gui.training;

import java.util.Locale;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import ch.hearc.profitmap.R;
import ch.hearc.profitmap.R.id;
import ch.hearc.profitmap.R.layout;
import ch.hearc.profitmap.R.menu;
import ch.hearc.profitmap.R.string;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LiveTrainingActivity extends FragmentActivity implements
		ActionBar.TabListener {

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
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_live_training);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
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
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
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
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = null;
			if (position == 1) fragment = new MapSectionFragment();
			else fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt("pos", position + 1);
			fragment.setArguments(args);
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
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_live_training_dummy, container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return rootView;
		}
	}
	
	public static class MapSectionFragment extends Fragment {
		
		private SupportMapFragment fragment;

		private GoogleMap map;
		private static final LatLng NEUCH_LOC = new LatLng(47.0045047, 6.957424);
		private MarkerOptions mo = new MarkerOptions().position(NEUCH_LOC)
				.snippet("Tits!").title("Prout!");
		private Marker m;

		private PolylineOptions plo = new PolylineOptions().geodesic(true).color(
				Color.parseColor("#AA66CC"));
		private Polyline pl;
		
		public MapSectionFragment()
		{
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		    return inflater.inflate(R.layout.fragment_live_training_map, container, false);
		}
		@Override
		public void onResume() {
		    super.onResume();
		    if (map == null) {
		        map = fragment.getMap();
				m = map.addMarker(mo);
				setupFakeGPS();
		    }
		}
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
		    super.onActivityCreated(savedInstanceState);
		    FragmentManager fm = getChildFragmentManager();
		    fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
		    if (fragment == null) {
		        fragment = SupportMapFragment.newInstance();
		        fm.beginTransaction().replace(R.id.map, fragment).commit();
		    }
		}
		private void setupFakeGPS() {
			final LocationManager lm;
			FakeLocationListener ll;
			lm = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
			ll = new FakeLocationListener();
			if (lm.getProvider("Test") == null) {
				lm.addTestProvider("Test", false, false, false, false, false,
						false, false, 0, 1);
			}
			lm.setTestProviderEnabled("Test", true);
			lm.requestLocationUpdates("Test", 0, 0, ll);

			map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
				@SuppressLint("NewApi")
				@Override
				public void onMapClick(LatLng l) {
					Log.i(map.hashCode()+ "","hash");
					Location loc = new Location("Test");
					loc.setLatitude(l.latitude);
					loc.setLongitude(l.longitude);
					loc.setAltitude(0);
					loc.setAccuracy(1);
					loc.setTime(System.currentTimeMillis());
					loc.setElapsedRealtimeNanos(1000);
					lm.setTestProviderLocation("Test", loc);
				}
			});
		}
		private class FakeLocationListener implements LocationListener {
			@Override
			public void onLocationChanged(Location location) {

				// Called when a new location is found by the network location
				// provider.
				Log.i("lat", location.getLatitude() + "");
				Log.i("long", location.getLongitude() + "");
				// map.addMarker(new MarkerOptions().position(new
				// LatLng(location.getLatitude(), location.getLongitude())));
				plo.add(new LatLng(location.getLatitude(), location.getLongitude()));
				if (pl != null) {
					pl.remove();
				}

				pl = map.addPolyline(plo);
			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub

			}
		}
	}

}
