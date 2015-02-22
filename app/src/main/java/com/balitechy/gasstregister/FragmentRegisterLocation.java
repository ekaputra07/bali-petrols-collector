package com.balitechy.gasstregister;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

public class FragmentRegisterLocation extends Fragment implements OnMapReadyCallback{
	
    private Button btnRegister;
    private TextView txtSuccess;
    private EditText editLat, editLong;
    private double latitude, longitude;
    private GoogleMap map;
    private int locationUpdateCounter = 0;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_register, container, false);
		
        txtSuccess = (TextView) view.findViewById(R.id.successMsg);
        editLat = (EditText) view.findViewById(R.id.editLat);
        editLong = (EditText) view.findViewById(R.id.editLong);
        btnRegister = (Button) view.findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = getUsername();

                if(username.equals("")){
                    showNoUsernameWarning();
                }else{
                    btnRegister.setText(R.string.proccessing_text);
                    txtSuccess.setVisibility(View.INVISIBLE);

                    ParseObject GasStation = new ParseObject("GasStation");
                    ParseGeoPoint point = new ParseGeoPoint(latitude, longitude);

                    GasStation.put("point", point);
                    GasStation.put("username", username);
                    GasStation.pinInBackground();

                    btnRegister.setText(R.string.register_text);
                    txtSuccess.setVisibility(View.VISIBLE);
                }
            }
        });

        // Create Map
        MapView mapView = (MapView) view.findViewById(R.id.mapNow);
        mapView.getMapAsync(this);
		mapView.onCreate(savedInstanceState);
		mapView.onResume();
		MapsInitializer.initialize(getActivity().getApplicationContext());
        
		return view;
	}

    @Override
    public void onMapReady(final GoogleMap map) {
        this.map = map;

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

                latitude = location.getLatitude();
                longitude = location.getLongitude();
                editLat.setText(String.valueOf(location.getLatitude()));
                editLong.setText(String.valueOf(location.getLongitude()));

                // Make sure zooming only happened once when the first time location detected.
                if(locationUpdateCounter == 0){
                    // Once first location coordinate gathered, show save button.
                    btnRegister.setVisibility(View.VISIBLE);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(location.getLatitude(), location.getLongitude()), 17)
                    );
                }
                locationUpdateCounter++;
            }
        });
    }

    private String getUsername(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return sp.getString(getString(R.string.preference_username_key), "");
    }

    private void showNoUsernameWarning(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.username_warning_title);
        builder.setMessage(R.string.username_warning_message);
        builder.setPositiveButton(R.string.open_settings_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });

        builder.setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
