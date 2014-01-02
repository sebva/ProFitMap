package ch.hearc.profitmap.nfc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.GZIPInputStream;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.util.Log;
import ch.hearc.profitmap.gui.training.EndTrainingActivity;
import ch.hearc.profitmap.model.TrackInstance;
import ch.hearc.profitmap.model.Tracks;

import com.google.gson.Gson;

public class NFCImportActivity extends Activity
{

	@Override
	public void onResume()
	{
		super.onResume();
		// Check to see that the Activity started due to an Android Beam
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction()))
			processIntent(getIntent());
	}

	@Override
	public void onNewIntent(Intent intent)
	{
		// onResume gets called after this to handle the intent
		setIntent(intent);
	}

	private void processIntent(Intent intent)
	{
		Log.i("NFC", "Processing track received via Beam");
		Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

		/*
		 * 0: TrackInstance in Gson form
		 * 1: Sport ID (int in byte[] form)
		 * 2: Android Application Record
		 */
		NdefRecord[] records = ((NdefMessage) rawMsgs[0]).getRecords();
		try
		{
			String json = decompress(records[0].getPayload());
			TrackInstance trackInstance = new Gson().fromJson(json, TrackInstance.class);

			int sportId = byteArrayToInt(records[1].getPayload());

			Intent endTrainingIntent = new Intent(this, EndTrainingActivity.class);
			Tracks.currentTrackInstance = trackInstance;
			endTrainingIntent.putExtra("sport", sportId);
			endTrainingIntent.putExtra("trackId", -1);
			endTrainingIntent.putExtra("rating", trackInstance.getRating());
			startActivity(endTrainingIntent);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	// From: http://stackoverflow.com/a/6718707
	public static String decompress(byte[] compressed) throws IOException
	{
		final int BUFFER_SIZE = 32;
		ByteArrayInputStream is = new ByteArrayInputStream(compressed);
		GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
		StringBuilder string = new StringBuilder();
		byte[] data = new byte[BUFFER_SIZE];
		int bytesRead;
		while ((bytesRead = gis.read(data)) != -1)
			string.append(new String(data, 0, bytesRead));

		gis.close();
		is.close();
		return string.toString();
	}

	// From: http://stackoverflow.com/a/10380460
	public static int byteArrayToInt(byte[] byteBarray)
	{
		return ByteBuffer.wrap(byteBarray).order(ByteOrder.LITTLE_ENDIAN).getInt();
	}

}
