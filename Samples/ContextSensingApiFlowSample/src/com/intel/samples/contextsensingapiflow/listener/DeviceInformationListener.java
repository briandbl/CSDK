package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;
import java.util.NoSuchElementException;

import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.ContextType;
import com.intel.context.item.DeviceInformation;
import com.intel.context.item.Item;
import com.intel.context.item.deviceinformation.Sensor;

public class DeviceInformationListener extends IApplicationListener {

	private final String LOG_TAG = DeviceInformationListener.class.getName();
		
	public DeviceInformationListener(UpdateNotifier updateNotifier, int index) {
		super("Device Information", ContextType.DEVICE_INFORMATION, updateNotifier, index);
	}

	@Override
	public void onReceive(Item item) {
        if(item instanceof DeviceInformation) {
            setLastKnownItem(item);
            notifyUpdate();
        }
	}

	@Override
	public void onError(ContextError error) {
        mUpdateNotifier.notifyError("DeviceInformationListener error: " + error.getMessage());
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
		DeviceInformation deviceInfo = (DeviceInformation) item;
		StringBuilder sb = new StringBuilder();
		
		appendDeviceInfoString(sb,"Brand", deviceInfo.getBrand());
		appendDeviceInfoString(sb,"Board", deviceInfo.getBoard());
		appendDeviceInfoString(sb,"CPU", deviceInfo.getCpu());
		
        try {
        	appendDeviceInfoString(sb,"Hardware Name", ""+deviceInfo.getHardwareName());
        } catch(NoSuchElementException ex) {
        	appendDeviceInfoString(sb,"Hardware Name", "Unavailable");
        }
        
		appendDeviceInfoString(sb,"Manufacturer", deviceInfo.getManufacturer());
		appendDeviceInfoString(sb,"Model", deviceInfo.getModel());
		appendDeviceInfoString(sb,"OS Type", deviceInfo.getOsType());
		appendDeviceInfoString(sb,"OS Version", deviceInfo.getOsVersion());
		
        try {
        	appendDeviceInfoString(sb,"Phone Number", ""+deviceInfo.getPhoneNumber());
        } catch(NoSuchElementException ex) {
        	appendDeviceInfoString(sb,"Phone Number", "Unavailable");
        }
		appendDeviceInfoString(sb,"Product Name", deviceInfo.getProductName());
		appendDeviceInfoString(sb,"Density", ""+deviceInfo.getDensity());
		appendDeviceInfoString(sb,"Height", ""+deviceInfo.getHeight());
		appendDeviceInfoString(sb,"Width", ""+deviceInfo.getWidth());
		appendDeviceInfoString(sb,"Natural Orientation", deviceInfo.getNaturalOrientation().toString());		
		appendDeviceInfoString(sb,"RAM", ""+deviceInfo.getTotalRamSize());
		appendDeviceInfoString(sb,"Has external memory?", ""+deviceInfo.hasExternalMemory());

        try {
        	appendDeviceInfoString(sb,"Storage Size", ""+deviceInfo.getTotalStorageSize());
        } catch(NoSuchElementException ex) {
        	appendDeviceInfoString(sb,"Storage Size", "Unavailable");
        }
		
		List<Sensor> sensors = null;
        try {
        	sensors = deviceInfo.getSensorsInformation();
        	
        } catch(NoSuchElementException ex) {
        	appendDeviceInfoString(sb,"Sensor", "Unable to retrieve sensor data");
        }
        if(sensors != null)
        {
	        for(Sensor sensor: sensors)
	        {
	        	appendDeviceInfoString(sb,"Sensor", sensor.getName());
	        }
        }
        
		return sb.toString();
	}
	
	private StringBuilder appendDeviceInfoString(StringBuilder sb, String propertyName, String propertyValue){
		sb.append(propertyName);
		sb.append(" : ");
		sb.append(propertyValue);
		sb.append("\n");
		
		return sb;
	}

}
