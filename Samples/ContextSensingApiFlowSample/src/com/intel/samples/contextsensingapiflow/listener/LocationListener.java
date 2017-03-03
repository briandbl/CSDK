package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;

import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.ContextType;
import com.intel.context.item.Item;
import com.intel.context.item.LocationCurrent;

public class LocationListener extends IApplicationListener {

    private final String LOG_TAG = LocationListener.class.getName();

    public LocationListener(UpdateNotifier updateNotifier, int index) {
    	super("Location", ContextType.LOCATION, updateNotifier, index);
    }
    
    @Override
    protected EventFilter constructEventFilter() {
        return null; // No event filter for this provider.
    }
    
	@Override
	public void onReceive(Item state) {
		if(state instanceof LocationCurrent) {
			setLastKnownItem(state);
			notifyUpdate();
		}
	}

	@Override
	public void onError(ContextError error) {
		mUpdateNotifier.notifyError("LocationListener error: " + error.getMessage());
        Log.e(LOG_TAG, "Error: " + error.getMessage());
	}

	@Override
	protected String describeItem(Item item) {
		LocationCurrent mLocation = (LocationCurrent) item;

        StringBuilder sb = new StringBuilder();
        sb.append(mLocation.getActivity());
        sb.append("\n Latitude: " + mLocation.getLocation().getLatitude());
        sb.append("\n Longitude: " + mLocation.getLocation().getLongitude());

        return sb.toString();
	}
	
	@Override
	protected void addProviderOptions(List<ProviderOption> providerOptions) {
	}
	
	@Override
    public boolean shouldNotStartSensing() {
        return false;
    }


}