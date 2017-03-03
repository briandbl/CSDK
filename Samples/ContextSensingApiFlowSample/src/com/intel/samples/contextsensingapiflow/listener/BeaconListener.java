package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;

import android.os.Bundle;
import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.Beacons;
import com.intel.context.item.beacons.BeaconInfo;
import com.intel.context.item.ContextType;
import com.intel.context.item.Item;
import com.intel.samples.contextsensingapiflow.listener.InstalledAppsListener.HARVESTING_MODE;
import com.intel.samples.contextsensingapiflow.listener.InstalledAppsListener.MAX_APP_QUANTITY;

public class BeaconListener extends IApplicationListener {

    private final String LOG_TAG = BeaconListener.class.getName();
    
    public BeaconListener(UpdateNotifier updateListener, int index) {
        super("Beacons", ContextType.BEACONS, updateListener, index);
    }
    
    @Override
    protected EventFilter constructEventFilter() {
        return null; // No event filter for this provider.
    }
    
    public void onReceive(Item state) {
        if (state instanceof Beacons) {
            setLastKnownItem(state);
            notifyUpdate();
        }
    }

    public void onError(ContextError error) {
        mUpdateNotifier.notifyError("BeaconListener error: " + error.getMessage());
        Log.e(LOG_TAG, "Error: " + error.getMessage());
    }

	@Override
	public String describeItem(Item item) {
        Beacons beacons = (Beacons) item;
        
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for(BeaconInfo beacon:beacons.getBeacons())
        {
	        sb.append("MAC: " + beacon.getMACAddress());
	        sb.append("\nUUID: " + beacon.getUUID());
	        sb.append("\nRSSI: " + beacon.getRSSILevel());
	        sb.append("\nDistance: " + beacon.getDistance());
	        sb.append("\nStatus: " + beacon.getStatus());
	        
	        if(++count < beacons.getBeacons().size())sb.append("\n\n");
        }
        
        String status = sb.toString();
        Log.d(LOG_TAG, "New Beacon State: " + status);
        return status;
	}
	
    @Override
    protected void addProviderOptions(List<ProviderOption> providerOptions) {
        providerOptions.add(ProviderOption.createFromEnum(SCAN_INTERVAL.class, SCAN_INTERVAL.FIVE_SECONDS));
        providerOptions.add(ProviderOption.createFromEnum(MONITOR_INTERVAL.class, MONITOR_INTERVAL.THIRTY_SECONDS));
    }
	
    @Override
	public Bundle getProviderOptionsBundle() {
	    Bundle bundle = new Bundle();
	    bundle.putInt(SCAN_INTERVAL.class.getSimpleName(), getProviderOptionEnum(SCAN_INTERVAL.class).getInterval());
	    bundle.putInt(MONITOR_INTERVAL.class.getSimpleName(), getProviderOptionEnum(MONITOR_INTERVAL.class).getInterval());
	    
	    return bundle;
	}
	
	public enum SCAN_INTERVAL {
	    
	    ONE_SECOND(1000),
	    FIVE_SECONDS(5000),
	    TEN_SECONDS(10000),
	    FIFTEEN_SECONDS(15000),
	    TWENTY_SECONDS(20000),
	    TWENTY_FIVE_SECONDS(25000),
	    THIRTY_SECONDS(30000),
	    ONE_MINUTE(60000),
	    TWO_MINUTES(120000),
	    FIVE_MINUTES(300000),
	    TEN_MINUTES(600000);
	    
	    private final int mInterval;
	    
	    private SCAN_INTERVAL(int quantity) {
	        mInterval = quantity;
	    }
	    
	    public int getInterval() {
	        return mInterval;
	    }
	}
	
	@Override
    public boolean shouldNotStartSensing() {
        return false;
    }
}

