package com.intel.samples.contextsensingapiflow.listener;

import java.text.NumberFormat;
import java.util.List;
import java.util.NoSuchElementException;

import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.Calendar;
import com.intel.context.item.ContextType;
import com.intel.context.item.Item;
import com.intel.context.item.cloud.Traffic;
import com.intel.context.item.cloud.traffic.Incident;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleApplication;

public class TrafficListener extends IApplicationListener {

    private final String LOG_TAG = ContextSensingApiFlowSampleApplication.class.getName();
    
    public TrafficListener(UpdateNotifier updateNotifier, int index) {
        super("Traffic", ContextType.TRAFFIC, updateNotifier, index);
    }

    @Override
    public void onReceive(Item item) {
        if (item instanceof Traffic) {
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
        return true;
    }

    @Override
    protected void addProviderOptions(List<ProviderOption> providerOptions) {
    }

    @Override
    protected EventFilter constructEventFilter() {
        return null; // No filter for this provider.
    }

    @Override
    protected String describeItem(Item item) {
        Traffic traffic = (Traffic) item;
        
        StringBuilder sb = new StringBuilder();
        for (Incident incident : traffic.getIncidents()) {
            appendIncident(sb, incident).append("\n");
        }
        
        return sb.toString();
    }

    StringBuilder appendIncident(StringBuilder sb, Incident incident) {
        sb.append("Incident ID: ").append(incident.getId()).append("\n");
        sb.append("Type: ").append(incident.getType()).append("\n");
        
        try {
            sb.append("Severity: ").append(incident.getSeverity()).append("\n");
        } catch(NoSuchElementException ex) {
            sb.append("Unavaliable\n");
        }
        
        sb.append("Summary: ").append(incident.getSummary()).append("\n");
        
        String coordinateFormatString = "%.5f";
        try {
            sb.append("Latitude: ").append(String.format(coordinateFormatString, incident.getLatitude())).append("\n");
        } catch(NoSuchElementException ex) {
            sb.append("Unavaliable\n");
        }
        
        try {
            sb.append("Longitude: ").append(String.format(coordinateFormatString, incident.getLongitude())).append("\n");
        } catch(NoSuchElementException ex) {
            sb.append("Unavaliable\n");
        }
        
        sb.append("Start Time: ").append(incident.getStartTime()).append("\n");
        sb.append("End Time: ").append(incident.getEndTime()).append("\n");
        
        return sb;
    }
}
