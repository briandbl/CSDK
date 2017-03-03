package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;

import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.ContextType;
import com.intel.context.item.Item;
import com.intel.context.item.cloud.NearbyRestaurants;
import com.intel.context.item.cloud.nearbyrestaurants.Restaurant;

public class NearbyResturantListener extends IApplicationListener {

    private final String LOG_TAG = NearbyResturantListener.class.getName();
    
    public NearbyResturantListener(UpdateNotifier updateNotifier, int index) {
    	super("Nearby Restaurants", ContextType.NEARBY_RESTAURANTS, updateNotifier, index);
    }
    
    @Override
    protected EventFilter constructEventFilter() {
        return null; // No event filter for this provider.
    }
    
	@Override
	public void onReceive(Item state) {
		if(state instanceof NearbyRestaurants) {
			setLastKnownItem(state);
			notifyUpdate();
		}
	}

	@Override
	public void onError(ContextError error) {
		mUpdateNotifier.notifyError("NearbyResturantListener error: " + error.getMessage());
        Log.e(LOG_TAG, "Error: " + error.getMessage());
	}

	@Override
	protected String describeItem(Item item) {
		NearbyRestaurants nearbyResturants = (NearbyRestaurants) item;
        
        StringBuilder sb = new StringBuilder();
         List<Restaurant> resturantList = nearbyResturants.getRestaurants();
         
         for(int i =0;i<resturantList.size();i++) {  
         	
     		sb.append(resturantList.get(i).getName() + "\n ");
     		sb.append(resturantList.get(i).getPhone());
     		sb.append("\n");
         }

         return sb.toString();
	}
	
	@Override
	protected void addProviderOptions(List<ProviderOption> providerOptions) {
	}
	
	@Override
    public boolean shouldNotStartSensing() {
        return true;
    }
}

