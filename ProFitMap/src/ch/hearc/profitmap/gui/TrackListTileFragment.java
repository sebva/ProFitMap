package ch.hearc.profitmap.gui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import ch.hearc.profitmap.R;

public class TrackListTileFragment extends Fragment
{

	public static final String ARG_SPORT_NUMBER = "sport_number";
	private GridAdapter mAdapter;
	
	private class GridAdapter extends BaseAdapter {
	    private LayoutInflater inflater;
	    private String sport;

	    public GridAdapter(Context c, String sport) {
	        inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        this.sport = sport;
	    }

	    public int getCount() {
	        return 20;
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
	        tv.setText(sport + " " + position);
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
	    
	    public void setSport(String sport)
	    {
	    	this.sport = sport;
	    	this.notifyDataSetChanged();
	    }
	}

	public TrackListTileFragment()
	{
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_trackinstance_grid, container, false);
		int i = getArguments().getInt(ARG_SPORT_NUMBER);
		String sport = getResources().getStringArray(R.array.sports_array)[i];
		
		GridView gridView = (GridView) rootView.findViewById(R.id.trackinstance_grid);
		mAdapter = new GridAdapter(getActivity(), sport);
		setSport(i);
		
		gridView.setAdapter(mAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();
	        }
	    });

		return rootView;
	}

	public void setSport(int sportId)
	{
		String sport = getResources().getStringArray(R.array.sports_array)[sportId];
		mAdapter.setSport(sport);
	}

	public void setSortMode(String sortMode)
	{

	}

}
