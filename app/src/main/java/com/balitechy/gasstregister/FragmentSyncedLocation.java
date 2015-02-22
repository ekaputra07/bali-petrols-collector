package com.balitechy.gasstregister;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


public class FragmentSyncedLocation extends Fragment {
	private List<LocationItem> locationList = new ArrayList<LocationItem>();
	private LocationListAdapter listAdapter;
    private ProgressDialog progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_synced_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.action_map_synced:
                startMap();
                break;
            case R.id.action_reload_synced:
                loadLocations();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.location_list, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        progress = new ProgressDialog(getActivity());
        progress.setMessage(getResources().getString(R.string.loading_text));
        progress.setIndeterminate(false);
        progress.setCancelable(true);

		listAdapter = new LocationListAdapter(getActivity(), R.layout.location, locationList);
        ListView listView = (ListView) getView().findViewById(R.id.list);
		listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ParseObject parseObject = ((LocationItem) listAdapter.getItem(position)).getParseObject();
                double[] latLong = {parseObject.getParseGeoPoint("point").getLatitude(), parseObject.getParseGeoPoint("point").getLongitude()};

                Intent mapIntent = new Intent(getActivity(), MapActivity.class);
                mapIntent.putExtra("LatLong", latLong);
                startActivity(mapIntent);
            }
        });

		registerForContextMenu(listView);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {		
		if(v.getId() == R.id.list){
			getActivity().getMenuInflater().inflate(R.menu.synced_list_contextmenu, menu);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.action_delete_live:
				AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
				deleteLocation(info.position);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	public void loadLocations(){
		progress.show();
		locationList.clear();
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("GasStation");
		query.orderByDescending("createdAt");
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> locations, ParseException e) {
                if(e == null) {
                    for(ParseObject parseObject: locations){
                        locationList.add(new LocationItem(parseObject, getActivity()));
                    }
                    listAdapter.notifyDataSetChanged();
                }
                progress.dismiss();
			}
		});
	}

    public void startMap(){
        Intent mapIntent = new Intent(getActivity(), MapActivity.class);
        mapIntent.putExtra("isLive", true);
        startActivity(mapIntent);
    }

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if(isVisibleToUser && locationList.size() == 0){
			loadLocations();
		}
	}

    private void deleteLocation(int index){
		ParseObject location = ((LocationItem) listAdapter.getItem(index)).getParseObject();
		
		location.deleteInBackground(new DeleteCallback(){

			@Override
			public void done(ParseException e) {
                if(e == null) {
                    loadLocations();
                }
			}
			
		});
	}
}
