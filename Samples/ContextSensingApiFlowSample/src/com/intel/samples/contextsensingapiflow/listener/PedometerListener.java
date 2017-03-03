package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;

import android.os.Bundle;
import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.ContextType;
import com.intel.context.item.Item;
import com.intel.context.item.Pedometer;
import com.intel.context.option.ContinuousFlag;
import com.intel.context.option.pedometer.Mode;
import com.intel.context.option.pedometer.PedometerOptionBuilder;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleApplication;

public class PedometerListener extends IApplicationListener {

    private final String LOG_TAG = ContextSensingApiFlowSampleApplication.class.getName();

    public PedometerListener(UpdateNotifier updateNotifier, int index) {
    	super("Pedometer", ContextType.PEDOMETER, updateNotifier, index);
    }
    
    @Override
    protected EventFilter constructEventFilter() {
        return null; // No event filter for this provider.
    }
    
	@Override
	public void onReceive(Item state) {
		if(state instanceof Pedometer) {
			setLastKnownItem(state);
			notifyUpdate();
		}
	}

	@Override
	public void onError(ContextError error) {
		mUpdateNotifier.notifyError("PedometerListener error: " + error.getMessage());
        Log.e(LOG_TAG, "Error: " + error.getMessage());
	}

	@Override
	protected String describeItem(Item item) {
		Pedometer pedometer = (Pedometer) item;
        
        StringBuilder sb = new StringBuilder();
        sb.append("Steps: " + pedometer.getSteps());
        sb.append("\n");
        sb.append("AfternoonSteps: " +pedometer.getAfternoonSteps());
        sb.append("\n");
        sb.append("EveningSteps: " +pedometer.getEveningSteps());
        sb.append("\n");
        sb.append("MidnightSteps: " +pedometer.getMidnightSteps());
        sb.append("\n");
        sb.append("MorningSteps: " +pedometer.getMorningSteps());
        sb.append("\n");
        sb.append("NightSteps: " +pedometer.getNightSteps());
        sb.append("\n");
        sb.append("NoonSteps: " +pedometer.getNoonSteps());
        
        return sb.toString();
	}
	
    @Override
    protected void addProviderOptions(List<ProviderOption> providerOptions) {
        providerOptions.add(ProviderOption.createFromEnum(Mode.class, Mode.ON_CHANGE));
        providerOptions.add(ProviderOption.createFromEnum(N_HUNDREDS.class, N_HUNDREDS.ZERO));
        providerOptions.add(ProviderOption.createFromEnum(ContinuousFlag.class, ContinuousFlag.PAUSE_ON_SLEEP));
    }
    
	@Override
	public Bundle getProviderOptionsBundle() {
		PedometerOptionBuilder pedometerSettings = new PedometerOptionBuilder();
		pedometerSettings.setMode(getProviderOptionEnum(Mode.class));
		pedometerSettings.setNHundreds(getProviderOptionEnum(N_HUNDREDS.class).getIntValue());
		pedometerSettings.setSensorHubContinuousFlag(getProviderOptionEnum(ContinuousFlag.class));
		
		return pedometerSettings.toBundle();
	}
	
	public static enum N_HUNDREDS {
	    ZERO(0),
	    ONE(1),
	    TWO(2),
	    THREE(3),
	    FOUR(4),
	    FIVE(5);
	    
	    private final int mValue;
	    private N_HUNDREDS(int value) {
	        mValue = value;
	    }
	    
	    public int getIntValue() {
	        return mValue;
	    }
	}
	
	@Override
    public boolean shouldNotStartSensing() {
        return false;
    }
}

