package com.example.dialogbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
     // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.dialog_msg)
               .setTitle(R.string.dialog_name)
               .setItems(R.array.sports_names, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.i("clicked",which + "");
					
				}
			});

        
        
        
        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        
        dialog.show();
    }
    public void confirmFireMissiles(View view) {
        DialogFragment newFragment = new FireMissilesDialogFragment();
        newFragment.show(getFragmentManager(), "missiles");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}