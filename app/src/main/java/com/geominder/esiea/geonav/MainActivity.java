package com.geominder.esiea.geonav;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.SyncStateContract;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ArrayList<Alarme> listAlarme = new ArrayList<Alarme>();
    private ArrayList<Geofence> geofences = new ArrayList<Geofence>();
    private File fileAlarme;
    private PendingIntent mGeofencePendingIntent = null;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String filePath = this.getFilesDir().getPath().toString() + "/liste_alarme.txt";
        fileAlarme = new File(filePath);
        try {
            fileAlarme.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadListAlarmeFromFile(listAlarme, fileAlarme);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                        if (mLastLocation != null) {
                            LatLng myLatlng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatlng));
                        }
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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        for(Alarme alarme : listAlarme){
            if(alarme.getIsActivated() == true) {
                LatLng latLng = new LatLng(alarme.getLatitude(), alarme.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).title(alarme.getLieu().toString())
                        .snippet(alarme.getTitre()));
            }
        }
    }

    public void ajouterAction(View v){
        Intent intent = new Intent(this, AjouterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void gererAction(View v){
        Intent intent = new Intent(this, GererActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void saveListAlarmeToFile(ArrayList<Alarme> listAlarme, File file) {
        try{
            ObjectOutputStream dataOut = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));

            for (Alarme alarme : listAlarme)
                dataOut.writeObject(alarme);

            dataOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadListAlarmeFromFile(ArrayList<Alarme> listAlarme, File file) {
        try {
            ObjectInputStream dataIn = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));

            Alarme alarme;
            try {

                while ((alarme = (Alarme) dataIn.readObject()) != null)
                    listAlarme.add(alarme);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            dataIn.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
}
