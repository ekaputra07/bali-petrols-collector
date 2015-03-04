package com.balitechy.gasstregister;

import android.content.Context;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.Date;

public class LocationItem {
    private Context context;
    private ParseObject parseObject;
    private String syncStatus;

    LocationItem(ParseObject parseObject, Context context) {

        this.parseObject = parseObject;
        this.context = context;

        Date created = null;
        String address = null;
        try {
            created = parseObject.getCreatedAt();
            address = parseObject.getString("address");
        } finally {
            if (created == null && address == null) {
                syncStatus = "NOT SET";
            } else {
                if(address != null){
                    syncStatus = address;
                }else{
                    syncStatus = created.toString();
                }
            }
        }
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public ParseObject getParseObject() {
        return parseObject;
    }

    public void syncParseObject(final OnLocationItemSyncListener syncListener) {
        syncStatus = context.getResources().getString(R.string.sync_start_text);
        syncListener.onSyncStart();

        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    syncStatus = context.getResources().getString(R.string.sync_success_text);
                    parseObject.unpinInBackground();
                    syncListener.onSyncDone(false);
                } else {
                    syncStatus = context.getResources().getString(R.string.sync_failed_text);
                    syncListener.onSyncDone(true);
                }
            }
        });
    }

    public interface OnLocationItemSyncListener {
        public void onSyncDone(Boolean isError);

        public void onSyncStart();
    }
}
