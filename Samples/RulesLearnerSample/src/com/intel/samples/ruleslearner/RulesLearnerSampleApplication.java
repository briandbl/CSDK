package com.intel.samples.ruleslearner;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.intel.context.Sensing;
import com.intel.context.error.ContextError;
import com.intel.context.item.Item;
import com.intel.context.item.LocationCurrent;
import com.intel.context.item.Network;
import com.intel.context.sensing.ContextTypeListener;
import com.intel.context.sensing.SensingEvent;
import com.intel.context.sensing.SensingStatusListener;


public class RulesLearnerSampleApplication extends Application {
    //TODO: private Auth mAuth;
    private Sensing mSensing;
    private static RulesLearnerSampleApplication mInstance;
    private NetworkListener mNetworkListener;
    private LocationListener mLocationListener;

    public void onCreate() {
        super.onCreate();
        mInstance = this;
        /*TODO:
        mAuth = new Auth(getApplicationContext(),
                Settings.API_KEY, Settings.SECRET,
                Environment.PROD);*/
        mSensing = new Sensing(getApplicationContext(), new MySensingListener());    
        mNetworkListener = new NetworkListener();
        mLocationListener = new LocationListener();
    }
    
    public static RulesLearnerSampleApplication getInstance() {
        return mInstance;
    }

    public Sensing getSensing() {
        return mSensing;
    }
    /*TODO:
    public Auth getAuth(){
        return mAuth;
    }*/



    public ContextTypeListener getNetworkListener(){
    	return mNetworkListener;
    }
    
    public ContextTypeListener getLocationListener() {
    	return mLocationListener;
    }
    private class MySensingListener implements SensingStatusListener {        
        
        private final String LOG_TAG = MySensingListener.class.getName();

        MySensingListener() {}
        
        @Override
        public void onEvent(SensingEvent event) {
            Toast.makeText(getApplicationContext(),
                    "Event: " + event.getDescription(), Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Event: " + event.getDescription());
        }

        @Override
        public void onFail(ContextError error) {
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Context Sensing error: " + error.getMessage());            
        }
    }
 
    private class NetworkListener implements ContextTypeListener  {

        private final String LOG_TAG = NetworkListener.class.getName();
     
      
        
        public void onReceive(Item state) {
            if (state instanceof Network) {
                Network network = (Network) state;
                  
                StringBuilder sb = new StringBuilder();
                sb.append("Type: " + network.getNetworkType());
                sb.append("\nSSID: " + network.getSsid());
                sb.append("\nSignal: " + network.getSignalStrength());
                sb.append("\nTraffic sent: " + network.getTrafficSent());
                sb.append("\n" + network.getDateTime());
                Log.d(LOG_TAG, "New Network State: " + sb.toString());
                final String networkText = sb.toString();
               
                 Toast.makeText(getApplicationContext(),
                            "New Current App Package Name: " + networkText, Toast.LENGTH_LONG).show();

            }
        }

        public void onError(ContextError error) {
            Toast.makeText(getApplicationContext(),
                    "Listener Status: " + error.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Error: " + error.getMessage());
        }
    }
    
    private class LocationListener implements ContextTypeListener  {

        private final String LOG_TAG = NetworkListener.class.getName();
          
        public void onReceive(Item state) {
        	if (state instanceof LocationCurrent) {
                LocationCurrent mLocation = (LocationCurrent) state;
              
                
                StringBuilder sb = new StringBuilder();
                sb.append(mLocation.getActivity());
                sb.append("\n Latitude: " + mLocation.getLocation().getLatitude());
                sb.append("\n Longitude: " + mLocation.getLocation().getLongitude());
                sb.append("\n" + mLocation.getDateTime());
                
                Log.e(LOG_TAG, "New Location State: " + sb.toString());
                final String locationText = sb.toString();
                Toast.makeText(getApplicationContext(),
                        "New Current App Package Name: " + locationText, Toast.LENGTH_LONG).show();

            }
        }

        public void onError(ContextError error) {
            Toast.makeText(getApplicationContext(),
                    "Listener Status: " + error.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Error: " + error.getMessage());
        }
    }
       
    
}
