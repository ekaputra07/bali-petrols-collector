package com.balitechy.gasstregister;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class MapActivity extends ActionBarActivity implements OnMapReadyCallback {
    private LatLng centerPos;
    private Boolean isDataLive;
    private double[] latLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle extras = getIntent().getExtras();
        isDataLive = extras.getBoolean("isLive");
        latLong = extras.getDoubleArray("LatLong");
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);

        centerPos = new LatLng(-8.3809918, 115.1761822); //Bali
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(centerPos, 10));

        if (latLong != null) {
            addMarker(map, latLong);
        } else if (isDataLive != null) {
            addMarkers(map, isDataLive);
        }
    }

    private void addMarker(GoogleMap map, double[] latLong) {
        LatLng point = new LatLng(latLong[0], latLong[1]);
        map.addMarker(new MarkerOptions().position(point));
    }

    private void addMarkers(final GoogleMap map, Boolean isDataLive) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("GasStation");
        if (!isDataLive) {
            query.fromLocalDatastore();
        }
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> locations, ParseException e) {
                if (e == null) {
                    for (ParseObject loc : locations) {
                        LatLng point = new LatLng(loc.getParseGeoPoint("point").getLatitude(), loc.getParseGeoPoint("point").getLongitude());
                        map.addMarker(new MarkerOptions().position(point)).setTitle(loc.getObjectId());
                    }
                }
            }
        });
    }
}
