package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;

import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.ContextType;
import com.intel.context.item.GlyphItem;
import com.intel.context.item.Item;
import com.intel.context.item.glyph.GlyphType;
import com.intel.context.option.ContinuousFlag;
import com.intel.context.option.glyph.GlyphOptionBuilder;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleApplication;

public class GlyphListener extends IApplicationListener {
	private final String LOG_TAG = ContextSensingApiFlowSampleApplication.class.getName();

    public GlyphListener(UpdateNotifier updateNotifier, int index) {
    	super("Glyph", ContextType.GLYPH, updateNotifier, index);
    }
    
    @Override
    protected EventFilter constructEventFilter() {
        return EventFilter.createFromEnum(GlyphType.class, GlyphType.values());
    }
    
	@Override
	public void onReceive(Item state) {
		if(state instanceof GlyphItem) {
			setLastKnownItem(state);
			notifyUpdate();
		}
	}

	@Override
	public void onError(ContextError error) {
		mUpdateNotifier.notifyError("GlyphListener error: " + error.getMessage());
        Log.e(LOG_TAG, "Error: " + error.getMessage());
	}

	@Override
	protected String describeItem(Item item) {
		GlyphItem glyphItem = (GlyphItem) item;

		StringBuilder sb = new StringBuilder();
		sb.append("Glyph: " + glyphItem.getType());
		Log.d(LOG_TAG, "New Glyph State: " + sb.toString());
		return sb.toString();
	}
	
	@Override
	protected void addProviderOptions(List<ProviderOption> providerOptions) {
		providerOptions.add(ProviderOption.createFromEnum(ContinuousFlag.class, ContinuousFlag.PAUSE_ON_SLEEP));
	}
	
	@Override
	public Bundle getProviderOptionsBundle() {
		GlyphOptionBuilder glyphSetting = new GlyphOptionBuilder();
		glyphSetting.setSensorHubContinuousFlag(getProviderOptionEnum(ContinuousFlag.class));
		glyphSetting.setFilter(getEventFilter().getAsEnumArray(GlyphType.class));
		
		return glyphSetting.toBundle();
	}
	
	@Override
    public boolean shouldNotStartSensing() {
        return false;
    }	
}