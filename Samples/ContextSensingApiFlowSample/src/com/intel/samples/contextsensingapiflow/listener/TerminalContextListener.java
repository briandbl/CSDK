package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;

import android.os.Bundle;
import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.ContextType;
import com.intel.context.item.Item;
import com.intel.context.item.TerminalContext;
import com.intel.context.option.ContinuousFlag;
import com.intel.context.option.terminalcontext.TerminalContextOptionBuilder;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleApplication;

public class TerminalContextListener extends IApplicationListener {

    private final String LOG_TAG = ContextSensingApiFlowSampleApplication.class.getName();

    public TerminalContextListener(UpdateNotifier updateNotifier, int index) {
    	super("Terminal Context", ContextType.TERMINAL_CONTEXT, updateNotifier, index);
    }
    
    @Override
    protected EventFilter constructEventFilter() {
        return null; // No event filter for this provider.
    }
    
	@Override
	public void onReceive(Item state) {
		if(state instanceof TerminalContext) {
			setLastKnownItem(state);
			notifyUpdate();
		}
	}

	@Override
	public void onError(ContextError error) {
		mUpdateNotifier.notifyError("TerminalContextListener error: " + error.getMessage());
        Log.e(LOG_TAG, "Error: " + error.getMessage());
	}

	@Override
	protected String describeItem(Item item) {
		TerminalContext terminal = (TerminalContext) item;
        
        StringBuilder sb = new StringBuilder();
        sb.append(terminal.getOrientation().toString());
        sb.append("\n" + terminal.getFace().toString());

        return sb.toString();
	}
	
	@Override
	protected void addProviderOptions(List<ProviderOption> providerOptions) {
		providerOptions.add(ProviderOption.createFromEnum(ContinuousFlag.class, ContinuousFlag.PAUSE_ON_SLEEP));
	}
	
	@Override
	public Bundle getProviderOptionsBundle() {
		TerminalContextOptionBuilder terminalContextSettings = new TerminalContextOptionBuilder();
		terminalContextSettings.setSensorHubContinuousFlag(getProviderOptionEnum(ContinuousFlag.class));
		
		return terminalContextSettings.toBundle();
	}
	
	@Override
    public boolean shouldNotStartSensing() {
        return false;
    }
}


