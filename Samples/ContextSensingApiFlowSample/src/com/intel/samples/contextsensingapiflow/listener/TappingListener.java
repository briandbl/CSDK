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
import com.intel.context.item.TappingItem;
import com.intel.context.item.tapping.TappingType;
import com.intel.context.option.ContinuousFlag;
import com.intel.context.option.Sensitivity;
import com.intel.context.option.tapping.TappingOptionBuilder;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleApplication;

public class TappingListener extends IApplicationListener {
	private final String LOG_TAG = ContextSensingApiFlowSampleApplication.class.getName();
    
	public TappingListener(UpdateNotifier updateNotifier, int index) {
    	super("Tapping", ContextType.TAPPING, updateNotifier, index);
    }
	
	@Override
	protected EventFilter constructEventFilter() {
	    return EventFilter.createFromEnum(TappingType.class, TappingType.values());
	}
	
	@Override
	public void onReceive(Item state) {
		if(state instanceof TappingItem) {
			setLastKnownItem(state);
			notifyUpdate();
		}
	}

	@Override
	public void onError(ContextError error) {
		mUpdateNotifier.notifyError("TappingListner error: " + error.getMessage());
        Log.e(LOG_TAG, "Error: " + error.getMessage());
	}

	@Override
	protected String describeItem(Item item) {
		TappingItem tappingItem = (TappingItem) item;

		StringBuilder sb = new StringBuilder();
		sb.append(tappingItem.getType());
		
		return sb.toString();
	}
	
	@Override
	protected void addProviderOptions(List<ProviderOption> providerOptions) {
	    providerOptions.add(ProviderOption.createFromEnum(Sensitivity.class, Sensitivity.MIDDLE));
	    providerOptions.add(ProviderOption.createFromEnum(ContinuousFlag.class, ContinuousFlag.PAUSE_ON_SLEEP));
	}
	
	@Override
	public Bundle getProviderOptionsBundle() {
		TappingOptionBuilder tappingSetting = new TappingOptionBuilder();
		tappingSetting.setSensitivity(getProviderOptionEnum(Sensitivity.class));
		tappingSetting.setSensorHubContinuousFlag(getProviderOptionEnum(ContinuousFlag.class));
		tappingSetting.setFilter(getEventFilter().getAsEnumArray(TappingType.class));
		
		return tappingSetting.toBundle();
	}
	
	@Override
    public boolean shouldNotStartSensing() {
        return false;
    }
}