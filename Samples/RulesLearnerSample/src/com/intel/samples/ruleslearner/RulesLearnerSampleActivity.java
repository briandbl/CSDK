package com.intel.samples.ruleslearner;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;


import com.intel.context.RuleLearner;
import com.intel.context.Sensing;
import com.intel.context.error.ContextError;
import com.intel.context.exception.ContextProviderException;
import com.intel.context.exception.RuleLearnerException;
import com.intel.context.item.ContextType;
import com.intel.context.rules.learner.Action;
import com.intel.context.rules.learner.SuggestedRule;
import com.intel.context.rules.learner.classifiers.FullLikelyHood;
import com.intel.context.sensing.ContextTypeListener;
import com.intel.context.sensing.InitCallback;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class RulesLearnerSampleActivity extends Activity {
    private final static String LOG_TAG = RulesLearnerSampleActivity.class.getName();
    private Sensing mySensing;
    private ContextTypeListener myNetworkListener;
    private ContextTypeListener myLocationListener;
    private RuleLearner rLearner;
    private Button getRulesButton;
    private Button startDaemonButton;
    private Button startRulesLearnerButton;
    private Button stopRulesLearnerButton;
    private Button stopDaemonButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mySensing = RulesLearnerSampleApplication.getInstance().getSensing();
        myNetworkListener = RulesLearnerSampleApplication.getInstance().getNetworkListener();
        myLocationListener = RulesLearnerSampleApplication.getInstance().getLocationListener();
        configureUI();
    }

    private void startRulesLearner() {
    	//enable provider for network
    	//enable provider for location
    	
    	 try {
             mySensing.addContextTypeListener(ContextType.NETWORK, myNetworkListener);
             mySensing.addContextTypeListener(ContextType.LOCATION, myLocationListener);
             
             Toast.makeText(getApplicationContext(), "Add Listeners Success", Toast.LENGTH_SHORT).show();
         } catch (ContextProviderException e) {
             Log.e(LOG_TAG, "Add Listener error: " + e.getMessage());
         }
    	 	
    	 rLearner = new RuleLearner("My Learned Rule",
    			"When the WiFi status changes, is because [?] happens.",
    			new FullLikelyHood(
    					(float) 0.33,
    					10,
    					Action.WifiChange,
    					Arrays.asList("Place.type", "Date.partOfWeek")));
		try {
			rLearner.start();
			Toast.makeText(getApplicationContext(), "Rules Learner Started", Toast.LENGTH_SHORT).show();
		} catch (RuleLearnerException e) {
			Log.e(LOG_TAG, "RuleLearner Exception" + e.getMessage());
			e.printStackTrace();
		}
		
			
	}
    
    
    private void getRules(){
    	
    	List<SuggestedRule> suggestedRules = rLearner.getSuggestedRules();
		if (suggestedRules.size() == 0) {
			Toast.makeText(getApplicationContext(), "No suggested rules yet...", Toast.LENGTH_SHORT).show();
		    Log.d("APP", "No suggested rules yet...");
		} else {
		    for (SuggestedRule sRule : suggestedRules) {
		        StringBuilder sb = new StringBuilder();
		        sb.append("Action Value: " + sRule.getActionValue());
		        for (Entry<String, String> e : sRule.getConditionValues().entrySet()) {
		            sb.append("\n> When: " + e.getKey() + " --> " + e.getValue());
		        }
		        String SuggestedRules = sb.toString();
		        Log.d("APP", "Suggested Rule:\n" + SuggestedRules);
		        Toast.makeText(getApplicationContext(), SuggestedRules, Toast.LENGTH_SHORT).show();
		    }
		}
    	
    }
    
    private void stopRulesLearner() {
    	try {
			rLearner.stop();
			Toast.makeText(getApplicationContext(),
                    "Rules Learner Stopped", Toast.LENGTH_LONG).show();
		} catch (RuleLearnerException e) {
			// TODO Auto-generated catch block
        	Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
    }

	public void onDestroy() {
        super.onDestroy();
    }
    
   
    private void configureUI(){
    	getRulesButton = (Button) findViewById(R.id.getRulesButton);
    	getRulesButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                getRules();
            }});
        
        startDaemonButton = (Button) findViewById(R.id.startSensingDaemonButton);
        startDaemonButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                startDaemon();
            }
        });

        
        startRulesLearnerButton = (Button) findViewById(R.id.startRulesLearnerButton);
        startRulesLearnerButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
            	startRulesLearner();
            }});

        stopRulesLearnerButton = (Button) findViewById(R.id.stopRulesLearnerButton);
        stopRulesLearnerButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
            	stopRulesLearner();
            }});
        
        
        stopDaemonButton = (Button) findViewById(R.id.stopSensingDaemonButton);
        stopDaemonButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                stopDaemon();
            }
        });
    }
        
    private void startDaemon() {
        mySensing.start(new InitCallback() {
            
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(),
                        "Context Sensing Daemon Started" , Toast.LENGTH_SHORT).show();               
            }
            
            @Override
            public void onError(ContextError error) {
                Toast.makeText(getApplicationContext(),
                        "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    
    
    private void stopDaemon() {
        try {
            mySensing.stop();
        } catch (RuntimeException e) {
            Toast.makeText(getApplicationContext(),
                    "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Error: " + e.getMessage());
        }
    }

    

}
