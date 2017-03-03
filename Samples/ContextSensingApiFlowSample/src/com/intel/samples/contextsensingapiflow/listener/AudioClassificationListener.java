package com.intel.samples.contextsensingapiflow.listener;

import java.util.List;

import android.os.Bundle;
import android.util.Log;

import com.intel.context.error.ContextError;
import com.intel.context.item.AudioClassification;
import com.intel.context.item.ContextType;
import com.intel.context.item.Item;
import com.intel.context.item.audioclassification.Audio;
import com.intel.context.option.ContinuousFlag;
import com.intel.context.option.audio.AudioOptionBuilder;
import com.intel.context.option.audio.Mode;
import com.intel.samples.contextsensingapiflow.ContextSensingApiFlowSampleApplication;

public class AudioClassificationListener extends IApplicationListener {

    private final String LOG_TAG = ContextSensingApiFlowSampleApplication.class.getName();

    public AudioClassificationListener(UpdateNotifier updateNotifier, int index) {
        super("Audio", ContextType.AUDIO, updateNotifier, index);
    }
    
    @Override
    protected EventFilter constructEventFilter() {
        return null; // No event filter for this provider.
    }
    
    public void onReceive(Item state) {
        if (state instanceof AudioClassification) {
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
        AudioClassification audioRecognitionState = (AudioClassification) item;
        
        StringBuilder sb = new StringBuilder();
        List<Audio> audioList = audioRecognitionState.getAudio();
        for (Audio a: audioList) {
        	 sb.append(a.getName().toString().split("_")[0]+" " + a.getProbability() + "% chance\n");
        }   
        
        Log.d(LOG_TAG, "New Audio State: " + sb.toString());
        return sb.toString();
	}
	
    @Override
    protected void addProviderOptions(List<ProviderOption> providerOptions) {
        providerOptions.add(ProviderOption.createFromEnum(Mode.class, Mode.FAST));
        providerOptions.add(ProviderOption.createFromEnum(ContinuousFlag.class, ContinuousFlag.PAUSE_ON_SLEEP));
    }
    
	@Override
	public Bundle getProviderOptionsBundle() {
		AudioOptionBuilder audioOptionBuilder = new AudioOptionBuilder();
		audioOptionBuilder.setMode(getProviderOptionEnum(Mode.class));
		audioOptionBuilder.setSensorHubContinuousFlag(getProviderOptionEnum(ContinuousFlag.class));
		
		return audioOptionBuilder.toBundle();
	}
	
	@Override
    public boolean shouldNotStartSensing() {
        return false;
    }
}
