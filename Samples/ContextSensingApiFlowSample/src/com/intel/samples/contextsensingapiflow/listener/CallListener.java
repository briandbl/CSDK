package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;

import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.Call;
import com.intel.context.item.ContextType;
import com.intel.context.item.Item;

public class CallListener extends IApplicationListener {

    private final String LOG_TAG = CallListener.class.getName();

    public CallListener(UpdateNotifier updateNotifier, int index) {
        super("Call", ContextType.CALL, updateNotifier, index);
    }
    
    @Override
    protected EventFilter constructEventFilter() {
        return null; // No event filter for this provider.
    }
    
    public void onReceive(Item state) {
        if (state instanceof Call) {
            setLastKnownItem(state);
            notifyUpdate();
        }
    }

    public void onError(ContextError error) {
        mUpdateNotifier.notifyError("CallListener error: " + error.getMessage());
        Log.e(LOG_TAG, "Error: " + error.getMessage());
    }

	@Override
	public String describeItem(Item item) {
		Call call = (Call) item;
        
        StringBuilder sb = new StringBuilder();
        sb.append("Phone number: " + call.getCaller());
        sb.append("\nMissed qty.: " + call.getMissedQuantity());
        sb.append("\nRing qty.: " + call.getRingQuantity());
        sb.append("\nNotif. type: " + call.getNotificationType());
        Log.d(LOG_TAG, "New Call State: " + sb.toString());
        return sb.toString();
	}

	@Override
	protected void addProviderOptions(List<ProviderOption> providerOptions) {
	}
	
	@Override
    public boolean shouldNotStartSensing() {
        return false;
    }

}

