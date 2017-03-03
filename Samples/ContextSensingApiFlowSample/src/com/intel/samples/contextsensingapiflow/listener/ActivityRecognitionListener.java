package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;

import android.os.Bundle;
import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.ActivityRecognition;
import com.intel.context.item.ContextType;
import com.intel.context.item.Item;
import com.intel.context.item.activityrecognition.ActivityName;
import com.intel.context.item.activityrecognition.PhysicalActivity;
import com.intel.context.option.ContinuousFlag;
import com.intel.context.option.ProcessingUnit;
import com.intel.context.option.activity.ActivityOptionBuilder;
import com.intel.context.option.activity.Mode;
import com.intel.context.option.activity.ReportType;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleApplication;

public class ActivityRecognitionListener extends IApplicationListener {

    private final String LOG_TAG = ContextSensingApiFlowSampleApplication.class.getName();
    
    public ActivityRecognitionListener(UpdateNotifier updateNotifier, int index) {
    	super("Activity", ContextType.ACTIVITY_RECOGNITION, updateNotifier, index);
    }
    
    @Override
    protected EventFilter constructEventFilter() {
        return EventFilter.createFromEnum(ActivityName.class, ActivityName.values());
    }
      
    public void onReceive(Item state) {
        if (state instanceof ActivityRecognition) {
            setLastKnownItem(state);
            notifyUpdate();
        }
    }

    public void onError(ContextError error) {
        mUpdateNotifier.notifyError("Listener Status: " + error.getMessage());
        Log.e(LOG_TAG, "Error: " + error.getMessage());
    }

	@Override
	public String describeItem(Item item) {
		// Cast the incoming context state to an ActivityRecognition Item.
        ActivityRecognition activityRecognitionState = (ActivityRecognition) item;

        // Obtain the list of recognized physical activities.
        List<PhysicalActivity> physicalActivities = activityRecognitionState.getActivities();
        
        StringBuilder sb = new StringBuilder();
        for (PhysicalActivity activity: physicalActivities) {
            // Obtain the probability of each recognized activity.
            int activityProbability = activity.getProbability();
            sb.append(activity.getActivity().toString() + " " + activityProbability + "% chance\n");
            
        }
        
        Log.d(LOG_TAG, "New Activity Recognition State: " + sb.toString());
        return sb.toString();
	}
	
    @Override
    protected void addProviderOptions(List<ProviderOption> providerOptions) {
        providerOptions.add(ProviderOption.createFromEnum(Mode.class, Mode.NORMAL));
        providerOptions.add(ProviderOption.createFromEnum(ReportType.class, ReportType.FREQUENCY));
        providerOptions.add(ProviderOption.createFromEnum(ContinuousFlag.class, ContinuousFlag.PAUSE_ON_SLEEP));
    }
    
    @Override
    public Bundle getProviderOptionsBundle() {

        ActivityOptionBuilder activitySettings = new ActivityOptionBuilder();
        activitySettings.setMode(getProviderOptionEnum(Mode.class));
        activitySettings.setReportType(getProviderOptionEnum(ReportType.class));
        activitySettings.setSensorHubContinuousFlag(getProviderOptionEnum(ContinuousFlag.class));
        activitySettings.setFilter(getEventFilter().getAsEnumArray(ActivityName.class));
        activitySettings.setProcessingUnit(ProcessingUnit.HOST);

        return activitySettings.toBundle();
    }
    
    @Override
    public boolean shouldNotStartSensing() {
        return false;
    }
}

