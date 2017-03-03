package com.intel.samples.contextsensingapiflow;

import java.util.ArrayList;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.intel.context.Sensing;
import com.intel.context.error.ContextError;
import com.intel.context.sensing.SensingEvent;
import com.intel.context.sensing.SensingStatusListener;
import com.intel.samples.contextsensingapiflow.listener.IApplicationListener;

public class ContextSensingApiFlowSampleApplication extends Application {
    private Sensing mSensing;
    private ArrayList<IApplicationListener> mListeners;
    
    
    private boolean mServiceStarted = false;
    
    public void onCreate() {
        super.onCreate();
        mListeners = new ArrayList<IApplicationListener>();
        mSensing = new Sensing(getApplicationContext(), new MySensingListener());
    }
    
    public Sensing getSensing() {
        return mSensing;
    }

    public ArrayList<IApplicationListener> getApplicationListeners()
    {
    	return mListeners;
    }
  
    public void start() {
        mServiceStarted = true;
    }
    
    public void stop() {
        mServiceStarted = false;
    }
    
    public boolean isStarted() {
        return mServiceStarted;
    }
    
    /*
     * Listener to receive updates from Context Sensing Daemon.
     */
    private class MySensingListener implements SensingStatusListener {
        
        private final String LOG_TAG = MySensingListener.class.getName();

        MySensingListener() {}

        public void onEvent(SensingEvent event) {
            Toast.makeText(getApplicationContext(),
                    "Event: " + event.getDescription(), Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Event: " + event.getDescription());
        }

        public void onFail(ContextError error) {
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Context Sensing error: " + error.getMessage());
        }
    }
}
