package com.intel.samples.contextsensingapiflow;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.intel.context.Auth;
import com.intel.context.Historical;
import com.intel.context.Sensing;
import com.intel.context.auth.AuthCallback;
import com.intel.context.auth.Provider;
import com.intel.context.auth.Token;
import com.intel.context.error.ContextError;
import com.intel.context.exception.ContextException;
import com.intel.context.exception.ContextProviderException;
import com.intel.context.item.Item;
import com.intel.context.item.LocationCurrent;
import com.intel.context.sensing.InitCallback;
import com.intel.samples.contextsensingapiflow.listener.ActivityRecognitionListener;
import com.intel.samples.contextsensingapiflow.listener.AudioClassificationListener;
import com.intel.samples.contextsensingapiflow.listener.BatteryListener;
import com.intel.samples.contextsensingapiflow.listener.BeaconListener;
import com.intel.samples.contextsensingapiflow.listener.CalendarListener;
import com.intel.samples.contextsensingapiflow.listener.CallListener;
import com.intel.samples.contextsensingapiflow.listener.ContactsListener;
import com.intel.samples.contextsensingapiflow.listener.DateListener;
import com.intel.samples.contextsensingapiflow.listener.DeviceInformationListener;
import com.intel.samples.contextsensingapiflow.listener.DevicePositionListener;
import com.intel.samples.contextsensingapiflow.listener.EarTouchListener;
import com.intel.samples.contextsensingapiflow.listener.EventFilter;
import com.intel.samples.contextsensingapiflow.listener.FlickListener;
import com.intel.samples.contextsensingapiflow.listener.GeographicListener;
import com.intel.samples.contextsensingapiflow.listener.GlyphListener;
import com.intel.samples.contextsensingapiflow.listener.IApplicationListener;
import com.intel.samples.contextsensingapiflow.listener.IApplicationListener.UpdateNotifier;
import com.intel.samples.contextsensingapiflow.listener.InstalledAppsListener;
import com.intel.samples.contextsensingapiflow.listener.InstantActivityListener;
import com.intel.samples.contextsensingapiflow.listener.LiftListener;
import com.intel.samples.contextsensingapiflow.listener.LocationListener;
import com.intel.samples.contextsensingapiflow.listener.MessageListener;
import com.intel.samples.contextsensingapiflow.listener.MusicListener;
import com.intel.samples.contextsensingapiflow.listener.NearbyResturantListener;
import com.intel.samples.contextsensingapiflow.listener.NetworkListener;
import com.intel.samples.contextsensingapiflow.listener.PanZoomTiltListener;
import com.intel.samples.contextsensingapiflow.listener.PedometerListener;
import com.intel.samples.contextsensingapiflow.listener.PlaceListener;
import com.intel.samples.contextsensingapiflow.listener.ProviderOption;
import com.intel.samples.contextsensingapiflow.listener.RunningAppsListener;
import com.intel.samples.contextsensingapiflow.listener.ShakingListener;
import com.intel.samples.contextsensingapiflow.listener.TappingListener;
import com.intel.samples.contextsensingapiflow.listener.TerminalContextListener;
import com.intel.samples.contextsensingapiflow.listener.TrafficListener;
import com.intel.samples.contextsensingapiflow.listener.TimeZoneListener;
import com.intel.samples.contextsensingapiflow.listener.UDGListener;
import com.intel.samples.contextsensingapiflow.listener.WeatherListener;
import com.renn.rennsdk.AccessToken;
import com.renn.rennsdk.RennClient;
import com.renn.rennsdk.RennClient.LoginListener;
import com.facebook.Session;
import com.facebook.SessionState;
import android.content.Intent;

/**
 * Android activity to demonstrate usage of the Context Sensing API.
 *
 */
public class ContextSensingApiFlowSampleActivity extends Activity {
    private static final String LOG_TAG = ContextSensingApiFlowSampleActivity.class.getName();

    private ListView mListView;
    private ListenerAdapter mListenerAdapter;
    private ProviderActionModeCallback mActionModeCallback;
    private UpdateNotifier mUpdateNotifier;

