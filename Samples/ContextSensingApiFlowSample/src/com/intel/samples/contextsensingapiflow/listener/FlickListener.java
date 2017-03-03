package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;

import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.ContextType;
import com.intel.context.item.FlickItem;
import com.intel.context.item.Item;
import com.intel.context.item.flick.FlickType;
import com.intel.context.option.ContinuousFlag;
import com.intel.context.option.Sensitivity;
import com.intel.context.option.flick.GestureFlickOptionBuilder;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleApplication;

public class FlickListener extends IApplicationListener {
	private final String LOG_TAG = ContextSensingApiFlowSampleApplication.class.getName();

    public FlickListener(UpdateNotifier updateNotifier, int index) {
    	super("Flick", ContextType.GESTURE_FLICK, updateNotifier, index);
    }
    
    @Override
    protected EventFilter constructEventFilter() {
        return EventFilter.createFromEnum(FlickType.class, FlickType.values());
    }
    
	@Override
	public void onReceive(Item state) {
		if(state instanceof FlickItem) {
			setLastKnownItem(state);
			notifyUpdate();
		}
	}

	@Override
	public void onError(ContextError error) {
		mUpdateNotifier.notifyError("FlickListener error: " + error.getMessage());
        Log.e(LOG_TAG, "Error: " + error.getMessage());
	}

	@Override
	protected String describeItem(Item item) {
		FlickItem flickItem = (FlickItem) item;
		StringBuilder sb = new StringBuilder();
		sb.append(flickItem.getType());
		Log.d(LOG_TAG, "New Tapping State: " + sb.toString());
		return sb.toString();
	}
	
	@Override
	protected void addProviderOptions(List<ProviderOption> providerOptions) {
		providerOptions.add(ProviderOption.createFromEnum(ContinuousFlag.class, ContinuousFlag.PAUSE_ON_SLEEP));
		providerOptions.add(ProviderOption.createFromEnum(Sensitivity.class, Sensitivity.MIDDLE));
	}
	
	@Override
	public Bundle getProviderOptionsBundle() {
         GestureFlickOptionBuilder flickSetting = new GestureFlickOptionBuilder();
         flickSetting.setSensorHubContinuousFlag(getProviderOptionEnum(ContinuousFlag.class));
         flickSetting.setSensitivity(getProviderOptionEnum(Sensitivity.class));
         flickSetting.setFilter(getEventFilter().getAsEnumArray(FlickType.class));
		 
         return flickSetting.toBundle();
	}
	
	@Override
    public boolean shouldNotStartSensing() {
        return false;
    }
}