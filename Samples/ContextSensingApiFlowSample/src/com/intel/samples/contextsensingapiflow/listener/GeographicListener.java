package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;

import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.ContextType;
import com.intel.context.item.Item;
import com.intel.context.item.cloud.Geographic;

public class GeographicListener extends IApplicationListener {

    private final String LOG_TAG = GeographicListener.class.getName();

    public GeographicListener(UpdateNotifier updateNotifier, int index) {
    	super("Geographic", ContextType.GEOGRAPHIC, updateNotifier, index);
    }
    
    @Override
    protected EventFilter constructEventFilter() {
        return null; // No event filter for this provider.
    }
    
	@Override
	public void onReceive(Item state) {
		if(state instanceof Geographic) {
			setLastKnownItem(state);
			notifyUpdate();
		}
	}

	@Override
	public void onError(ContextError error) {
		mUpdateNotifier.notifyError("GeographicListener error: " + error.getMessage());
        Log.e(LOG_TAG, "Error: " + error.getMessage());
	}

	@Override
	protected String describeItem(Item item) {
		Geographic geographic = (Geographic) item;

        StringBuilder sb = new StringBuilder();
        sb.append("Continent: " + geographic.getContinent());
        sb.append("\nCountry: " + geographic.getCountry());
        sb.append("\nCity: " + geographic.getCity());
        Log.d(LOG_TAG, "New Geographic State: " + sb.toString());
        return sb.toString();
	}
	
	@Override
    public boolean shouldNotStartSensing() {
        return true;
    }

	@Override
	protected void addProviderOptions(List<ProviderOption> providerOptions) {	
	}
}

