package ch.hearc.profitmap.gui.settings;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.MenuItem;
import ch.hearc.profitmap.R;


public class SettingsActivity extends Activity
{
	public static class SettingsFragment extends PreferenceFragment
	{
		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			
			addPreferencesFromResource(R.xml.prefs);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		
		if(item.getItemId() == android.R.id.home)
			finish();
		return true;
	}

}
