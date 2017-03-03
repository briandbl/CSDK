package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;

import android.os.Bundle;
import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.AppsRunning;
import com.intel.context.item.ContextType;
import com.intel.context.item.Item;
import com.intel.context.item.runningapplication.RunningApplicationInfo;

public class RunningAppsListener extends IApplicationListener {

    private final String LOG_TAG = RunningAppsListener.class.getName();
    
    public RunningAppsListener(UpdateNotifier updateNotifier, int index) {
    	super("Running Apps", ContextType.APPS, updateNotifier, index);
    }
    
    @Override
    protected EventFilter constructEventFilter() {
        return null; // No event filter for this provider.
    }
    
	@Override
	public void onReceive(Item state) {
		if(state instanceof AppsRunning) {
			setLastKnownItem(state);
			notifyUpdate();
		}
	}

	@Override
	public void onError(ContextError error) {
		mUpdateNotifier.notifyError("RunningAppsListener error: " + error.getMessage());
        Log.e(LOG_TAG, "Error: " + error.getMessage());
	}

	@Override
	protected String describeItem(Item item) {
		AppsRunning apps = (AppsRunning) item;

		StringBuilder sb = new StringBuilder();
        List<RunningApplicationInfo> runningapps = apps.getLatestApplications();
        sb.append("Current Running Apps :");
        
        for(int i = 0 ; i<runningapps.size(); i++)
        {
        	sb.append(runningapps.get(i).getApplicationName()+ "\n");
        	sb.append(runningapps.get(i).getClassName()+ "\n");
        	sb.append(runningapps.get(i).getPackageName()+ "\n");
        }
        sb.append("Current app: " + apps.getCurrentApplication().getApplicationName());

        return sb.toString();
	}
	
	@Override
	protected void addProviderOptions(List<ProviderOption> providerOptions) {
	    providerOptions.add(ProviderOption.createFromEnum(MAX_TASKS_QUANTITY.class, MAX_TASKS_QUANTITY.THIRTY));
	    providerOptions.add(ProviderOption.createFromEnum(MONITOR_INTERVAL.class, MONITOR_INTERVAL.TEN_SECONDS));
	}
	
	@Override
	public Bundle getProviderOptionsBundle() {
	    Bundle bundle = new Bundle();
	    bundle.putInt(MAX_TASKS_QUANTITY.class.getSimpleName(), getProviderOptionEnum(MAX_TASKS_QUANTITY.class).getQuantity());
	    bundle.putInt(MONITOR_INTERVAL.class.getSimpleName(), getProviderOptionEnum(MONITOR_INTERVAL.class).getInterval());
	    
	    return bundle;
	}
	
	@Override
    public boolean shouldNotStartSensing() {
        return false;
    }
	
public static enum MAX_TASKS_QUANTITY {
        
        FIVE(5),
        TEN(10),
        FIFTEEN(15),
        TWENTY(20),
        TWENTY_FIVE(25),
        THIRTY(30);
        
        private final int mQuantity;
        
        private MAX_TASKS_QUANTITY(int quantity) {
            mQuantity = quantity;
        }
        
        public int getQuantity() {
            return mQuantity;
        }
    }
}

