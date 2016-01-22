package com.geominder.esiea.geonav;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ModifierActivity extends AppCompatActivity {
    private GoogleApiClient mGoogleApiClient;
    private EditText editTitre;
    private TimePickerDialog tpdDepart, tpdFin;
    private TextView timeDepart, timeFin, lieu;
    private TextView L,Ma,Me,J,V,S,D;
    private boolean dayState[] = {false,false,false,false,false,false,false};
    private ArrayList<Alarme> listAlarme = new ArrayList<>();
    private File fileAlarme;
    private String name, adresse;
    private LatLng latLng;
    private int position;
    private int PLACE_PICKER_REQUEST = 1;
    private PendingIntent mGeofencePendingIntent;
    private Geofence geofence;

    public final static String ALERTE = "com.octip.cours.inf4042_11.BIERS_UPDATE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier);

        Intent intent = getIntent();
        position = intent.getIntExtra(GererActivity.POSITION,0);

        editTitre = (EditText)findViewById(R.id.edit_titre);
        lieu = (TextView)findViewById(R.id.tv_lieu);
        timeDepart = (TextView)findViewById(R.id.edit_depart);
        timeFin = (TextView)findViewById(R.id.edit_fin);
        L = (TextView)findViewById(R.id.tv_lundi);
        Ma = (TextView)findViewById(R.id.tv_mardi);
        Me = (TextView)findViewById(R.id.tv_mercredi);
        J = (TextView)findViewById(R.id.tv_jeudi);
        V = (TextView)findViewById(R.id.tv_vendredi);
        S = (TextView)findViewById(R.id.tv_samedi);
        D = (TextView)findViewById(R.id.tv_dimanche);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                    }
                })
                .build();


        String filePath = this.getFilesDir().getPath() + "/liste_alarme.txt";
        fileAlarme = new File(filePath);
        try {
            fileAlarme.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadListAlarmeFromFile(listAlarme, fileAlarme);

        Alarme alarme = listAlarme.get(position);
        name = alarme.getLieu();
        adresse = alarme.getAdresse();
        latLng = new LatLng(alarme.getLatitude(),alarme.getLongitude());

        tpdDepart = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time;
                if(minute == 0 && hourOfDay != 0) {
                    time = hourOfDay + ":00";
                    timeDepart.setTextColor(getResources().getColor(R.color.BelizeHole));
                }
                else if(hourOfDay == 0 && minute != 0) {
                    time = "00:" + minute;
                    timeDepart.setTextColor(getResources().getColor(R.color.BelizeHole));
                }
                else if (hourOfDay == 0 && minute == 0){
                    time = "00:00";
                    timeDepart.setTextColor(getResources().getColor(R.color.Silver));
                }
                else {
                    time = hourOfDay + ":" + minute;
                    timeDepart.setTextColor(getResources().getColor(R.color.BelizeHole));
                }

                timeDepart.setText(time);
            }
        },12,0,true);

        tpdFin = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time;
                if(minute == 0 && hourOfDay != 0) {
                    time = hourOfDay + ":00";
                    timeFin.setTextColor(getResources().getColor(R.color.BelizeHole));
                }
                else if(hourOfDay == 0 && minute != 0) {
                    time = "00:" + minute;
                    timeFin.setTextColor(getResources().getColor(R.color.BelizeHole));
                }
                else if (hourOfDay == 0 && minute == 0){
                    time = "00:00";
                    timeFin.setTextColor(getResources().getColor(R.color.Silver));
                }
                else {
                    time = hourOfDay + ":" + minute;
                    timeFin.setTextColor(getResources().getColor(R.color.BelizeHole));
                }

                timeFin.setText(time);
            }
        },12,0,true);

        editTitre.setText(alarme.getTitre());
        lieu.setText(alarme.getLieu());
        timeDepart.setText(alarme.getTimeStart());
        timeDepart.setTextColor(getResources().getColor(R.color.BelizeHole));
        timeFin.setText(alarme.getTimeEnd());
        timeFin.setTextColor(getResources().getColor(R.color.BelizeHole));

        alarme.getDayState(dayState);
        for(int i = 0; i < dayState.length; i++){
            if(dayState[i])
                Log.d("TAG", "daystate["+i+"] = true");
            else
                Log.d("TAG", "daystate["+i+"] = false");
        }

        if(dayState[0]) {
            L.setTextColor(getResources().getColor(R.color.BelizeHole));
            L.setText(getResources().getString(R.string.lundi));
        }
        if(dayState[1]) {
            Ma.setTextColor(getResources().getColor(R.color.BelizeHole));
            Ma.setText(getResources().getString(R.string.mardi));
        }
        if(dayState[2]) {
            Me.setTextColor(getResources().getColor(R.color.BelizeHole));
            Me.setText(getResources().getString(R.string.mercredi));
        }
        if(dayState[3]) {
            J.setTextColor(getResources().getColor(R.color.BelizeHole));
            J.setText(getResources().getString(R.string.jeudi));
        }
        if(dayState[4]) {
            V.setTextColor(getResources().getColor(R.color.BelizeHole));
            V.setText(getResources().getString(R.string.vendredi));
        }
        if(dayState[5]) {
            S.setTextColor(getResources().getColor(R.color.BelizeHole));
            S.setText(getResources().getString(R.string.samedi));
        }
        if(dayState[6]) {
            D.setTextColor(getResources().getColor(R.color.BelizeHole));
            D.setText(getResources().getString(R.string.dimanche));
        }

        if(!dayState[0]) {
            L.setTextColor(getResources().getColor(R.color.Silver));
            L.setText(Html.fromHtml("<i>"+getResources().getString(R.string.lundi)+"</i>"));
        }
        if(!dayState[1]) {
            Ma.setTextColor(getResources().getColor(R.color.Silver));
            Ma.setText(Html.fromHtml("<i>"+getResources().getString(R.string.mardi)+"</i>"));
        }
        if(!dayState[2]) {
            Me.setTextColor(getResources().getColor(R.color.Silver));
            Me.setText(Html.fromHtml("<i>"+getResources().getString(R.string.mercredi)+"</i>"));
        }
        if(!dayState[3]) {
            J.setTextColor(getResources().getColor(R.color.Silver));
            J.setText(Html.fromHtml("<i>"+getResources().getString(R.string.jeudi)+"</i>"));
        }
        if(!dayState[4]) {
            V.setTextColor(getResources().getColor(R.color.Silver));
            V.setText(Html.fromHtml("<i>"+getResources().getString(R.string.vendredi)+"</i>"));
        }
        if(!dayState[5]) {
            S.setTextColor(getResources().getColor(R.color.Silver));
            S.setText(Html.fromHtml("<i>"+getResources().getString(R.string.samedi)+"</i>"));
        }
        if(!dayState[6]) {
            D.setTextColor(getResources().getColor(R.color.Silver));
            D.setText(Html.fromHtml("<i>"+getResources().getString(R.string.dimanche)+"</i>"));
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geofence);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {

        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }

        mGeofencePendingIntent = GeofenceService.startActionFoo(this);
        return mGeofencePendingIntent;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                name = place.getName().toString();
                adresse = place.getAddress().toString();
                latLng = place.getLatLng();
                lieu.setText(name);
                lieu.setTextColor(getResources().getColor(R.color.BelizeHole));
                Log.d("Place Picker", "result OK !");
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        Log.d("Google Place", "Connexion établie");
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        Log.d("Google Place", "Connexion finis");
    }

    public void selectPlace(View v){
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil
                    .getErrorDialog(e.getConnectionStatusCode(), this, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(this, "Google Play Services is not available.",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    public void enregistrerAction(View v){
        if(editTitre.getText() == null || (editTitre.getText() != null && editTitre.getText().toString().equals(""))) {
            AlertDialog.Builder alertDialog;
            alertDialog = new AlertDialog.Builder(this);
            alertDialog.setMessage("Formulaire mal rempli veuillez au moins indiquer un titre et un lieu");
            alertDialog.setNeutralButton(R.string.fermer, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
        }
        else {
            Alarme alarme1 = listAlarme.get(position);
            Alarme alarme = new Alarme(editTitre.getText().toString(), name,
                    timeDepart.getText().toString(), timeFin.getText().toString(), adresse,
                    latLng, dayState, true);
            listAlarme.remove(position);
            listAlarme.add(position, alarme);

            saveListAlarmeToFile(listAlarme, fileAlarme);

            geofence = new Geofence.Builder().setRequestId(alarme.getTitre())
                    .setCircularRegion(alarme.getLatitude(), alarme.getLongitude(), 500)
                    .setExpirationDuration(10000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .build();

            if(mGoogleApiClient.isConnected()){
                List<String> geofenceRequestId = new ArrayList<>();
                geofenceRequestId.add(alarme1.getTitre());
                LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, geofenceRequestId );
                LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, getGeofencingRequest(), getGeofencePendingIntent());

                IntentFilter intentFilter = new IntentFilter(ALERTE);
                LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(new GeofenceAlerte(), intentFilter);
            }

            Toast.makeText(getApplicationContext(), getResources().getString(R.string.modificationalarme), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, GererActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    public void supprimerAction(View v){
        Alarme alarme1 = listAlarme.get(position);
        listAlarme.remove(position);
        saveListAlarmeToFile(listAlarme, fileAlarme);

        if(mGoogleApiClient.isConnected()) {
            List<String> geofenceRequestId = new ArrayList<>();
            geofenceRequestId.add(alarme1.getTitre());
            LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, geofenceRequestId);
        }

        Toast.makeText(getApplicationContext(), getResources().getString(R.string.supressionalarme), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, GererActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void homeAction(View v){
        Intent intent = new Intent(this, GererActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void showTimeDepartAction(View v){
        tpdDepart.show();
    }

    public void showTimeFinAction(View v){
        tpdFin.show();
    }

    public void lundiAction(View v){
        if(!dayState[0]) {
            L.setTextColor(getResources().getColor(R.color.BelizeHole));
            L.setText(getResources().getString(R.string.lundi));
            dayState[0] = true;
        }
        else {
            L.setTextColor(getResources().getColor(R.color.Silver));
            L.setText(Html.fromHtml("<i>"+getResources().getString(R.string.lundi)+"</i>"));
            dayState[0] = false;
        }
    }

    public void mardiAction(View v){
        if(!dayState[1]) {
            Ma.setTextColor(getResources().getColor(R.color.BelizeHole));
            Ma.setText(getResources().getString(R.string.mardi));
            dayState[1] = true;
        }
        else {
            Ma.setTextColor(getResources().getColor(R.color.Silver));
            Ma.setText(Html.fromHtml("<i>"+getResources().getString(R.string.mardi)+"</i>"));
            dayState[1] = false;
        }
    }

    public void mercrediAction(View v){
        if(!dayState[2]) {
            Me.setTextColor(getResources().getColor(R.color.BelizeHole));
            Me.setText(getResources().getString(R.string.mercredi));
            dayState[2] = true;
        }
        else {
            Me.setTextColor(getResources().getColor(R.color.Silver));
            Me.setText(Html.fromHtml("<i>"+getResources().getString(R.string.mercredi)+"</i>"));
            dayState[2] = false;
        }
    }

    public void jeudiAction(View v){
        if(!dayState[3]) {
            J.setTextColor(getResources().getColor(R.color.BelizeHole));
            J.setText(getResources().getString(R.string.jeudi));
            dayState[3] = true;
        }
        else {
            J.setTextColor(getResources().getColor(R.color.Silver));
            J.setText(Html.fromHtml("<i>" + getResources().getString(R.string.jeudi) + "</i>"));
            dayState[3] = false;
        }
    }

    public void vendrediAction(View v){
        if(!dayState[4]) {
            V.setTextColor(getResources().getColor(R.color.BelizeHole));
            V.setText(getResources().getString(R.string.vendredi));
            dayState[4] = true;
        }
        else {
            V.setTextColor(getResources().getColor(R.color.Silver));
            V.setText(Html.fromHtml("<i>" + getResources().getString(R.string.vendredi) + "</i>"));
            dayState[4] = false;
        }
    }

    public void samediAction(View v){
        if(!dayState[5]) {
            S.setTextColor(getResources().getColor(R.color.BelizeHole));
            S.setText(getResources().getString(R.string.samedi));
            dayState[5] = true;
        }
        else {
            S.setTextColor(getResources().getColor(R.color.Silver));
            S.setText(Html.fromHtml("<i>" + getResources().getString(R.string.samedi) + "</i>"));
            dayState[5] = false;
        }
    }

    public void dimancheAction(View v){
        if(!dayState[6]) {
            D.setTextColor(getResources().getColor(R.color.BelizeHole));
            D.setText(getResources().getString(R.string.dimanche));
            dayState[6] = true;
        }
        else {
            D.setTextColor(getResources().getColor(R.color.Silver));
            D.setText(Html.fromHtml("<i>" + getResources().getString(R.string.dimanche) + "</i>"));
            dayState[6] = false;
        }
    }

    private void saveListAlarmeToFile(ArrayList<Alarme> listAlarme, File file) {
        try{
            ObjectOutputStream dataOut = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));

            for (Alarme alarme : listAlarme)
                dataOut.writeObject(alarme);

            dataOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadListAlarmeFromFile(ArrayList<Alarme> listAlarme, File file){
        try{
            ObjectInputStream dataIn = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));

            Alarme alarme;
            try {

                while ((alarme = (Alarme)dataIn.readObject()) != null)
                    listAlarme.add(alarme);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            dataIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class GeofenceAlerte extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            String geofenceTransitionDetails = intent.getStringExtra("geofenceTransitionDetails");
            sendNotification(geofenceTransitionDetails);
        }

        private void sendNotification(String notificationDetails) {

            Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(notificationIntent);

            PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                            R.mipmap.ic_launcher))
                    .setColor(Color.RED)
                    .setContentTitle(notificationDetails)
                    .setContentText(lieu.getText())
                    .setContentIntent(notificationPendingIntent);
            builder.setAutoCancel(true);

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(0, builder.build());
        }
    }
}
