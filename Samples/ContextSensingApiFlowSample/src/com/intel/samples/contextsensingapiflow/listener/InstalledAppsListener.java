package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;

import android.os.Bundle;
import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.AppsInstalled;
import com.intel.context.item.ContextType;
import com.intel.context.item.Item;
import com.intel.context.item.installedapplication.InstalledApplicationInfo;

public class InstalledAppsListener extends IApplicationListener {

    private final String LOG_TAG = InstalledAppsListener.class.getName();

    public InstalledAppsListener(UpdateNotifier updateNotifier, int index) {
    	super("Installed Apps", ContextType.INSTALLED_APPS,
    			updateNotifier, index);
    }
    
    @Override
    protected EventFilter constructEventFilter() {
        return null; // No event filter for this provider.
    }
    
	@Override
	public void onReceive(Item state) {
		if(state instanceof AppsInstalled) {
			setLastKnownItem(state);
			notifyUpdate();
		}
	}

	@Override
	public void onError(ContextError error) {
		mUpdateNotifier.notifyError("AppsListener error: " + error.getMessage());
        Log.e(LOG_TAG, "Error: " + error.getMessage());
	}

	@Override
	protected String describeItem(Item item) {
		 AppsInstalled apps = (AppsInstalled) item;

         StringBuilder sb = new StringBuilder();
         sb.append("Name/Permissions: ");
         sb.append("\n");
         List<InstalledApplicationInfo> appsStatus = apps.getInstalledApplications(); 
         for(int i =0;i<appsStatus.size();i++){  
         	
         		sb.append(appsStatus.get(i).getAppName() + "\n ");
         		sb.append(appsStatus.get(i).getStatus());
         		sb.append("\n");
         }
         Log.d(LOG_TAG, "New Installed Apps State: " + sb.toString());
         return sb.toString();
	}
	
	@Override
	protected void addProviderOptions(List<ProviderOption> providerOptions) {
	    providerOptions.add(ProviderOption.createFromEnum(HARVESTING_MODE.class, HARVESTING_MODE.DOWNLOADED_APPS));
	    providerOptions.add(ProviderOption.createFromEnum(MAX_APP_QUANTITY.class, MAX_APP_QUANTITY.THIRTY));
	}
	
	@Override
	public Bundle getProviderOptionsBundle() {
	    Bundle bundle = new Bundle();
	    bundle.putString(HARVESTING_MODE.class.getSimpleName(), getProviderOptionEnum(HARVESTING_MODE.class).name());
	    bundle.putInt(MAX_APP_QUANTITY.class.getSimpleName(), getProviderOptionEnum(MAX_APP_QUANTITY.class).getQuantity());
	    
	    return bundle;
	}
	
	@Override
    public boolean shouldNotStartSensing() {
        return false;
    }
	
	public static enum HARVESTING_MODE {
	    FULL,
	    DOWNLOADED_APPS
	}
	
	public static enum MAX_APP_QUANTITY {
	    
	    FIVE(5),
	    TEN(10),
	    FIFTEEN(15),
	    TWENTY(20),
	    TWENTY_FIVE(25),
	    THIRTY(30);
	    
	    private final int mQuantity;
	    
	    private MAX_APP_QUANTITY(int quantity) {
	        mQuantity = quantity;
	    }
	    
	    public int getQuantity() {
	        return mQuantity;
	    }
	}
}

