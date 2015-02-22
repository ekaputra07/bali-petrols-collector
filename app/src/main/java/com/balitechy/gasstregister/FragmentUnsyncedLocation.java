package com.balitechy.gasstregister;

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

import java.util.ArrayList;
import java.util.List;

public class FragmentUnsyncedLocation extends Fragment implements LocationItem.OnLocationItemSyncListener {
    private List<LocationItem> locationList = new ArrayList<LocationItem>();
    private LocationListAdapter listAdapter;
    private ProgressDialog progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // this will append new menu specific to this fragment in toolbar.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_unsynced_actions, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.location_list, container, false);
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_map_unsynced:
                startMap();
                break;
            case R.id.action_reload_unsynced:
                loadLocations();
                break;
            case R.id.action_sync:
                syncOfflineLocations();
                break;
        }
        return super.onOptionsItemSelected(item);
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
        if (v.getId() == R.id.list) {
            getActivity().getMenuInflater().inflate(R.menu.unsynced_list_contextmenu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                deleteLocation(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && locationList.size() == 0) {
            loadLocations();
        }
    }

    public void loadLocations() {

        progress.show();
        locationList.clear();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("GasStation");
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> locations, ParseException e) {
                if (e == null) {
                    for (ParseObject parseObject : locations) {
                        locationList.add(new LocationItem(parseObject, getActivity()));
                    }
                    listAdapter.notifyDataSetChanged();
                }
                progress.dismiss();
            }
        });
    }

    public void syncOfflineLocations() {
        for (LocationItem locationItem : locationList) {
            locationItem.syncParseObject(this);
        }
    }

    public void startMap() {
        Intent mapIntent = new Intent(getActivity(), MapActivity.class);
        mapIntent.putExtra("isLive", false);
        startActivity(mapIntent);
    }

    private void deleteLocation(int index) {

        ParseObject location = locationList.get(index).getParseObject();
        location.unpinInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException arg0) {
                loadLocations();
            }
        });
    }

    @Override
    public void onSyncDone(Boolean isError) {
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSyncStart() {
        listAdapter.notifyDataSetChanged();
    }
}