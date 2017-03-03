package com.intel.samples.predictionsample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.intel.context.Auth;
import com.intel.context.Behavior;
import com.intel.context.auth.AuthCallback;
import com.intel.context.behavior.ActivityPredictionOptions;
import com.intel.context.error.ContextError;
import com.intel.context.item.activityrecognition.ActivityName;

import java.util.HashMap;

public class PredictionActivity extends AppCompatActivity {
    private Auth externalAuth;
    private String LOG_TAG = this.getClass().getName();
    private Behavior behaviorAPI;
    private Context appContext;
    private Activity thisActivity = this;
    private ActivityName predictionActivityName = ActivityName.RUNNING;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appContext = getApplicationContext();
        behaviorAPI = new Behavior(appContext);
        externalAuth = Auth.getInstance(this);

        Button auth = (Button) findViewById(R.id.authButton);
        auth.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                authenticate();
            }});

        Button predictBtn = (Button) findViewById(R.id.predictButton);
        predictBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ActivityPredictionOptions options = new ActivityPredictionOptions(
                        predictionActivityName);
                PredictionListener listener = new PredictionListener(appContext, thisActivity,
                        predictionActivityName);
                behaviorAPI.getPredictionModels(options, listener);

            }
        });

    }

    protected ActivityName getPredictionActivityName(){return predictionActivityName;}


    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int build_option_id = item.getItemId();
        if(build_option_id == R.id.Build_Prediction_model)
        {
//            View build_model_view = findViewById(build_option_id);
//            registerForContextMenu(build_model_view);
            //createPredictionModelDialogBox();
            ActivityPredictionOptions options = new ActivityPredictionOptions(predictionActivityName);
            PredictionListener listener = new PredictionListener(appContext, thisActivity,
                    predictionActivityName);
            behaviorAPI.createPredictionModel(options, listener);

            return true;
        }
        else{
            return false;
        }


    }

    public void createPredictionModelDialogBox(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle("Your Title");

        // set dialog message
        alertDialogBuilder
                .setMessage("Click yes to exit!")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        PredictionActivity.this.finish();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(" Select Activity type for Building Prediction Model");
        menu.add(0, v.getId(), 0, "RUNNING");
        menu.add(0, v.getId(), 0, "WALKING");
        menu.add(0, v.getId(), 0, "BIKING");
        menu.add(0, v.getId(), 0, "SEDENTARY");
        menu.add(0, v.getId(), 0, "INCAR");
        menu.add(0, v.getId(), 0, "ONTRAIN");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case NEW_MENU_ITEM:
//                showMsg("New");
//                break;
//            case SAVE_MENU_ITEM:
//                showMsg("Save");
//                break;
//        }
        return super.onContextItemSelected(item);
    }


    private void authenticate() {
        if (externalAuth.isInit()) {
            Log.w(LOG_TAG, "Already authenticated");
//            startDaemon();
            return;
        }
        authenticateWithGoogle();
    }


    private void authenticateWithGoogle(){
//        if(!externalAuth.isInit()) {
            externalAuth.init(
                    Settings.API_KEY,
                    Settings.SECRET,
                    Settings.REDIRECT_URI,
                    "context:post:device:sensor "
                            + "context:post:location:detailed "
                            + "context:post:device:information "
                            + "context:geolocation:detailed "
                            + "context:time:detailed "
                            + "context:weather "
                            + "context:location:detailed "
                            + "context:post:device:applications:running "
                            + "context:post:device:status:battery "
                            + "context:time:detailed "
                            + "context:post:device:telephony "
                            + "context:post:device:personal "
                            + "context:post:media:consumption "
                            + "context:device:sensor "
                            + "context:post:device:sensor",
                    new MyAuthCallback()
            );
//        }
//        else {
//            Toast.makeText(this, "Connected....", Toast.LENGTH_LONG).show();
//        }

    }

    /**
     * Callback invoked after attempted authorization.
     *
     */
    private class MyAuthCallback implements AuthCallback {
        @Override
        public void onSuccess() {
            Toast.makeText(appContext, "Init Success",
                    Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Init Success");
            invalidateOptionsMenu();
        }

        @Override
        public void onError(ContextError error) {
            Toast.makeText(appContext,
                    "Init Error: " + error.getMessage(), Toast.LENGTH_LONG)
                    .show();
            invalidateOptionsMenu();
        }

        @Override
        public void onExpired() {
            // TODO Auto-generated method stub
        }
    }
}
