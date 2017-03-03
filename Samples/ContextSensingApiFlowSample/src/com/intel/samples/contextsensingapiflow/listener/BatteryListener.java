package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;

import android.os.Bundle;
import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.Battery;
import com.intel.context.item.ContextType;
import com.intel.context.item.Item;

public class BatteryListener extends IApplicationListener {

    private final String LOG_TAG = BatteryListener.class.getName();
    
    public BatteryListener(UpdateNotifier updateListener, int index) {
        super("Battery", ContextType.BATTERY, updateListener, index);
    }
    
    @Override
    protected EventFilter constructEventFilter() {
        return null; // No event filter for this provider.
    }
    
    public void onReceive(Item state) {
        if (state instanceof Battery) {
            setLastKnownItem(state);
            notifyUpdate();
        }
    }

    public void onError(ContextError error) {
        mUpdateNotifier.notifyError("BatteryListener error: " + error.getMessage());
        Log.e(LOG_TAG, "Error: " + error.getMessage());
    }

	@Override
	public String describeItem(Item item) {
        Battery battery = (Battery) item;
        
        StringBuilder sb = new StringBuilder();
        sb.append("Level: " + battery.getLevel());
        sb.append("\nTemperature: " + battery.getTemperature());
        sb.append("\nTime on battery: " + battery.getTimeOnBattery());
        sb.append("\nBattery present: " + battery.getBatteryPresent());
        sb.append("\nRemaining Battery Life: " + battery.getRemainingBatteryLife());
        sb.append("\nStatus: " + battery.getStatus());
        sb.append("\nPlugged: "  + battery.getPlugged());
        
        String status = sb.toString();
        Log.d(LOG_TAG, "New Battery State: " + status);
        return status;
	}
	
    @Override
    protected void addProviderOptions(List<ProviderOption> providerOptions) {
        providerOptions.add(ProviderOption.createFromEnum(OPERATION_MODE.class, OPERATION_MODE.FULL_MODE));
        providerOptions.add(ProviderOption.createFromEnum(MONITOR_INTERVAL.class, MONITOR_INTERVAL.TWO_MINUTES));
    }
	
	@Override
	public Bundle getProviderOptionsBundle() {
		
		OPERATION_MODE mode = getProviderOptionEnum(OPERATION_MODE.class);
	    
		Bundle settings = new Bundle();
		settings.putString(OPERATION_MODE.class.getSimpleName(), mode.toString());
	    
		return settings;
	}
	
	public static enum OPERATION_MODE {
	    FULL_MODE,
	    MEDIUM_MODE,
	    ECONOMIZER_MODE
	}
	
	@Override
    public boolean shouldNotStartSensing() {
        return false;
    }
}

