package com.geominder.esiea.geonav;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class GeofenceService extends IntentService {

    private static final String ACTION_FOO = "com.geominder.esiea.geonav.action.FOO";


    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static PendingIntent startActionFoo(Context context) {
        Intent intent = new Intent(context, GeofenceService.class);
        intent.setAction(ACTION_FOO);
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public GeofenceService() {
        super("GeofenceService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent != null) {
            handleActionFoo(intent);

        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = "geofencing event has error";
            Log.e("TAG", errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition, triggeringGeofences);

            // Send notification and log the transition details.
            Intent intent1 = new Intent(AjouterActivity.ALERTE);
            intent1.putExtra("geofenceTransitionDetails", geofenceTransitionDetails);

            LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);

            Log.i("TAG", geofenceTransitionDetails);
        } else {
            // Log the error.
            Log.e("TAG", getString(R.string.common_google_play_services_api_unavailable_text,
                    geofenceTransition));

        }
    }

        private String getGeofenceTransitionDetails(int geofenceTransition, List<Geofence> triggeringGeofences) {

            String geofenceTransitionString = getTransitionString(geofenceTransition);

            // Get the Ids of each geofence that was triggered.
            ArrayList triggeringGeofencesIdsList = new ArrayList();
            for (Geofence geofence : triggeringGeofences) {
                triggeringGeofencesIdsList.add(geofence.getRequestId());
            }

            return TextUtils.join(", ", triggeringGeofencesIdsList);

        }

        private String getTransitionString(int transitionType) {
            switch (transitionType) {
                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    return "Enter : ";
                default:
                    return null;
            }
        }

}
