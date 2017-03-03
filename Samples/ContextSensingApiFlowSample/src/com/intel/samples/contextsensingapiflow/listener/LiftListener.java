package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;

import android.os.Bundle;
import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.ContextType;
import com.intel.context.item.Item;
import com.intel.context.item.LiftItem;
import com.intel.context.option.ContinuousFlag;
import com.intel.context.option.lift.LiftOptionBuilder;

public class LiftListener  extends IApplicationListener {

    private final String LOG_TAG = LiftListener.class.getName();
    
    public LiftListener(UpdateNotifier updateNotifier, int index) {
    	super("Lift", ContextType.LIFT, updateNotifier, index);
    }
    
    @Override
    protected EventFilter constructEventFilter() {
        return null; // No event filter for this provider.
    }
    
	@Override
	public void onReceive(Item state) {
		if(state instanceof LiftItem) {
			setLastKnownItem(state);
			notifyUpdate();
		}
	}

	@Override
	public void onError(ContextError error) {
		mUpdateNotifier.notifyError("LiftListener error: " + error.getMessage());
        Log.e(LOG_TAG, "Error: " + error.getMessage());
	}

	@Override
	protected String describeItem(Item item) {
		LiftItem liftItem = (LiftItem) item;

        StringBuilder sb = new StringBuilder();
        sb.append("Look: " + liftItem.getLook());
        sb.append("\nVertical: " + liftItem.getVertical());
        Log.d(LOG_TAG, "New Lift State: " + sb.toString());
        return sb.toString();      
	}
	
    @Override
    protected void addProviderOptions(List<ProviderOption> providerOptions) {
        providerOptions.add(ProviderOption.createFromEnum(LookDetection.class, LookDetection.ENABLED));
        providerOptions.add(ProviderOption.createFromEnum(VerticalDetection.class, VerticalDetection.ENABLED));
        providerOptions.add(ProviderOption.createFromEnum(ContinuousFlag.class, ContinuousFlag.PAUSE_ON_SLEEP));
    }
	
	@Override
	public Bundle getProviderOptionsBundle() {
		LiftOptionBuilder liftSetting = new LiftOptionBuilder();
		liftSetting.setLookDetector(getProviderOptionEnum(LookDetection.class) == LookDetection.ENABLED);
		liftSetting.setLookDetector(getProviderOptionEnum(VerticalDetection.class) == VerticalDetection.ENABLED);
		liftSetting.setSensorHubContinuousFlag(getProviderOptionEnum(ContinuousFlag.class));
		
		return liftSetting.toBundle();
	}
	
	@Override
    public boolean shouldNotStartSensing() {
        return false;
    }
	
	public static enum LookDetection {
	    ENABLED,
	    DISABLED
	}
	
	public static enum VerticalDetection {
	    ENABLED,
	    DISABLED
	}
}

