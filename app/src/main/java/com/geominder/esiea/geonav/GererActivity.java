package com.geominder.esiea.geonav;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

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

public class GererActivity extends AppCompatActivity {
    private ArrayList<Alarme> listAlarme = new ArrayList<>();
    private File fileAlarme;
    protected RecyclerView recyclerView;
    private GoogleApiClient mGoogleApiClient;
    private PendingIntent mGeofencePendingIntent;
    private Geofence geofence;
    private int position;

    public final static String POSITION = "com.geominder.esiea.geonav.position";
    public final static String ALERTE = "com.octip.cours.inf4042_11.BIERS_UPDATE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerer);

        recyclerView = (RecyclerView)findViewById(R.id.listeAlarme);

        String filePath = this.getFilesDir().getPath() + "/liste_alarme.txt";
        fileAlarme = new File(filePath);
        try {
            fileAlarme.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadListAlarmeFromFile(listAlarme, fileAlarme);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
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

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new AlarmAdapter(listAlarme));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
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

    public void homeAction(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void ajouterAction(View v){
        Intent intent = new Intent(this, AjouterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

    class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmHolder> {
        private ArrayList<Alarme> listAlarme = new ArrayList<>();

        public AlarmAdapter(ArrayList<Alarme> listAlarme){
            this.listAlarme = listAlarme;
        }

        @Override
        public AlarmHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.liste_alarme_element, parent, false);
            return new AlarmHolder(view);
        }

        @Override
        public void onBindViewHolder(AlarmHolder holder, int position) {
            holder.titre.setText(listAlarme.get(position).getTitre());

            if (listAlarme.get(position).getIsActivated()) {
                holder.state.setChecked(true);
                holder.titre.setTextColor(getResources().getColor(R.color.BelizeHole));
            }
            else {
                holder.state.setChecked(false);
                holder.titre.setTextColor(getResources().getColor(R.color.Silver));
            }
        }

        @Override
        public int getItemCount() {
            return listAlarme.size();
        }

        class AlarmHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
            public TextView titre;
            public CheckBox state;

            public AlarmHolder(View itemView){
                super(itemView);
                this.titre = (TextView)itemView.findViewById(R.id.element_titre);
                this.state = (CheckBox)itemView.findViewById(R.id.element_state);

                itemView.setClickable(true);
                itemView.setOnLongClickListener(this);

                this.state.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        position = getPosition();

                        if(state.isChecked()) {
                            titre.setTextColor(getResources().getColor(R.color.BelizeHole));
                            listAlarme.get(position).setIsActivated(true);

                            geofence = new Geofence.Builder().setRequestId(listAlarme.get(position).getTitre())
                                    .setCircularRegion(listAlarme.get(position).getLatitude(), listAlarme.get(position).getLongitude(), 500)
                                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                                    .build();


                            if(mGoogleApiClient.isConnected()){
                                LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, getGeofencingRequest(), getGeofencePendingIntent());
                                Log.d("TEST", "Geofence add");
                            }
                        }
                        else {
                            titre.setTextColor(getResources().getColor(R.color.Silver));
                            listAlarme.get(position).setIsActivated(false);

                            if(mGoogleApiClient.isConnected()){
                                List<String> geofenceRequestId = new ArrayList<>();
                                geofenceRequestId.add(listAlarme.get(position).getTitre());

                                LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, geofenceRequestId);
                                Log.d("TEST", "Geofence remove");
                            }
                        }

                        saveListAlarmeToFile(listAlarme,fileAlarme);
                        IntentFilter intentFilter = new IntentFilter(ALERTE);
                        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(new GeofenceAlerte(), intentFilter);
                    }
                });
            }

            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(getBaseContext(), ModifierActivity.class);
                position = getPosition();
                intent.putExtra(POSITION,position);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return false;
            }
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
                    .setContentText(listAlarme.get(position).getLieu())
                    .setContentIntent(notificationPendingIntent);
            builder.setAutoCancel(true);

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(0, builder.build());
        }
    }
}
