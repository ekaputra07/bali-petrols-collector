package com.balitechy.gasstregister;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseObject;

public class LocationListAdapter extends BaseAdapter {
	private Context context;
	private int layoutId;
	private List<LocationItem> items;

	public LocationListAdapter(Context context, int layoutId, List<LocationItem> items) {
		this.context = context;
		this.layoutId = layoutId;
		this.items = items;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(layoutId, parent, false);
		}

        LocationItem item = items.get(position);
		ParseObject parseObject = item.getParseObject();

		TextView coordinateText = (TextView) convertView.findViewById(R.id.item_coordinate);
		coordinateText.setText(parseObject.getParseGeoPoint("point").getLatitude() + ", " + parseObject.getParseGeoPoint("point").getLongitude());

        TextView createdText = (TextView) convertView.findViewById(R.id.item_created);
        createdText.setText(item.getSyncStatus());
		
		return convertView;
	}

}
