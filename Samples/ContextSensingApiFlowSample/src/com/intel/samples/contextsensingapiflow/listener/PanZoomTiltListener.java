package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;

import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.ContextType;
import com.intel.context.item.Item;
import com.intel.context.item.PanZoomTiltItem;
import com.intel.context.option.ContinuousFlag;
import com.intel.context.option.panzoomtilt.PanZoomTiltOptionBuilder;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleApplication;

public class PanZoomTiltListener extends IApplicationListener {
	private final String LOG_TAG = ContextSensingApiFlowSampleApplication.class.getName();

	public PanZoomTiltListener(UpdateNotifier updateNotifier, int index) {
    	super("PanZoomTilt", ContextType.PANZOOMTILT, updateNotifier, index);
    }
	
	@Override
	protected EventFilter constructEventFilter() {
	    return null; // No event filter for this provider.
	}
    
	@Override
	public void onReceive(Item state) {
		if(state instanceof PanZoomTiltItem) {
			PanZoomTiltItem oldState = (PanZoomTiltItem) getLastKnownItem();
			PanZoomTiltItem newState = (PanZoomTiltItem) state;
			setLastKnownItem(state);		
			if (oldState == null ||
					Math.abs(newState.getDeltaX() - oldState.getDeltaX()) > 2 || 
					Math.abs(newState.getDeltaY() - oldState.getDeltaY()) > 2 ||
					Math.abs(newState.getDeltaZ() - oldState.getDeltaZ()) > 2 ) {
				notifyUpdate();
			}
		}
	}

	@Override
	public void onError(ContextError error) {
		mUpdateNotifier.notifyError("PanZoomTiltListener error: " + error.getMessage());
        Log.e(LOG_TAG, "Error: " + error.getMessage());
	}

	@Override
	protected String describeItem(Item item) {
		PanZoomTiltItem panZoomTiltItem = (PanZoomTiltItem) item;

		StringBuilder sb = new StringBuilder();
		sb.append("Delta X: " +panZoomTiltItem.getDeltaX());
		sb.append("\nDelta Y: " +panZoomTiltItem.getDeltaY());
		sb.append("\nDelta Z: " +panZoomTiltItem.getDeltaZ());

		return sb.toString();
	}
	
	@Override
	protected void addProviderOptions(List<ProviderOption> providerOptions) {
        providerOptions.add(ProviderOption.createFromEnum(ContinuousFlag.class, ContinuousFlag.PAUSE_ON_SLEEP));       
	}
	
    @Override
    public Bundle getProviderOptionsBundle() {

    	PanZoomTiltOptionBuilder panZoomTiltSettings = new PanZoomTiltOptionBuilder();
        panZoomTiltSettings.setSensorHubContinuousFlag(getProviderOptionEnum(ContinuousFlag.class));

        return panZoomTiltSettings.toBundle();
    }
    
	@Override
    public boolean shouldNotStartSensing() {
        return false;
    }
}