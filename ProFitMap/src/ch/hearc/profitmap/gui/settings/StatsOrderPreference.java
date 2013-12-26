package ch.hearc.profitmap.gui.settings;

import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.util.AttributeSet;

public class StatsOrderPreference extends Preference
{

	public StatsOrderPreference(Context context, AttributeSet attributeSet)
	{
		super(context, attributeSet);
	}
	
	@Override
	protected void onClick()
	{
		Intent intent = new Intent(getContext(), StatsOrderActivity.class);
		getContext().startActivity(intent);
	}
	
}