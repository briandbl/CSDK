package com.intel.samples.predictionsample;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.intel.context.behavior.IPredictionListener;
import com.intel.context.behavior.PredictionModel;
import com.intel.context.error.ContextError;
import com.intel.context.item.ContextType;
import com.intel.context.item.activityrecognition.ActivityName;
import com.intel.context.behavior.PredictionModel.PredictionModelEntry;

import java.util.HashMap;

public class PredictionListener implements IPredictionListener {
    private Context appContext;
    private Activity currentActivity;
    private String LOG_TAG = this.getClass().getName();
    private String predictionActivityName;


    public PredictionListener(Context context, Activity activity, ActivityName name){
        appContext = context;
        currentActivity = activity;
        predictionActivityName = name.toString();
    }

    @Override
    public void onPredictionModelReceived(PredictionModel predictionModel) {
        String predictedActivity = ActivityName.UNKNOWN.toString();

        Toast.makeText(appContext, "Received Prediction model",
                Toast.LENGTH_SHORT).show();
        Log.i(LOG_TAG, "Received Prediction model with length=" + predictionModel.
                getPredictionModelArray().size());
        HashMap<String, String> predictionConditions = getPredictionConditions(currentActivity);
        for (PredictionModelEntry entry : predictionModel.getPredictionModelArray()){
            PredictionModel.Conditions conditions = entry.getModelEntryConditions();
            if(conditions.getHourOfDay().equals(predictionConditions.get("hour"))
                    && conditions.getPartOfWeek().equals(predictionConditions.get("day"))
                    && conditions.getPlace().equals(predictionConditions.get("place"))
                    && conditions.getWeatherCondition().equals(predictionConditions.get("weather"))){
                PredictionModel.Prediction prediction = entry.getModelEntryPrediction();
                if(prediction.getPredictionType().equals
                        (ContextType.ACTIVITY_RECOGNITION.getIdentifier())
                        && prediction.getPredictionValue().getActivity().equals
                        (predictionActivityName)){
                    predictedActivity = prediction.getPredictionValue().getPerform();
                }
            }


        }

        TextView textView = (TextView) currentActivity.findViewById(R.id.prediction_is);
        if (predictedActivity.equals(ActivityName.UNKNOWN.toString())) {
            Log.i(LOG_TAG, "Unknown activity");
            textView.setText(ActivityName.UNKNOWN.toString());
        } else if (Boolean.parseBoolean(predictedActivity)) {
            Log.i(LOG_TAG, "User will be doing the activity");
            textView.setText(predictionActivityName);
        } else {
            Log.i(LOG_TAG, "User will NOT be doing the activity");
            textView.setText("NOT "+predictionActivityName);
        }

    }

    private HashMap<String, String> getPredictionConditions(Activity currentActivity) {
        HashMap<String, String> predictionConditions = new HashMap<>();

        Spinner timeSpinner = (Spinner) currentActivity.findViewById(R.id.timeSpinner);
        predictionConditions.put("hour", timeSpinner.getSelectedItem().toString());

        Spinner daySpinner = (Spinner) currentActivity.findViewById(R.id.daySpinner);
        String day = daySpinner.getSelectedItem().toString();
        if(day.equals("Saturday") || day.equals("Sunday")){
            predictionConditions.put("day", "weekend" );
        }
        else{
            predictionConditions.put("day", "weekday" );
        }

        Spinner weatherSpinner = (Spinner) currentActivity.findViewById(R.id.weatherSpinner);
        predictionConditions.put("weather",  weatherSpinner.getSelectedItem().toString());

        Spinner placeSpinner = (Spinner) currentActivity.findViewById(R.id.placeSpinner);
        predictionConditions.put("place", placeSpinner.getSelectedItem().toString());

        return predictionConditions;
    }

    @Override
    public void onPredictionModelError(ContextError error) {
        Toast.makeText(appContext, "Prediction model error",
                Toast.LENGTH_SHORT).show();
        Log.i(LOG_TAG, "Prediction model error");


    }



}
