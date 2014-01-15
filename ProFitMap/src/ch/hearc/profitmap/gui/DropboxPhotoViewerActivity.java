package ch.hearc.profitmap.gui;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.app.Activity;
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

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
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
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		DbxPath path = new DbxPath(getIntent().getStringExtra("path"));
		imageView.loadImageFromDropbox(path, DropboxManager.getInstance().getFilesystem(), true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getItemId() == android.R.id.home)
			finish();
		return super.onOptionsItemSelected(item);
	}

}
