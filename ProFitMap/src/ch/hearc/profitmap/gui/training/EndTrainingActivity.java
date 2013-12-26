package ch.hearc.profitmap.gui.training;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import ch.hearc.profitmap.R;
import ch.hearc.profitmap.TrackListActivity;
import ch.hearc.profitmap.gui.training.fragments.SummaryFragment;
import ch.hearc.profitmap.gui.training.fragments.SummaryFragment.StatisticsProvider;
import ch.hearc.profitmap.model.DropboxManager;
import ch.hearc.profitmap.model.Statistics;
import ch.hearc.profitmap.model.Track;
import ch.hearc.profitmap.model.TrackInstance;
import ch.hearc.profitmap.model.Tracks;
import ch.hearc.profitmap.model.Statistics.TypeStatistics;

import com.dropbox.chooser.android.DbxChooser;
import com.dropbox.chooser.android.DbxChooser.ResultType;

public class EndTrainingActivity extends FragmentActivity implements StatisticsProvider
{
	
	protected static final int kChooserCode = 109;
	private TrackInstance mTrackInstance;
	private int mSport;
	private int mTrackId;
	
	private EditText mTrackTitle;
	private RatingBar mRating;
	private DbxChooser mChooser;
	private ImageButton mBtnChoosePic;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_end_training);
		// Show the Up button in the action bar.
		setupActionBar();
		
		mChooser = new DbxChooser("q2sr7uxe7l3b38n");
		
		mBtnChoosePic = (ImageButton) findViewById(R.id.btn_choose_pic);
		mBtnChoosePic.setOnClickListener(new android.view.View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				mChooser.forResultType(ResultType.FILE_CONTENT).launch(EndTrainingActivity.this, kChooserCode);
			}
		});
		
		mTrackInstance = Tracks.currentTrackInstance;
		mSport = getIntent().getIntExtra("sport", 0);
		mTrackId = getIntent().getIntExtra("trackId", -1);
		
		mTrackTitle = (EditText) findViewById(R.id.track_name);
		if(mTrackId != -1)
		{
			mTrackTitle.setText(Tracks.getInstance(mSport).getTrack(mTrackId).getName());
			mTrackTitle.setEnabled(false);
			mTrackTitle.setFocusable(false);
		}
		
		mRating = (RatingBar) findViewById(R.id.track_rating);
		SummaryFragment mStatsFragment = (SummaryFragment) getSupportFragmentManager().findFragmentById(R.id.summary_fragment);
		mStatsFragment.setStatistics(mTrackInstance.getStatistics(), TypeStatistics.END);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar()
	{

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == kChooserCode && resultCode == RESULT_OK)
		{
			DbxChooser.Result result = new DbxChooser.Result(data);
			try
			{
				Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result.getLink());
				bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
				mBtnChoosePic.setImageBitmap(bitmap);
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			DropboxManager dropbox = DropboxManager.getInstance();
			
			mTrackInstance.setThumbnail(dropbox.copyPictureToDropbox(this, result.getLink()));
		}
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
						finish();
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
				Tracks tracks = Tracks.getInstance(mSport);
				Track track;
				if(mTrackId == -1)
					track = new Track();
				else
					track = tracks.getTrack(mTrackId);
				
				track.setName(mTrackTitle.getText().toString());
				mTrackInstance.setRating((int) mRating.getRating());
				
				track.addTrackInstance(mTrackInstance);
				if(mTrackId == -1)
					tracks.addTrack(track);
				startActivity(new Intent(this, TrackListActivity.class));
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public Statistics getStatistics()
	{
		return null;
	}

	@Override
	public TypeStatistics getTypeStatistics()
	{
		return TypeStatistics.END;
	}
}
