package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;

import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.ContextType;
import com.intel.context.item.Item;
import com.intel.context.item.cloud.TimeZone;

public class TimeZoneListener extends IApplicationListener{
	
	private final String LOG_TAG = TimeZoneListener.class.getName();

	public TimeZoneListener(UpdateNotifier updateNotifier, int index) {
		super("Time Zone", ContextType.TIMEZONE, updateNotifier, index);
	}

	@Override
	public void onReceive(Item item) {
		if(item instanceof TimeZone) {
			setLastKnownItem(item);
			notifyUpdate();
		}
	}

	@Override
	public void onError(ContextError error) {
		mUpdateNotifier.notifyError("TimeZoneListener error: " + error.getMessage());
        Log.e(LOG_TAG, "Error: " + error.getMessage());
	}

	@Override
	public boolean shouldNotStartSensing() {
		return true;
	}

	@Override
	protected void addProviderOptions(List<ProviderOption> providerOptions) {
	}

	@Override
	protected EventFilter constructEventFilter() {
		return null; // No event filter for this provider.
	}

	@Override
	protected String describeItem(Item item) {
		TimeZone tz = (TimeZone) item;
		StringBuilder sb = new StringBuilder();
		
		sb.append("Offset: ").append(tz.getOffset()).append("\n");
		sb.append("Zone DST: ").append(tz.getZoneDst()).append("\n");
		sb.append("Zone Offset: ").append(tz.getZoneOffset()).append("\n");
		sb.append("Zone Total Offset: ").append(tz.getZoneTotalOffset()).append("\n");

		return sb.toString();
	}

}
