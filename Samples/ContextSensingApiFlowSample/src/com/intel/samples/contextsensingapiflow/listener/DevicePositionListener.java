package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;

import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.ContextType;
import com.intel.context.item.DevicePositionItem;
import com.intel.context.item.Item;
import com.intel.context.option.ContinuousFlag;
import com.intel.context.option.ProcessingUnit;
import com.intel.context.option.deviceposition.DevicePositionOptionBuilder;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleApplication;

public class DevicePositionListener extends IApplicationListener {
	private final String LOG_TAG = ContextSensingApiFlowSampleApplication.class.getName();

    public DevicePositionListener(UpdateNotifier updateNotifier, int index) {
    	super("Device Position", ContextType.DEVICE_POSITION, updateNotifier, index);
    }
    
    @Override
    protected EventFilter constructEventFilter() {
        return null; // No event filter for this provider.
    }
    
	@Override
	public void onReceive(Item state) {
		if(state instanceof DevicePositionItem) {
			setLastKnownItem(state);
			notifyUpdate();
		}
	}

	@Override
	public void onError(ContextError error) {
		mUpdateNotifier.notifyError("DevicePositionListener error: " + error.getMessage());
        Log.e(LOG_TAG, "Error: " + error.getMessage());
	}

	@Override
	protected String describeItem(Item item) {
		DevicePositionItem devicePositionItem = (DevicePositionItem) item;

		StringBuilder sb = new StringBuilder();
		sb.append(devicePositionItem.getType());
		Log.d(LOG_TAG, "New DevicePosition State: " + sb.toString());
		return sb.toString();
	}

	@Override
	protected void addProviderOptions(List<ProviderOption> providerOptions) {
		providerOptions.add(ProviderOption.createFromEnum(ContinuousFlag.class, ContinuousFlag.PAUSE_ON_SLEEP));
	}
	
    @Override
    public Bundle getProviderOptionsBundle() {
    	DevicePositionOptionBuilder activitySettings = new DevicePositionOptionBuilder();
        activitySettings.setSensorHubContinuousFlag(getProviderOptionEnum(ContinuousFlag.class));
        activitySettings.setProcessingUnit(ProcessingUnit.DEFAULT);

        return activitySettings.toBundle();
    }
    
	@Override
    public boolean shouldNotStartSensing() {
        return false;
    }
}