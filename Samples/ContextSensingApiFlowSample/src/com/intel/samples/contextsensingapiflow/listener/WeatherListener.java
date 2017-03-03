package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;

import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.ContextType;
import com.intel.context.item.Item;
import com.intel.context.item.cloud.Weather;

public class WeatherListener extends IApplicationListener {

    private final String LOG_TAG = WeatherListener.class.getName();

    public WeatherListener(UpdateNotifier updateNotifier, int index) {
    	super("Weather", ContextType.WEATHER, updateNotifier, index);
    }
    
    @Override
    protected EventFilter constructEventFilter() {
        return null; // No event filter for this provider.
    }
    
	@Override
	public void onReceive(Item state) {
		if(state instanceof Weather) {
			setLastKnownItem(state);
			notifyUpdate();
		}
	}

	@Override
	public void onError(ContextError error) {
		mUpdateNotifier.notifyError("WeatherListener error: " + error.getMessage());
        Log.e(LOG_TAG, "Error: " + error.getMessage());
	}

	@Override
	protected String describeItem(Item item) {
        Weather weather = (Weather) item;
        
        StringBuilder sb = new StringBuilder();
        sb.append("Condition: " + weather.getCondition());
        sb.append("\nTemp.: " + weather.getTemperature().getImperial());
        sb.append("\nHumidity: " + weather.getHumidity());
        Log.d(LOG_TAG, "New Weather State: " + sb.toString());
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