    private Auth mAuth;
    private RennClient mRennClient;
    private Sensing mySensing;
    private ArrayList<CompoundButton.OnCheckedChangeListener> mCheckboxListeners;
    private ArrayList<OnLongClickListener> mLongClickListeners;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private ActionMode mOpenActionMode = null;
    private static boolean FACEBOOK_AUTH_FLAG = false ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        mAuth = Auth.getInstance(this);
        mySensing = getApplicationInstance().getSensing();
        mRennClient = RennClient.getInstance(this);

        mUpdateNotifier = new UpdateNotifier() {

            @Override
            public void notifyUpdate(final int position, final String info) {
                if (mListenerAdapter == null) {
                    return;
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateListenerInfo(position, info);
                    }
                });
            }

            @Override
            public void notifyError(final String error) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        };

        ArrayList<IApplicationListener> listeners = getApplicationInstance().getApplicationListeners();
        if (listeners.isEmpty()) {
            createListeners(listeners, mUpdateNotifier);
        }

        // There needs to be one long click listener and one checkbox listener per application listener.
        mCheckboxListeners = new ArrayList<CompoundButton.OnCheckedChangeListener>(
                Collections.nCopies(listeners.size(), (CompoundButton.OnCheckedChangeListener) null));
        mLongClickListeners = new ArrayList<OnLongClickListener>(Collections.nCopies(listeners.size(), (OnLongClickListener) null));

        mListenerAdapter = new ListenerAdapter();
        mActionModeCallback = new ProviderActionModeCallback();
        mListView = (ListView) findViewById(R.id.main_list_view);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mListView.setAdapter(mListenerAdapter);
    }

    
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(FACEBOOK_AUTH_FLAG)
    	{
    		super.onActivityResult(requestCode, resultCode, data);
    		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    		FACEBOOK_AUTH_FLAG = false;
    	}
    }
    
    
    
    
    private void updateListenerInfo(int position, String info) {
        int firstVisible = mListView.getFirstVisiblePosition();
        int lastVisiblePosition = mListView.getLastVisiblePosition();
        if (position < firstVisible || position > lastVisiblePosition) {
            return;
        }

        View viewToUpdate = mListView.getChildAt(position - firstVisible);
        TextView infoTextView = (TextView) viewToUpdate.findViewById(R.id.provider_list_item_data);
        infoTextView.setText(info);
    }

    private ContextSensingApiFlowSampleApplication getApplicationInstance() {
        return (ContextSensingApiFlowSampleApplication) getApplicationContext();
    }

    private void createListeners(ArrayList<IApplicationListener> listeners, UpdateNotifier notifier) {

        listeners.add(new LocationListener(notifier, listeners.size()));
        listeners.add(new PedometerListener(notifier, listeners.size()));
        listeners.add(new TerminalContextListener(notifier, listeners.size()));
        listeners.add(new ActivityRecognitionListener(notifier, listeners.size()));
        listeners.add(new AudioClassificationListener(notifier, listeners.size()));
        listeners.add(new RunningAppsListener(notifier, listeners.size()));
        listeners.add(new InstalledAppsListener(notifier, listeners.size()));
        listeners.add(new BatteryListener(notifier, listeners.size()));
        listeners.add(new BeaconListener(notifier, listeners.size()));
        listeners.add(new CallListener(notifier, listeners.size()));
        listeners.add(new ContactsListener(notifier, listeners.size()));
        listeners.add(new CalendarListener(notifier, listeners.size()));
        listeners.add(new DeviceInformationListener(notifier, listeners.size()));
        listeners.add(new MessageListener(notifier, listeners.size()));
        listeners.add(new MusicListener(notifier, listeners.size()));
        listeners.add(new NetworkListener(notifier, listeners.size()));
        listeners.add(new TappingListener(notifier, listeners.size()));
        listeners.add(new EarTouchListener(notifier, listeners.size()));
        listeners.add(new FlickListener(notifier, listeners.size()));
        listeners.add(new ShakingListener(notifier, listeners.size()));
        listeners.add(new InstantActivityListener(notifier, listeners.size()));
        listeners.add(new LiftListener(notifier, listeners.size()));
        listeners.add(new UDGListener(notifier, listeners.size()));        
        listeners.add(new GlyphListener(notifier, listeners.size()));
        listeners.add(new PanZoomTiltListener(notifier, listeners.size()));
        listeners.add(new DevicePositionListener(notifier, listeners.size()));
        // TODO: Cloud divider
        listeners.add(new PlaceListener(notifier, listeners.size()));
        listeners.add(new GeographicListener(notifier, listeners.size()));
        listeners.add(new WeatherListener(notifier, listeners.size()));
        listeners.add(new DateListener(notifier, listeners.size()));
        listeners.add(new NearbyResturantListener(notifier, listeners.size()));
        listeners.add(new TrafficListener(notifier, listeners.size()));
        listeners.add(new TimeZoneListener(notifier, listeners.size()));
    }



    private void enableProvider(IApplicationListener listener) {
        if (!getApplicationInstance().isStarted()) {
            Toast.makeText(getApplicationContext(),
                    "Error adding listener to provider: Service needs to be started.",
                    Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Error adding listener: Service needs to be started.");
            mListenerAdapter.notifyDataSetChanged();
            return;
        }

        if (listener.isRunning()) {
            mListenerAdapter.notifyDataSetChanged();
            return;
        }

        try {
            if (mOpenActionMode != null) {
                mActionModeCallback.mRestartAfterClosing = false; // To avoid a double-start.
                mOpenActionMode.finish();
            }

            mySensing.addContextTypeListener(listener.getContextType(), listener);

            if (!listener.shouldNotStartSensing()) {
                mySensing.enableSensing(listener.getContextType(), listener.getProviderOptionsBundle());
            }

            listener.setIsRunning(true);
        } catch (ContextProviderException e) {
            Toast.makeText(getApplicationContext(), "Error adding listener to provider: " + e.getMessage(), 
                    Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Error adding listener: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            Toast.makeText(getApplicationContext(), "Error adding listener to provider: " + e.getMessage(), 
                    Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Error adding listener: " + e.getMessage());
        }

        mListenerAdapter.notifyDataSetChanged();
    }

    private void disableProvider(IApplicationListener listener) {
        if (!listener.isRunning()) {
            mListenerAdapter.notifyDataSetChanged();
            return;
        }

        try {
            if (!listener.shouldNotStartSensing()) {
                mySensing.disableSensing(listener.getContextType());
            }
            mySensing.removeContextTypeListener(listener);
            listener.setIsRunning(false);
        } catch (ContextProviderException e) {
            Toast.makeText(getApplicationContext(), "Error removing listener from provider: " + e.getMessage(), 
                    Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Error adding listener: " + e.getMessage());
        }

        mListenerAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem startServerBtn = menu.findItem(R.id.menu_btn_start_server);
        MenuItem stopServerBtn = menu.findItem(R.id.menu_btn_stop_server);

        boolean serviceStarted = getApplicationInstance().isStarted();

        startServerBtn.setVisible(!serviceStarted);
        stopServerBtn.setVisible(serviceStarted);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_btn_start_server:
            item.setEnabled(false);
            authenticate();
            startDaemon();
            return true;
        case R.id.menu_btn_stop_server:
            item.setEnabled(false);
            stopDaemon();
            return true;
        case R.id.menu_btn_train_home:
            trainHome();
            return true;
        case R.id.menu_btn_train_work:
            trainWork();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Wrapper function around the various authentication methods. If already authenticated with IDP,
     * the daemon will be started.
     */
    private void authenticate() {
        if (mAuth.isInit()) {
            Log.w(LOG_TAG, "Already authenticated");
            startDaemon();
            return;
        }

        createIdpSelectionDialog();
    }

    /**
     * Triggers authentication with the given Identity Provider type.
     * @param providerType The IDP to authenticate with.
     */
    private void authenticateWithIdp(IdentityProviderType providerType) {
        switch(providerType) {
        case GOOGLE:
            authenticateWithGoogle();
            break;
        case FACEBOOK:
            authenticateWithFacebook();
            break;
        case RENREN:
            authenticateWithRenren();
            break;
        }
    }

    private void authenticateWithGoogle(){
        mAuth.init(
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
                        new MyAuthCallback());
    }


    private void authenticateWithRenren(){

        //**********RENREN CODE***********//
        mRennClient.init(Settings.RENREN_APPID,Settings.RENREN_API_KEY, Settings.RENREN_SECRET_KEY);
        mRennClient.setScope("read_user_feed read_user_status");
        mRennClient.setTokenType("bearer");
        mRennClient.setLoginListener(new LoginListener() {
            @Override
            public void onLoginSuccess() {
                AccessToken token = mRennClient.getAccessToken();
                mAuth.init(
                		Settings.API_KEY,
                        Settings.SECRET,
                        Settings.REDIRECT_URI,
                        "context:location:detailed context:post:location:detailed context:location:checkin context:post:location:checkin context:poi:search context:post:poi:search context:ratings context:post:ratings context:developer-specific prediction:location prediction:poi-weights location:enhanced prediction:location-id context:geolocation:detailed context:time:detailed context:device:applications:running context:post:device:applications:running context:device:telephony context:post:device:telephony context:device:calendar context:post:device:calendar context:device:status:battery context:post:device:status:battery context:media:consumption context:post:media:consumption context:device:information context:post:device:information context:device:personal context:post:device:personal context:device:sensor context:post:device:sensor context:weather",
                        new Token(token.accessToken,"Bearer", token.refreshToken, Provider.RENREN),
                        new MyAuthCallback()); 
            }
            @Override
            public void onLoginCanceled() {
                // TODO Auto-generated method stub
            }

        });
        mRennClient.login(this); 
    }


    private void authenticateWithFacebook(){

        //**********FACEBOOK CODE***********//
      
    	FACEBOOK_AUTH_FLAG = true;
        Session.openActiveSession(this, true, null, new Session.StatusCallback() {
            @Override
            public void call(Session facebookSession, SessionState state, Exception exception) {
                if (facebookSession.isOpened()) {
                    Toast.makeText(getApplicationContext(), "Facebook session successfully opened",
                        Toast.LENGTH_SHORT).show();
 
                     
                     Token   token =
                    		 new Token(facebookSession.getAccessToken(),"Bearer", facebookSession.getAccessToken(), Provider.FACEBOOK);

                    mAuth
                        .init(
                        		Settings.API_KEY,
                                Settings.SECRET,
                                Settings.REDIRECT_URI,
                            "context:post:device:sensor context:post:location:detailed context:post:device:information context:geolocation:detailed context:time:detailed context:weather context:location:detailed context:post:device:applications:running context:post:device:status:battery context:time:detailed context:post:device:telephony context:post:device:personal context:post:media:consumption",
                             token, new MyAuthCallback());
                }
            }
        });
    }


    private void removeAuthorization() {
        Auth auth = Auth.getInstance(this);
        auth.release();
    }

    private void createIdpSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ContextSensingApiFlowSampleActivity.this);
        final String[] possibleValuesArray = new String[] {
                IdentityProviderType.GOOGLE.getName(), 
                IdentityProviderType.FACEBOOK.getName(), 
                IdentityProviderType.RENREN.getName() };
        int checkedItem = 0;

        builder .setTitle("Select Identity Provider")
        .setSingleChoiceItems(possibleValuesArray, checkedItem, null)
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                int selectedItemPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                authenticateWithIdp(IdentityProviderType.fromName(possibleValuesArray[selectedItemPosition]));
            }
        }).create().show();
    }

    private void startDaemon() {
        mySensing.start(new InitCallback() {
            public void onSuccess() {
                Toast.makeText(getApplicationContext(),
                        "Context Sensing Daemon Started", Toast.LENGTH_SHORT)
                        .show();
                invalidateOptionsMenu();
                /*
                 * After successfully starting the Context Sensing Daemon, we
                 * can enable the sensing of context states such as activity
                 * recognition, location, etc.
                 */

                getApplicationInstance().start();
            }

            public void onError(ContextError error) {
                Toast.makeText(getApplicationContext(),
                        "Error: " + error.getMessage(), Toast.LENGTH_LONG)
                        .show();
                invalidateOptionsMenu();
            }
        });
    }

    private void stopDaemon() {
        try {
            stopAllProviders();
            mySensing.stop();
            removeAuthorization();
            getApplicationInstance().stop();
        } catch (RuntimeException e) {
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Error: " + e.getMessage());
        }
        invalidateOptionsMenu();
    }

    private void stopAllProviders() {
        for (IApplicationListener listener : getApplicationInstance().getApplicationListeners()) {
            disableProvider(listener);
        }
    }

    private void trainWork() {
        Toast.makeText(getApplicationContext(), "Training Work", Toast.LENGTH_SHORT).show();
        setCurrentLocationByTime(9); // TODO: Need to understand what "9" means in this context.
    }

    private void trainHome() {
        Toast.makeText(getApplicationContext(), "Training Home", Toast.LENGTH_SHORT).show();
        setCurrentLocationByTime(22); // TODO: Need to understand what "22" means in this context.
    }

    /**
     * Searches the {@link IApplicationListener} list for a listener of the given type.
     * @param listenerType Class object identifying the type of listener to find.
     * @param <T> The type of listener to find.
     * @return The listener. Note: if multiple listeners of the same type have been added to the list,
     * only the first will be returned.
     * @throws IllegalArgumentException If the listener is not found. This indicates a programming error
     * as the listener has not been added to the list.
     */
    private <T extends IApplicationListener> T findListener(Class<T> listenerType) {
        for (IApplicationListener listener : getApplicationInstance().getApplicationListeners()) {
            if (listener.getClass().equals(listenerType)) {
                return listenerType.cast(listener);
            }
        }

        throw new IllegalArgumentException("Listener of type \"" + listenerType.getSimpleName() + "\" has not been added.");
    }

    /*
     * This method forces the training of home/work based on the current Location state.
     * Use this method in order to simulate the 14 days required model training.
     */
    private void setCurrentLocationByTime(int hour) {
        LocationListener locationListener = findListener(LocationListener.class);


        // Listener must be running.
        if (!locationListener.isRunning()) {
            Toast.makeText(getApplicationContext(),
                    "Please enable the sensing of Location before training home/work.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Listener must have reported back with an item.
        if (locationListener.getLastKnownItem() == null) {
            Toast.makeText(getApplicationContext(),
                    "Please wait until location provider has reported location before training home/work.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        Historical historical = new Historical(getApplicationContext());    
        List<Item> items = new ArrayList<Item>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE),
                hour,
                0);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE),
                hour,
                5);

        for (int i=0; i < 20; i++) {
            LocationCurrent item = new LocationCurrent();
            LocationCurrent lastKnown = (LocationCurrent) locationListener.getLastKnownItem();
            item.setActivity("urn:activity:in");
            item.setAccuracy(lastKnown.getAccuracy());
            item.setLocation(lastKnown.getLocation());
            item.setTimestamp(calendar.getTimeInMillis());

            LocationCurrent item2 = new LocationCurrent();
            item2.setActivity("urn:activity:in");
            item2.setAccuracy(lastKnown.getAccuracy());
            item2.setLocation(lastKnown.getLocation());
            item2.setTimestamp(calendar2.getTimeInMillis());

            items.add(item);
            items.add(item2);
            calendar.add(Calendar.DATE, -1);
            calendar2.add(Calendar.DATE, -1);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
        }
        try {
            historical.setItem(items);
        } catch (ContextException e) {
            Toast.makeText(getApplicationContext(),
                    "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Error: " + e.getMessage());
        }
    }

    /**
     * Callback invoked after attempted authorization.
     *
     */
    private class MyAuthCallback implements AuthCallback {
        @Override
        public void onSuccess() {
            Toast.makeText(getApplicationContext(), "Init Success",
                    Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Init Success");
            invalidateOptionsMenu();
        }

        @Override
        public void onError(ContextError error) {
            Toast.makeText(getApplicationContext(),
                    "Init Error: " + error.getMessage(), Toast.LENGTH_LONG)
                    .show();
            invalidateOptionsMenu();
        }

        @Override
        public void onExpired() {
            // TODO Auto-generated method stub
        }
    }

    /**
     * ListView adapter to display each provider.
     * 
     */
    private class ListenerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return getApplicationInstance().getApplicationListeners().size();
        }

        @Override
        public Object getItem(int position) {
            return getApplicationInstance().getApplicationListeners().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // Get our item.
            final IApplicationListener currentItem = (IApplicationListener) getItem(position);

            // Reuse view (if possible), otherwise inflate a new one.
            ViewGroup providerLayout = (ViewGroup) convertView;
            if (providerLayout == null) {
                providerLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.provider_list_item, parent, false);
            }

            // Reference view components.
            CheckBox enabledCheckbox = (CheckBox) providerLayout.findViewById(R.id.provider_list_enabled_cbx);
            TextView statusTextView = (TextView) providerLayout.findViewById(R.id.provider_list_item_status);
            TextView providerNameTextView = (TextView) providerLayout.findViewById(R.id.provider_list_item_name);
            TextView providerDataTextView = (TextView) providerLayout.findViewById(R.id.provider_list_item_data);

            // We must remove any checkbox change listener already applied to the checkbox (will be re-added later).
            enabledCheckbox.setOnCheckedChangeListener(null);

            // Determine what will be displayed.
            boolean displayChecked = currentItem.isRunning();

            String name = currentItem.getName();

            String statusString = (currentItem.isRunning()) 
                    ? getResources().getString(R.string.providerStarted) 
                            : getResources().getString(R.string.providerStopped);

                    int statusColor = (currentItem.isRunning()) 
                            ? getResources().getColor(R.color.provider_started_color) 
                                    : getResources().getColor(android.R.color.primary_text_dark);

                            String data = currentItem.describeLastItem();

                            // Update all view elements.
                            enabledCheckbox.setChecked(displayChecked);
                            providerNameTextView.setText(name);
                            statusTextView.setText(statusString);
                            statusTextView.setTextColor(statusColor);
                            providerDataTextView.setText(data);

                            // Add all listeners.
                            providerLayout.setOnLongClickListener(getLongClickListenerFor(position));
                            enabledCheckbox.setOnCheckedChangeListener(getOnCheckedListenerFor(position, currentItem));

                            return providerLayout;
        }

    }

    private OnLongClickListener getLongClickListenerFor(int position) {
        OnLongClickListener listener = mLongClickListeners.get(position);
        if (listener == null) {
            listener = new OpenProviderActionMenuListener(position);
            mLongClickListeners.set(position, listener);
        }

        return listener;
    }

    private CompoundButton.OnCheckedChangeListener getOnCheckedListenerFor(int position, IApplicationListener currentItem) {
        CompoundButton.OnCheckedChangeListener listener = mCheckboxListeners.get(position);
        if (listener == null) {
            listener = new EnableProviderCheckboxChangeListener(currentItem);
            mCheckboxListeners.set(position, listener);
        }

        return listener;
    }

    /**
     * Listener used to open the provider action mode.
     *
     */
    private class OpenProviderActionMenuListener implements OnLongClickListener {
        private final int mPosition;

        public OpenProviderActionMenuListener(int position) {
            this.mPosition = position;
        }

        @Override
        public boolean onLongClick(View v) {
            mListView.setItemChecked(mPosition, true);
            
            /* Workaround for the issue described @
             * https://code.google.com/p/android/issues/detail?id=159527
             */
            final ActionMode mode = startActionMode(mActionModeCallback);
            if (mode != null) {
            	mode.invalidate();
            }
            return true;
        }
    }

    /**
     * Checkbox change listener invoked when a provider checkbox is checked/unchecked.
     *
     */
    private class EnableProviderCheckboxChangeListener implements CompoundButton.OnCheckedChangeListener {

        private final IApplicationListener mListener;

        public EnableProviderCheckboxChangeListener(IApplicationListener listener) {
            this.mListener = listener;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                enableProvider(mListener);
            } else {
                disableProvider(mListener);
            }
        }
    }

    /**
     * Callback to handle action mode for the provider list items.
     */
    private class ProviderActionModeCallback implements ActionMode.Callback {

        private static final int CACHE_OPTION_ID = 0xffff;
        private IApplicationListener mSelected;
        private List<ProviderOption> mProviderOptions;
        private boolean mRestartAfterClosing;

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == CACHE_OPTION_ID) {
                createFilterDialog(mSelected.getEventFilter());
                return false;
            }

            createDialog(mProviderOptions.get(item.getItemId()));
            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mOpenActionMode = mode;
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.provider_context_action_menu, menu);

            int selectedIndex = mListView.getCheckedItemPosition();
            ArrayList<IApplicationListener> applicationListeners = getApplicationInstance().getApplicationListeners();
            mSelected = applicationListeners.get(selectedIndex);
            mode.setTitle(mSelected.getName());
            mProviderOptions = mSelected.getProviderOptions();

            mRestartAfterClosing = mSelected.isRunning();
            disableProvider(mSelected);

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (mRestartAfterClosing) {
                mRestartAfterClosing = false;
                enableProvider(mSelected);
            }
            mSelected = null;
            mOpenActionMode = null;
            mProviderOptions = null;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            menu.clear();

            int index = 0;
            for (ProviderOption current : mProviderOptions) {
                MenuItem added = menu.add(Menu.NONE, index++, Menu.NONE, current.getName() + ": " + current.getCurrentValue());
                added.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            }

            if (mSelected.getEventFilter() != null) {
                MenuItem added = menu.add(Menu.NONE, CACHE_OPTION_ID, Menu.NONE, "Filter");
                added.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }
            return true;
        }

        private void createFilterDialog(final EventFilter filter) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ContextSensingApiFlowSampleActivity.this);
            final String[] possibleValuesArray = filter.getPossibleEvents().toArray(new String[filter.getPossibleEvents().size()]);
            boolean[] checkedValues = new boolean[possibleValuesArray.length];
            for (int i = 0; i < possibleValuesArray.length; i++) {
                checkedValues[i] = filter.elementIsInFilter(possibleValuesArray[i]);
            }

            builder .setTitle("Filter")
            .setMultiChoiceItems(possibleValuesArray, checkedValues, new DialogInterface.OnMultiChoiceClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    if (isChecked) {
                        filter.addFilterElement(possibleValuesArray[which]);
                    } else {
                        filter.removeFilterElement(possibleValuesArray[which]);
                    }
                }
            })
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Nothing extra needed. This will just dismiss the dialog.
                }
            }).create().show();
        }

        private void createDialog(final ProviderOption option) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ContextSensingApiFlowSampleActivity.this);
            String[] possibleValuesArray = option.getPossibleValues().toArray(new String[option.getPossibleValues().size()]);
            int checkedItem = option.getPossibleValues().indexOf(option.getCurrentValue());

            builder .setTitle(option.getName())
            .setSingleChoiceItems(possibleValuesArray, checkedItem, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    option.setCurrentValue(option.getPossibleValues().get(which));
                }
            })
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Nothing else to do here - just close.
                }
            }).create().show();
        }

    }

    /**
     * Enumeration of identity providers.
     */
    public static enum IdentityProviderType {
        GOOGLE("Google"),
        FACEBOOK("Facebook"),
        RENREN("Renren");
        
        private final String mName;
        private IdentityProviderType(String name) {
            mName = name;
        }
        
        /**
         * Get the human-readable name of the identity provider. 
         * @return The name.
         */
        public String getName() {
            return mName;
        }
        
        /**
         * Creates an {@link IdentityProviderType} from the provided name. Matching
         * is not case sensitive.
         * @param name The name of the identity provider requested.
         * @return The selected {@link IdentityProviderType}
         * @throws IllegalArgumentException If name does not correspond to an {@link IdentityProviderType}
         */
        public static IdentityProviderType fromName(String name) {
            for(IdentityProviderType type : IdentityProviderType.values()) {
                if(type.mName.equalsIgnoreCase(name))
                    return type;
            }
            
            // Not found.
            throw new IllegalArgumentException("Invalid Identity Provider name: " + name);
        }
    }
}
