package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;

import android.app.AlarmManager;
import android.os.Bundle;
import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.Calendar;
import com.intel.context.item.ContextType;
import com.intel.context.item.Item;
import com.intel.context.item.calendar.CalendarEvent;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleApplication;

public class CalendarListener extends IApplicationListener {

    private final String LOG_TAG = ContextSensingApiFlowSampleApplication.class.getName();
    
    public CalendarListener(UpdateNotifier updateNotifier, int index) {
        super("Calendar", ContextType.CALENDAR, updateNotifier, index);
    }

    @Override
    public void onReceive(Item item) {
        if (item instanceof Calendar) {
            setLastKnownItem(item);
            notifyUpdate();
        }
    }

    @Override
    public void onError(ContextError error) {
        mUpdateNotifier.notifyError("CalendarListener error: " + error.getMessage());
        Log.e(LOG_TAG, "Error: " + error.getMessage());
    }

    @Override
    public boolean shouldNotStartSensing() {
        return false;
    }

    @Override
    protected void addProviderOptions(List<ProviderOption> providerOptions) {
        providerOptions.add(ProviderOption.createFromEnum(INTERVAL.class, INTERVAL.HALF_HOUR));
    }
    
    @Override
    public Bundle getProviderOptionsBundle() {
        Bundle bundle = new Bundle();
        bundle.putLong(INTERVAL.class.getSimpleName(), getProviderOptionEnum(INTERVAL.class).getInterva());
        
        return bundle;
    }

    @Override
    protected EventFilter constructEventFilter() {
        return null; // No event filter for provider.
    }

    @Override
    protected String describeItem(Item item) {
        Calendar calendar = (Calendar) item;
        
        StringBuilder sb = new StringBuilder();
        for (CalendarEvent event : calendar.getEvents()) {
            appendCalendarEvent(sb, event).append("\n");
        }
        
        return sb.toString();
    }

    private StringBuilder appendCalendarEvent(StringBuilder sb, CalendarEvent event) {
        
        sb.append("Name: ").append(event.getTitle()).append("\n");
        sb.append("Description: ").append(event.getDescription()).append("\n");
        sb.append("Location: ").append(event.getLocation()).append("\n");
        sb.append("Starts: ").append(event.getStartDate()).append("\n");
        sb.append("Ends: ").append(event.getEndDate()).append("\n");
        sb.append("Duration: ").append(event.getDuration()).append("\n");
        sb.append("TimeZone: ").append(event.getTimezone()).append("\n");
        
        return sb;
    }
    
    public static enum INTERVAL {
        THIRTY_SECONDS(30 * 1000),
        ONE_MINUTE(60 * 1000),
        FIFTEEN_MINUTES(AlarmManager.INTERVAL_FIFTEEN_MINUTES),
        HALF_HOUR(AlarmManager.INTERVAL_HALF_HOUR),
        HOUR(AlarmManager.INTERVAL_HOUR),
        HALF_DAY(AlarmManager.INTERVAL_HALF_DAY),
        DAY(AlarmManager.INTERVAL_DAY);
        
        private final long mInterval;
        
        private INTERVAL(long interval) {
            mInterval = interval;
        }
        
        public long getInterva() {
            return mInterval;
        }
    }
}
