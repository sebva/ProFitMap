package ch.hearc.profitmap;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LocationTrackService extends Service
{
	public LocationTrackService()
	{
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
}      

