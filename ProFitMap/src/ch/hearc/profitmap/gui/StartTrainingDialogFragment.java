package ch.hearc.profitmap.gui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import ch.hearc.profitmap.R;
import ch.hearc.profitmap.TrackListActivity;
import ch.hearc.profitmap.gui.training.LiveTrainingActivity;

public class StartTrainingDialogFragment extends DialogFragment
{
	private String[] mSportsImages;
	private static final String[] mColumns = { "_id", "image", "text" };

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		ListView list = (ListView) getActivity().getLayoutInflater().inflate(R.layout.start_training_dialog, null);
		
		MatrixCursor sportsCursor = new MatrixCursor(mColumns, 4);
		String[] sports = getResources().getStringArray(R.array.sports_array);
		mSportsImages = getResources().getStringArray(R.array.sports_images);
		for (int i = 0; i < sports.length; i++)
		{
			Object[] row = new Object[3];
			row[0] = i;
			row[1] = getSportImageIdentifier(i);
			row[2] = sports[i];

			sportsCursor.addRow(row);
		}
		
		list.setAdapter(new SimpleCursorAdapter(getActivity(), R.layout.drawer_list_item, sportsCursor, new String[] { "image", "text" }, new int[] {
				R.id.imageView, R.id.textView }, 0));
		list.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				startActivity(new Intent(getActivity(), LiveTrainingActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
				dismiss();
			}
		});
		
		builder.setView(list);
		builder.setTitle(R.string.start_training);
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				dismiss();
			}
		});
		
		// Create the AlertDialog object and return it
		return builder.create();
	}
	
	private int getSportImageIdentifier(int position)
	{
		return getResources().getIdentifier(mSportsImages[position], "drawable", TrackListActivity.class.getPackage().getName());
	}
}