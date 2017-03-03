package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;

import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.ContextType;
import com.intel.context.item.Item;
import com.intel.context.item.Message;

public class MessageListener extends IApplicationListener {

    private final String LOG_TAG = MessageListener.class.getName();

    public MessageListener(UpdateNotifier updateNotifier, int index) {
    	super("Message", ContextType.MESSAGE, updateNotifier, index);
    }
    
    @Override
    protected EventFilter constructEventFilter() {
        return null; // No event filter for this provider.
    }
    
	@Override
	public void onReceive(Item state) {
		if(state instanceof Message) {
			setLastKnownItem(state);
			notifyUpdate();
		}
	}

	@Override
	public void onError(ContextError error) {
		mUpdateNotifier.notifyError("MessageListener error: " + error.getMessage());
        Log.e(LOG_TAG, "Error: " + error.getMessage());
	}

	@Override
	protected String describeItem(Item item) {
		Message message = (Message) item;
        
        StringBuilder sb = new StringBuilder();
        sb.append("Phone number: " + message.getPhoneNumber());
        sb.append("\nUnread messages: " + message.getTotalMessagesUnread());
        sb.append("\nSent messages: " + message.getTotalMessagesSent());
        
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

