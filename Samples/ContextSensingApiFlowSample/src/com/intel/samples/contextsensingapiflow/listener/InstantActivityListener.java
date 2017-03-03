package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;

import android.os.Bundle;
import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.ContextType;
import com.intel.context.item.InstantActivityItem;
import com.intel.context.item.Item;
import com.intel.context.item.instantactivity.InstantActivityType;
import com.intel.context.option.ContinuousFlag;
import com.intel.context.option.instantactivity.InstantActivityOptionBuilder;

public class InstantActivityListener extends IApplicationListener {

    private final String LOG_TAG = InstantActivityListener.class.getName();

    public InstantActivityListener(UpdateNotifier updateNotifier, int index) {
    	super("Instant Activity", ContextType.INSTANT_ACTIVITY, updateNotifier, index);
    }
    
    @Override
    protected EventFilter constructEventFilter() {
        return EventFilter.createFromEnum(InstantActivityType.class, InstantActivityType.values());
    }
    
	@Override
	public void onReceive(Item state) {
		if(state instanceof InstantActivityItem) {
			setLastKnownItem(state);
			notifyUpdate();
		}
	}

	@Override
	public void onError(ContextError error) {
		mUpdateNotifier.notifyError("InstantActivityListener error: " + error.getMessage());
        Log.e(LOG_TAG, "Error: " + error.getMessage());
	}

	@Override
	protected String describeItem(Item item) {
		InstantActivityItem instantActivityItem = (InstantActivityItem) item;
        return instantActivityItem.getType().toString();           
	}
	
	@Override
	protected void addProviderOptions(List<ProviderOption> providerOptions) {
		providerOptions.add(ProviderOption.createFromEnum(ContinuousFlag.class, ContinuousFlag.PAUSE_ON_SLEEP));
	}
	
	@Override
	public Bundle getProviderOptionsBundle() {
		 InstantActivityOptionBuilder instantActivitySetting = new InstantActivityOptionBuilder();
		 instantActivitySetting.setSensorHubContinuousFlag(getProviderOptionEnum(ContinuousFlag.class));
		 instantActivitySetting.setFilter(getEventFilter().getAsEnumArray(InstantActivityType.class));
		 
		 return instantActivitySetting.toBundle();
	}
	
	@Override
    public boolean shouldNotStartSensing() {
        return false;
    }
}

