package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;

import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.ContextType;
import com.intel.context.item.Item;
import com.intel.context.item.cloud.Date;

public class DateListener extends IApplicationListener {

    private final String LOG_TAG = DateListener.class.getName();

    public DateListener(UpdateNotifier updateNotifier, int index) {
    	super("Date", ContextType.DATE, updateNotifier, index);
    }
    
    @Override
    protected EventFilter constructEventFilter() {
        return null; // No event filter for this provider.
    }
    
    public void onReceive(Item state) {
        if (state instanceof Date) {
        	setLastKnownItem(state);
        	notifyUpdate();
        }
    }

    public void onError(ContextError error) {
    	mUpdateNotifier.notifyError("DateListener error: " + error.getMessage());
        Log.e(LOG_TAG, "Error: " + error.getMessage());
    }

	@Override
	protected String describeItem(Item item) {
		 Date date = (Date) item;
 
         StringBuilder sb = new StringBuilder();
         sb.append("Part Of Week: " + date.getPartOfWeek());
         sb.append("\nDay: " + date.getDay());
         sb.append("\nSeason: " + date.getSeason());
         Log.d(LOG_TAG, "New Date State: " + sb.toString());
         return sb.toString(); 
    }	
	
	@Override
	protected void addProviderOptions(List<ProviderOption> providerOptions) {
	}
	
    @Override
    public boolean shouldNotStartSensing() {
        return true;
    }
}

