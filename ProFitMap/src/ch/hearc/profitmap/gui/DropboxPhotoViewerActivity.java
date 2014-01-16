package ch.hearc.profitmap.gui;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import ch.hearc.profitmap.R;
import ch.hearc.profitmap.gui.DropboxImageView.ImageLoadedListener;
import ch.hearc.profitmap.model.DropboxManager;

import com.dropbox.sync.android.DbxPath;

public class DropboxPhotoViewerActivity extends Activity
{
	private DropboxImageView imageView;
	private PhotoViewAttacher attacher;
	private DbxPath path;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		path = new DbxPath(getIntent().getStringExtra("path"));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		setupImageView();
	}

	private void setupImageView()
	{
		setContentView(R.layout.activity_dropbox_photo_viewer);
		imageView = (DropboxImageView) findViewById(R.id.image_view);
		attacher = new PhotoViewAttacher(imageView);
		imageView.setListener(new ImageLoadedListener()
		{
			@Override
			public void onImageLoaded()
			{
				attacher.update();
			}
		});
		
		imageView.loadImageFromDropbox(path, DropboxManager.getInstance().getFilesystem(), true);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		super.onConfigurationChanged(newConfig);
		setupImageView();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getItemId() == android.R.id.home)
			finish();
		return super.onOptionsItemSelected(item);
	}

}
