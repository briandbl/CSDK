package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;

import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.ContextType;
import com.intel.context.item.Item;
import com.intel.context.item.Music;

public class MusicListener extends IApplicationListener {

    private final String LOG_TAG = MusicListener.class.getName();
    
    public MusicListener(UpdateNotifier updateNotifier, int index) {
    	super("Music", ContextType.MUSIC, updateNotifier, index);
    }
    
    @Override
    protected EventFilter constructEventFilter() {
        return null; // No event filter for this provider.
    }
    
	@Override
	public void onReceive(Item state) {
		if(state instanceof Music) {
			setLastKnownItem(state);
			notifyUpdate();
		}
	}

	@Override
	public void onError(ContextError error) {
		mUpdateNotifier.notifyError("MusicListener error: " + error.getMessage());
        Log.e(LOG_TAG, "Error: " + error.getMessage());
	}

	@Override
	protected String describeItem(Item item) {
		Music music = (Music) item;
        
        StringBuilder sb = new StringBuilder();
        sb.append("Title: " + music.getTitle());
        sb.append("\nAuthor: " + music.getAuthor());
        sb.append("\nAlbum: " + music.getAlbum());

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

