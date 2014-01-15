package ch.hearc.profitmap.gui;

import java.io.FileInputStream;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;
import ch.hearc.profitmap.R;

import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxFileSystem.ThumbFormat;
import com.dropbox.sync.android.DbxFileSystem.ThumbSize;
import com.dropbox.sync.android.DbxPath;

public class DropboxImageView extends ImageView
{
	private AsyncTask<Void, Void, Drawable> asyncTask;
	private ImageLoadedListener listener = null;

	public static interface ImageLoadedListener
	{
		public void onImageLoaded();
	}
	
	public DropboxImageView(Context context, AttributeSet attributeSet)
	{
		super(context, attributeSet);
	}
	
	public void reset()
	{
		if(asyncTask != null && !asyncTask.isCancelled())
			asyncTask.cancel(false);
		
		setBackgroundResource(android.R.color.holo_green_dark);
		setImageResource(android.R.color.transparent);
	}
	
	public void loadImageFromDropbox(final DbxPath path, final DbxFileSystem dbxFs)
	{
		loadImageFromDropbox(path, dbxFs, false);
	}
	
	public void loadImageFromDropbox(final DbxPath path, final DbxFileSystem dbxFs, final boolean wantMaxQuality)
	{
		asyncTask = new AsyncTask<Void, Void, Drawable>()
		{
			protected void onPreExecute()
			{
				setBackgroundResource(android.R.color.transparent);
				setImageResource(R.drawable.loading_dropbox);
			};
			 
			@Override
			protected Drawable doInBackground(Void... params)
			{
				try
				{
					DbxFile file;
					if(wantMaxQuality)
						file = dbxFs.open(path);
					else
						file = dbxFs.openThumbnail(path, ThumbSize.L, ThumbFormat.PNG);
					
					FileInputStream fis = file.getReadStream();
					Drawable drawable = Drawable.createFromStream(fis, file.getPath().toString());
					fis.close();
					file.close();
					return drawable;
				}
				catch (Exception e)
				{
					e.printStackTrace();
					return null;
				}
			}
			
			@Override
			protected void onPostExecute(Drawable result)
			{
				setImageDrawable(result);
				if(listener != null)
					listener.onImageLoaded();
			}
		}.execute();
	}

	public void setListener(ImageLoadedListener imageLoadedListener)
	{
		this.listener = imageLoadedListener;
	}
}
