package com.intel.samples.contextsensingapiflow.listener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.os.Bundle;
import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.ContextType;
import com.intel.context.item.Item;
import com.intel.context.item.ShakingItem;
import com.intel.context.option.ContinuousFlag;
import com.intel.context.option.Sensitivity;
import com.intel.context.option.shaking.ShakingOptionBuilder;

public class ShakingListener extends IApplicationListener {

    private final String LOG_TAG = ShakingListener.class.getName();
    
    public ShakingListener(UpdateNotifier updateNotifier, int index) {
    	super("Shaking", ContextType.SHAKING, updateNotifier, index);
    }
    
    @Override
    protected EventFilter constructEventFilter() {
        return null; // No event filter for this provider.
    }
     
	@Override
	public void onReceive(Item state) {
		if(state instanceof ShakingItem) {
			setLastKnownItem(state);
			notifyUpdate();
		}
	}

	@Override
	public void onError(ContextError error) {
		mUpdateNotifier.notifyError("ShakingListener error: " + error.getMessage());
        Log.e(LOG_TAG, "Error: " + error.getMessage());
	}

	@Override
	protected String describeItem(Item item) {
		ShakingItem shakingItem = (ShakingItem) item;
		return "Shake Detected.";
	}
	
    @Override
    protected void addProviderOptions(List<ProviderOption> providerOptions) {
        providerOptions.add(ProviderOption.createFromEnum(ContinuousFlag.class, ContinuousFlag.PAUSE_ON_SLEEP));
    }
	
	@Override
	public Bundle getProviderOptionsBundle() {
		ShakingOptionBuilder shakingSetting = new ShakingOptionBuilder();
 		shakingSetting.setSensorHubContinuousFlag(getProviderOptionEnum(ContinuousFlag.class));

		return shakingSetting.toBundle();
	}
	
	@Override
    public boolean shouldNotStartSensing() {
        return false;
    }
}

