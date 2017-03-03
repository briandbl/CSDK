package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;

import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.ContextType;
import com.intel.context.item.EarTouchItem;
import com.intel.context.item.Item;
import com.intel.context.item.eartouch.EarTouchType;
import com.intel.context.option.ContinuousFlag;
import com.intel.context.option.eartouch.GestureEarTouchOptionBuilder;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleApplication;

public class EarTouchListener extends IApplicationListener {
	private final String LOG_TAG = ContextSensingApiFlowSampleApplication.class.getName();

    public EarTouchListener(UpdateNotifier updateNotifier, int index) {
    	super("Ear Touch", ContextType.GESTURE_EAR_TOUCH, updateNotifier, index);
    }
    
    @Override
    protected EventFilter constructEventFilter() {
        return EventFilter.createFromEnum(EarTouchType.class, EarTouchType.values());
    }
    
	@Override
	public void onReceive(Item state) {
		if(state instanceof EarTouchItem) {
			setLastKnownItem(state);
			notifyUpdate();
		}
	}

	@Override
	public void onError(ContextError error) {
		mUpdateNotifier.notifyError("EarTouchListener error: " + error.getMessage());
        Log.e(LOG_TAG, "Error: " + error.getMessage());
	}

	@Override
	protected String describeItem(Item item) {
		EarTouchItem earTouchItem = (EarTouchItem) item;

		StringBuilder sb = new StringBuilder();
		sb.append(earTouchItem.getType());
		Log.d(LOG_TAG, "New Ear Touch State: " + sb.toString());
		return sb.toString();
	}
	
	@Override
	protected void addProviderOptions(List<ProviderOption> providerOptions) {
		providerOptions.add(ProviderOption.createFromEnum(ContinuousFlag.class, ContinuousFlag.PAUSE_ON_SLEEP));
	}
	
	@Override
	public Bundle getProviderOptionsBundle() {
		 GestureEarTouchOptionBuilder earTouchSetting = new GestureEarTouchOptionBuilder();
		 earTouchSetting.setSensorHubContinuousFlag(getProviderOptionEnum(ContinuousFlag.class));
		 earTouchSetting.setFilter(getEventFilter().getAsEnumArray(EarTouchType.class));
		 return earTouchSetting.toBundle();
	}
	
	@Override
    public boolean shouldNotStartSensing() {
        return false;
    }
}