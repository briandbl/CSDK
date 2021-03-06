package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;

import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.ContextType;
import com.intel.context.item.Item;
import com.intel.context.item.cloud.Place;

public class PlaceListener extends IApplicationListener {

    private final String LOG_TAG = PlaceListener.class.getName();

    public PlaceListener(UpdateNotifier updateNotifier, int index) {
    	super("Place", ContextType.PLACE, updateNotifier, index);
    }
    
    @Override
    protected EventFilter constructEventFilter() {
        return null; // No event filter for this provider.
    }
    
	@Override
	public void onReceive(Item state) {
		if(state instanceof Place) {
			setLastKnownItem(state);
			notifyUpdate();
		}
	}

	@Override
	public void onError(ContextError error) {
		mUpdateNotifier.notifyError("PlaceListener error: " + error.getMessage());
        Log.e(LOG_TAG, "Error: " + error.getMessage());
	}

	@Override
	protected String describeItem(Item item) {
		Place place = (Place) item;
        
        StringBuilder sb = new StringBuilder();
        sb.append("Type: " + place.getType());

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

