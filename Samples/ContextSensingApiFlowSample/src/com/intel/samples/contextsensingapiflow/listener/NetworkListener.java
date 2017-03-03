package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;

import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.ContextType;
import com.intel.context.item.Item;
import com.intel.context.item.Network;

public class NetworkListener extends IApplicationListener {

    private final String LOG_TAG = NetworkListener.class.getName();

    public NetworkListener(UpdateNotifier updateNotifier, int index) {
    	super("Network", ContextType.NETWORK, updateNotifier, index);
    }
    
    @Override
    protected EventFilter constructEventFilter() {
        return null; // No event filter for this provider.
    }
    
	@Override
	public void onReceive(Item state) {
		if(state instanceof Network) {
			setLastKnownItem(state);
			notifyUpdate();
		}
	}

	@Override
	public void onError(ContextError error) {
		mUpdateNotifier.notifyError("NetworkListener error: " + error.getMessage());
        Log.e(LOG_TAG, "Error: " + error.getMessage());
	}

	@Override
	protected String describeItem(Item item) {
		Network network = (Network) item;
        
        StringBuilder sb = new StringBuilder();
        sb.append("Type: " + network.getNetworkType());
        sb.append("\nSSID: " + network.getSsid());
        sb.append("\nSignal: " + network.getSignalStrength());
        sb.append("\nTraffic sent: " + network.getTrafficSent());

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

