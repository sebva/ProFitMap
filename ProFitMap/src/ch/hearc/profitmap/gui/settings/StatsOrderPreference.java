package ch.hearc.profitmap.gui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.preference.Preference;
import android.util.AttributeSet;
import ch.hearc.profitmap.R;

public class StatsOrderPreference extends Preference
{

	private int sport;

	public StatsOrderPreference(Context context, AttributeSet attributeSet)
	{
		super(context, attributeSet);
		sport = Integer.parseInt(getKey());
		Resources resources = context.getResources();
		setTitle(resources.getStringArray(R.array.sports_array)[sport]);
		setIcon(resources.getIdentifier(resources.getStringArray(R.array.sports_images)[sport], "drawable", "ch.hearc.profitmap"));
	}
	
	@Override
	protected void onClick()
	{
		Intent intent = new Intent(getContext(), StatsOrderActivity.class);
		intent.putExtra("sportId", sport);
		getContext().startActivity(intent);
	}
	
}