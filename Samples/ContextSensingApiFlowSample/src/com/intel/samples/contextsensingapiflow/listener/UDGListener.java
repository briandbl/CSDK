package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;

import android.os.Bundle;
import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.ContextType;
import com.intel.context.item.Item;
import com.intel.context.item.UDGItem;
import com.intel.context.option.ContinuousFlag;
import com.intel.context.option.udg.Index;
import com.intel.context.option.udg.Mode;
import com.intel.context.option.udg.UDGOptionBuilder;

public class UDGListener extends IApplicationListener {

    private final String LOG_TAG = UDGListener.class.getName();
    
    public UDGListener(UpdateNotifier updateNotifier, int listenerIndex) {
    	super("UDG", ContextType.USER_DEFINED_GESTURE, updateNotifier, listenerIndex);
    }
    
    @Override
    protected EventFilter constructEventFilter() {
        return null; // No modifiable event filter for this provider.
    }
    
	@Override
	public void onReceive(Item state) {
		if(state instanceof UDGItem) {
			setLastKnownItem(state);
			notifyUpdate();
		}
	}
	
	@Override
	protected String getAdditionalNameInfo() {
	    return String.format("(%s, %s)", getProviderOptionEnum(Mode.class), getProviderOptionEnum(Index.class));
	}

	@Override
	public void onError(ContextError error) {
		mUpdateNotifier.notifyError("UDGListener error: " + error.getMessage());
        Log.e(LOG_TAG, "Error: " + error.getMessage());
	}

	@Override
	protected String describeItem(Item item) {
		UDGItem UDGItem = (UDGItem) item;
       
        StringBuilder sb = new StringBuilder();
        sb.append("Mode: " + UDGItem.getMode());
        sb.append("\nIndex: " + UDGItem.getIndex());
        sb.append("\nEnrollCode: "+ UDGItem.getEnrollCode());
        Log.d(LOG_TAG, "New UDG state: " + sb.toString());
        return sb.toString();
	}
	
	@Override
	protected void addProviderOptions(List<ProviderOption> providerOptions) {
		providerOptions.add(ProviderOption.createFromEnum(Mode.class, Mode.ENROLL));
		providerOptions.add(ProviderOption.createFromEnum(Index.class, Index.FIRST));
		providerOptions.add(ProviderOption.createFromEnum(ContinuousFlag.class, ContinuousFlag.PAUSE_ON_SLEEP));
	}
	
	@Override
	public Bundle getProviderOptionsBundle() {
		UDGOptionBuilder options = new UDGOptionBuilder();
		options.setSensorHubContinuousFlag(getProviderOptionEnum(ContinuousFlag.class));
		options.setMode(getProviderOptionEnum(Mode.class));
		Index index = getProviderOptionEnum(Index.class);
        options.setIndex(index);
		options.setFilter(new Index[] { index });
		
		return options.toBundle();
	}
	
	@Override
    public boolean shouldNotStartSensing() {
        return false;
    }
}


