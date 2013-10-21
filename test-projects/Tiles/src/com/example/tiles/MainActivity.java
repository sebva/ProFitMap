package com.example.tiles;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		GridView gridView = (GridView) findViewById(R.id.gridview);
		gridView.setAdapter(new GridAdapter(this));
		gridView.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            if(position % 3 == 0)
	            {
	            	Intent i = new Intent(getApplicationContext(), MainActivity.class);
	            	startActivity(i);
	            }
	            else
	            	Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
	        }
	    });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private class GridAdapter extends BaseAdapter {
	    private LayoutInflater inflater;

	    public GridAdapter(Context c) {
	        inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    }

	    public int getCount() {
	        return 40;
	    }

	    public Object getItem(int position) {
	        return null;
	    }

	    public long getItemId(int position) {
	        return 0;
	    }

	    public View getView(int position, View convertView, ViewGroup parent) {
	        View v;
	        if (convertView == null) {  // if it's not recycled, initialize some attributes
	            v = inflater.inflate(R.layout.tile, null);
	        } else {
	            v = convertView;
	        }
	        
	        TextView tv = (TextView)v.findViewById(R.id.textView);
	        tv.setText("Tuile " + position);
	        TextView count = (TextView)v.findViewById(R.id.count);
	        if(position % 3 == 0)
	        {
	        	count.setVisibility(View.VISIBLE);
	        	count.setText(String.valueOf(position / 3));
	        }
	        else
	        	count.setVisibility(View.INVISIBLE);
	        return v;
	    }
	}
	
}
